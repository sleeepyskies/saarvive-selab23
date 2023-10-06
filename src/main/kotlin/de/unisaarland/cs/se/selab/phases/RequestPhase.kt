package de.unisaarland.cs.se.selab.phases

import de.unisaarland.cs.se.selab.dataClasses.Request
import de.unisaarland.cs.se.selab.dataClasses.emergencies.Emergency
import de.unisaarland.cs.se.selab.dataClasses.vehicles.VehicleType
import de.unisaarland.cs.se.selab.global.Log
import de.unisaarland.cs.se.selab.simulation.DataHolder

/**
 * deals with all requests created in the current tick
 */
class RequestPhase(private val dataHolder: DataHolder) : Phase {
    /**
     * executes all the private functions based on logic
     */

    val allocationHelper = AllocationHelper(dataHolder)

    override fun execute() {
        while (requestExists()) {
            val request = dataHolder.requests.first()
            val emergency = dataHolder.ongoingEmergencies.first { it.id == request.emergencyID }
            val baseID = request.baseIDsToVisit.first()
            val base = dataHolder.bases.first { it.baseID == baseID }
            val assignableVehicles = allocationHelper.getAssignableAssets(base, emergency)

            // quick fix
            val sortedAssignableAssets = assignableVehicles.sortedBy { it.id }
            for (vehicle in sortedAssignableAssets) {
                if (allocationHelper.isNormalVehicle(vehicle)) {
                    allocationHelper.assignWithoutCapacity(vehicle, emergency, false)
                } else { allocationHelper.assignBasedOnCapacity(vehicle, emergency, false) }
            }

            // filters the vehicles for this request that are still in the emergency
            val leftOverVehicles = emergency.requiredVehicles.filter { it.key in request.requiredVehicles.keys }
            // creates a new request to the next base in the list
            // quick fix changed 0 to 1
            if (leftOverVehicles.isNotEmpty() && request.baseIDsToVisit.size > 1) {
                // the original list of bases minus the first one
                val newBaseIDsToVisit = request.baseIDsToVisit.drop(1)
                createRequest(emergency, newBaseIDsToVisit, leftOverVehicles)
            } else {
                // gets rid of always displaying request failure
                if (leftOverVehicles.isNotEmpty()) Log.displayRequestFailed(request.emergencyID)
            }

            dataHolder.requests.remove(request)
        }
    }

    /**
     * check if there are any requests in this tick
     */
    fun requestExists(): Boolean {
        return dataHolder.requests.isNotEmpty()
    }

    private fun createRequest(emergency: Emergency, baseList: List<Int>, requiredVehicles: Map<VehicleType, Int>) {
        val request = Request(
            baseList,
            emergency.id,
            dataHolder.requestID,
            requiredVehicles.toMutableMap(),
            emergency.requiredCapacity
        )
        Log.displayAssetRequest(emergency.id, baseList.first(), dataHolder.requestID)
        dataHolder.requests.add(request)
        dataHolder.requestID++
    }
}
