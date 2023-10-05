package de.unisaarland.cs.se.selab.phases

import de.unisaarland.cs.se.selab.dataClasses.Request
import de.unisaarland.cs.se.selab.dataClasses.bases.Base
import de.unisaarland.cs.se.selab.dataClasses.emergencies.Emergency
import de.unisaarland.cs.se.selab.dataClasses.emergencies.EmergencyStatus
import de.unisaarland.cs.se.selab.dataClasses.vehicles.Vehicle
import de.unisaarland.cs.se.selab.dataClasses.vehicles.VehicleStatus
import de.unisaarland.cs.se.selab.global.Log
import de.unisaarland.cs.se.selab.simulation.DataHolder

/** Represents the allocation phase of the simulation
 *
 */
class AllocationPhase(private val dataHolder: DataHolder) : Phase {
    private var nextRequestId = -1
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
                    createRequest(emergency, base)
                }
            }
        }
    }

    private fun sortAndAssign(vehicles: List<Vehicle>, emergency: Emergency) {
        val normalVehicles = allocationHelper.getNormalVehicles(vehicles)
        val specialVehicles = allocationHelper.getSpecialVehicles(vehicles)

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

    private fun getBasesByProximity(emergency: Emergency, base: Base): List<Base> {
        val baseVertex = dataHolder.baseToVertex
        val allBases = dataHolder.bases

        return dataHolder.graph.findClosestBasesByProximity(emergency, base, allBases, baseVertex)
    }

    private fun createRequest(emergency: Emergency, base: Base) {
        val basesToVisit = getBasesByProximity(emergency, base)
        val baseIds = basesToVisit.map { it.baseID }
        val requiredVehicles = emergency.requiredVehicles
        nextRequestId++
        val request = Request(baseIds, emergency.id, nextRequestId, requiredVehicles, emergency.requiredCapacity)
        dataHolder.requests.add(request)
        Log.displayAssetRequest(emergency.id, base.baseID, nextRequestId)
    }
}
