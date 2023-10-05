package de.unisaarland.cs.se.selab.phases

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
            // only go through the list of bases if more vehicles need to be requested
            for (baseID in request.baseIDsToVisit) if (request.requiredVehicles.isNotEmpty()) {
                val base = dataHolder.bases.first { it.baseID == baseID }
                val assignableVehicles = allocationHelper.getAssignableAssets(base, emergency)
                val normalVehicles = allocationHelper.getNormalVehicles(assignableVehicles).sortedBy { it.id }
                val specialVehicles = allocationHelper.getSpecialVehicles(assignableVehicles).sortedBy { it.id }

                allocationHelper.assignWithoutCapacity(normalVehicles, emergency)
                allocationHelper.assignBasedOnCapacity(specialVehicles, emergency)
            } else {
                break
            }

            // log request failing
            if (request.requiredVehicles.isNotEmpty()) {
                Log.displayRequestFailed(request.emergencyID)
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
}
