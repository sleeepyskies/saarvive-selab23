package de.unisaarland.cs.se.selab.phases

import de.unisaarland.cs.se.selab.dataClasses.emergencies.Emergency
import de.unisaarland.cs.se.selab.dataClasses.emergencies.EmergencyStatus
import de.unisaarland.cs.se.selab.dataClasses.vehicles.Vehicle
import de.unisaarland.cs.se.selab.dataClasses.vehicles.VehicleStatus
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
    }

    /**
     * Sends all the vehicles assigned to an emergency back to their
     * respective bases
     */
    private fun sendVehiclesBack(vehicles: List<Vehicle>) {
        for (vehicle in vehicles) {
            vehicle.vehicleStatus = VehicleStatus.MOVING_TO_BASE
            vehicle.assignedEmergencyID = null
            vehicle.roadProgress = 1
            vehicle.currentRoute = dataHolder.graph.calculateShortestRoute(vehicle.lastVisitedVertex, dataHolder.baseToVertex[vehicle.assignedBaseID]!!, vehicle.height)

        }
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
}
