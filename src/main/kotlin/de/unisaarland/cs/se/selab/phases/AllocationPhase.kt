package de.unisaarland.cs.se.selab.phases

import de.unisaarland.cs.se.selab.dataClasses.Request
import de.unisaarland.cs.se.selab.dataClasses.bases.Base
import de.unisaarland.cs.se.selab.dataClasses.emergencies.Emergency
import de.unisaarland.cs.se.selab.dataClasses.emergencies.EmergencyStatus
import de.unisaarland.cs.se.selab.dataClasses.emergencies.EmergencyType
import de.unisaarland.cs.se.selab.dataClasses.vehicles.Vehicle
import de.unisaarland.cs.se.selab.dataClasses.vehicles.VehicleStatus
import de.unisaarland.cs.se.selab.dataClasses.vehicles.VehicleType
import de.unisaarland.cs.se.selab.global.Log
import de.unisaarland.cs.se.selab.simulation.DataHolder

/** Represents the allocation phase of the simulation
 *
 */
class AllocationPhase(private val dataHolder: DataHolder) : Phase {
    // private var nextRequestId = -1
    private val allocationHelper = AllocationHelper(dataHolder)

    /**
     * Executes the allocation phase
     */
    override fun execute() {
        for (emergency in dataHolder.ongoingEmergencies) {
            if (emergency.emergencyStatus == EmergencyStatus.ASSIGNED ||
                emergency.emergencyStatus == EmergencyStatus.ONGOING
            ) {
                val base = dataHolder.emergencyToBase[emergency.id] ?: Base(1, 1, 1, mutableListOf())

                val assignableVehicles = allocationHelper.getAssignableAssets(base, emergency)
                // quick fix -- use boolean to solve only logging allocation
                sortAndAssign(assignableVehicles, emergency, false)

                if (emergency.requiredVehicles.isNotEmpty()) {
                    val reallocatableVehicles = getReallocatableVehicles(base, emergency)
                    sortAndAssign(reallocatableVehicles, emergency, true)
                }

                if (emergency.requiredVehicles.isNotEmpty()) {
                    sortAndAssignRequests(emergency, base)
                }
            }
        }
    }

    private fun sortAndAssign(vehicles: List<Vehicle>, emergency: Emergency, isReallocation: Boolean) {
        val getVehicles = mutableListOf<Vehicle>()
        getVehicles.addAll(vehicles.sortedBy { it.id })
        for (vehicle in getVehicles) {
            if (allocationHelper.isNormalVehicle(vehicle)) {
                allocationHelper.assignWithoutCapacity(vehicle, emergency, isReallocation)
            } else { allocationHelper.assignBasedOnCapacity(vehicle, emergency, isReallocation) }
        }
    }

    private fun getReallocatableVehicles(base: Base, emergency: Emergency): List<Vehicle> {
        val neededTypes = emergency.requiredVehicles.keys
        // get all vehicles that are assigned to the emergency or are moving to the emergency
        val activeVehicles =
            base.vehicles.filter {
                it.vehicleStatus == VehicleStatus.ASSIGNED_TO_EMERGENCY ||
                    it.vehicleStatus == VehicleStatus.MOVING_TO_BASE ||
                    it.vehicleStatus == VehicleStatus.MOVING_TO_EMERGENCY
            }
        return activeVehicles.filter {
            it.vehicleType in neededTypes &&
                it.assignedEmergencyID != emergency.id &&
                (dataHolder.vehicleToEmergency[it.id]?.severity ?: 0) < emergency.severity && it.isAvailable
        }
    }

    private fun getBasesByProximity(requiredEmergencyType: EmergencyType, base: Base): List<Base> {
        val baseVertex = dataHolder.baseToVertex
        val allBases = dataHolder.bases

        return dataHolder.graph.findClosestBasesByProximity(requiredEmergencyType, base, allBases, baseVertex)
    }

    private fun createRequest(
        emergency: Emergency,
        base: Base,
        requiredEmergencyType: EmergencyType,
        requiredVehicles: Map<VehicleType, Int>,
        requestList: MutableList<Request>
    ) {
        val basesToVisit = getBasesByProximity(requiredEmergencyType, base)
        val baseIds = basesToVisit.map { it.baseID }
        // use the one provided by sort for request
        // val requiredVehicles = emergency.requiredVehicles
        // if there are no bases on the map that we can visit we can't create the request
        if (baseIds.isEmpty()) return
        val request = Request(
            baseIds,
            emergency.id,
            null,
            requiredVehicles.toMutableMap(),
            emergency.requiredCapacity
        )
        requestList.add(request)
    }

    /**
     * checks which types of bases the request needs to be sent to
     */
    private fun sortAndAssignRequests(emergency: Emergency, base: Base) {
        val requestList: MutableList<Request> = mutableListOf()
        val policeStationVehicles = emergency.requiredVehicles.filter {
            it.key == VehicleType.K9_POLICE_CAR ||
                it.key == VehicleType.POLICE_MOTORCYCLE || it.key == VehicleType.POLICE_CAR
        }
        val hospitalVehicles = emergency.requiredVehicles.filter {
            it.key == VehicleType.AMBULANCE ||
                it.key == VehicleType.EMERGENCY_DOCTOR_CAR
        }
        val fireStationVehicles = emergency.requiredVehicles.filter {
            it.key == VehicleType.FIRE_TRUCK_TECHNICAL ||
                it.key == VehicleType.FIREFIGHTER_TRANSPORTER || it.key == VehicleType.FIRE_TRUCK_LADDER ||
                it.key == VehicleType.FIRE_TRUCK_WATER
        }

        if (policeStationVehicles.isNotEmpty()) {
            createRequest(
                emergency,
                base,
                EmergencyType.CRIME,
                policeStationVehicles,
                requestList
            )
        }
        if (hospitalVehicles.isNotEmpty()) {
            createRequest(
                emergency,
                base,
                EmergencyType.MEDICAL,
                hospitalVehicles,
                requestList
            )
        }
        if (fireStationVehicles.isNotEmpty()) {
            createRequest(
                emergency,
                base,
                EmergencyType.FIRE,
                fireStationVehicles,
                requestList
            )
        }

        val requestSorted = requestList.sortedBy { it.baseIDsToVisit[0] }
        for (request in requestSorted) {
            Log.displayAssetRequest(emergency.id, request.baseIDsToVisit.first(), dataHolder.requestID)
            dataHolder.requests.add(request)
            dataHolder.requestID++
        }
    }
}
