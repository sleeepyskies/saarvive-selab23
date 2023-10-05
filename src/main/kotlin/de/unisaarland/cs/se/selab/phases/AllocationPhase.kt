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
                sortAndAssign(assignableVehicles, emergency)

                if (emergency.requiredVehicles.isNotEmpty()) {
                    val reallocatableVehicles = getReallocatableVehicles(base, emergency)
                    sortAndAssign(reallocatableVehicles, emergency)
                }

                if (emergency.requiredVehicles.isNotEmpty()) {
                    sortForRequests(emergency, base)
                }
            }
        }
    }

    private fun sortAndAssign(vehicles: List<Vehicle>, emergency: Emergency) {
        val normalVehicles = allocationHelper.getNormalVehicles(vehicles).sortedBy { it.id }
        val specialVehicles = allocationHelper.getSpecialVehicles(vehicles).sortedBy { it.id }

        allocationHelper.assignWithoutCapacity(normalVehicles, emergency)
        allocationHelper.assignBasedOnCapacity(specialVehicles, emergency)
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

    private fun createRequest(emergency: Emergency, base: Base, requiredEmergencyType: EmergencyType) {
        val basesToVisit = getBasesByProximity(requiredEmergencyType, base)
        val baseIds = basesToVisit.map { it.baseID }
        val requiredVehicles = emergency.requiredVehicles
        // if there are no bases on the map that we can visit we can't create the request
        if (baseIds.isEmpty()) return
        val request = Request(baseIds, emergency.id, dataHolder.requestID, requiredVehicles, emergency.requiredCapacity)
        dataHolder.requests.add(request)
        Log.displayAssetRequest(emergency.id, request.baseIDsToVisit.first(), dataHolder.requestID)
        dataHolder.requestID++
    }

    /**
     * checks which types of bases the request needs to be sent to
     */
    private fun sortForRequests(emergency: Emergency, base: Base) {
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

        if (policeStationVehicles.isNotEmpty()) createRequest(emergency, base, EmergencyType.CRIME)
        if (hospitalVehicles.isNotEmpty()) createRequest(emergency, base, EmergencyType.MEDICAL)
        if (fireStationVehicles.isNotEmpty()) createRequest(emergency, base, EmergencyType.FIRE)
    }
}
