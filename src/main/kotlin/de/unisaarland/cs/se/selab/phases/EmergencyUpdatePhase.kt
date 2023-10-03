package de.unisaarland.cs.se.selab.phases

import de.unisaarland.cs.se.selab.dataClasses.emergencies.Emergency
import de.unisaarland.cs.se.selab.dataClasses.emergencies.EmergencyStatus
import de.unisaarland.cs.se.selab.dataClasses.vehicles.Vehicle
import de.unisaarland.cs.se.selab.dataClasses.vehicles.VehicleStatus
import de.unisaarland.cs.se.selab.global.Log
import de.unisaarland.cs.se.selab.simulation.DataHolder

/**
 * This phase is responsible for updating all ongoing emergencies in the simulation
 */
class EmergencyUpdatePhase(private val dataHolder: DataHolder) : Phase {
    /**
     * The main execute method of the EmergencyUpdatePhase
     */
    override fun execute() {
        reduceMaxDuration(dataHolder.ongoingEmergencies)
        reduceHandleTime(
            dataHolder.ongoingEmergencies.filter { emergency: Emergency ->
                emergency.emergencyStatus == EmergencyStatus.HANDLING
            }
        )
        updateEmergencies(dataHolder.ongoingEmergencies)
    }

    /**
     * Reduces the max duration of all ongoing emergencies
     */
    private fun reduceMaxDuration(emergencies: List<Emergency>) {
        for (emergency in emergencies) {
            emergency.maxDuration -= 1
        }
    }

    /**
     * Reduces the handle time of all emergencies being handled
     */
    private fun reduceHandleTime(emergencies: List<Emergency>) {
        for (emergency in emergencies) {
            emergency.handleTime -= 1
        }
    }

    /**
     * Sends all the vehicles assigned to an emergency back to their
     * respective bases
     */
    private fun sendVehiclesBack(vehicles: MutableList<Vehicle>) {
        for (vehicle in vehicles) {
            vehicle.vehicleStatus = VehicleStatus.MOVING_TO_BASE
            vehicle.assignedEmergencyID = null
            dataHolder.baseToVertex[vehicle.assignedBaseID]?.let { baseVertex ->
                vehicle.currentRoute = dataHolder.graph.calculateShortestRoute(
                    vehicle.lastVisitedVertex,
                    baseVertex,
                    vehicle.height
                )
            }
            vehicle.remainingRouteWeight =
                dataHolder.graph.weightOfRoute(
                    vehicle.currentRoute.first(),
                    vehicle.currentRoute.last(),
                    vehicle.height
                )
            vehicle.currentRoad = vehicle.currentRoute.first().connectingRoads[vehicle.currentRoute[2].id]
            vehicle.weightTillLastVisitedVertex = 0
            vehicle.lastVisitedVertex = vehicle.currentRoute.first()
            vehicle.currentRouteWeightProgress = 0
        }
    }

    /**
     * Checks if emergencies have failed or become resolved and updates accordingly
     */
    private fun updateEmergencies(emergencies: List<Emergency>) {
        val removeEmergencies: MutableList<Emergency> = mutableListOf()
        for (emergency in emergencies) {
            // emergency resolved
            if (emergency.handleTime == 0) {
                emergency.emergencyStatus = EmergencyStatus.RESOLVED
                // Log emergency resolved
                Log.displayEmergencyResolved(emergency.id)
                removeEmergencies.add(emergency)
                dataHolder.resolvedEmergencies.add(emergency)
                dataHolder.emergencyToVehicles[emergency.id]?.let { sendVehiclesBack(it) }
            }
            // emergency failed
            if (emergency.maxDuration == 0) {
                emergency.emergencyStatus = EmergencyStatus.FAILED
                Log.displayEmergencyFailed(emergency.id)
                removeEmergencies.add(emergency)
                dataHolder.resolvedEmergencies.add(emergency)
                dataHolder.emergencyToVehicles[emergency.id]?.let { sendVehiclesBack(it) }
            }
        }
        dataHolder.ongoingEmergencies.removeAll(removeEmergencies)
    }
}
