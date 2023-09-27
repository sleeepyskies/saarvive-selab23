package de.unisaarland.cs.se.selab.phases

import de.unisaarland.cs.se.selab.dataClasses.emergencies.Emergency
import de.unisaarland.cs.se.selab.dataClasses.emergencies.EmergencyStatus
import de.unisaarland.cs.se.selab.dataClasses.vehicles.Vehicle
import de.unisaarland.cs.se.selab.dataClasses.vehicles.VehicleStatus
import de.unisaarland.cs.se.selab.global.Number
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
     * Checks if emergencies have failed or become resolved and updates accordingly
     */
    private fun updateEmergencies(emergencies: List<Emergency>) {
        for (emergency in emergencies) {
            // emergency resolved
            if (emergency.handleTime == 0) {
                emergency.emergencyStatus = EmergencyStatus.RESOLVED
                // update mappings
                dataHolder.ongoingEmergencies.remove(emergency)
                dataHolder.resolvedEmergencies.add(emergency)
            }
            // emergency failed
            if (emergency.maxDuration == 0) {
                emergency.emergencyStatus = EmergencyStatus.FAILED
                // update mappings
                dataHolder.ongoingEmergencies.remove(emergency)
                dataHolder.resolvedEmergencies.add(emergency)
            }
        }

        /**
         * Returns the weight as ticks need to travel
         */
        fun weightToTicks(weight: Int): Int {
            if (weight < Number.TEN) return 1
            return if (weight % Number.TEN == 0) {
                weight // number is already a multiple of ten
            } else {
                weight + (Number.TEN - weight % Number.TEN) // round up
            }
        }

        /**
         * Sends all the vehicles assigned to an emergency back to their
         * respective bases
         */
        fun sendVehiclesBack(vehicles: List<Vehicle>) {
            for (vehicle in vehicles) {
                vehicle.vehicleStatus = VehicleStatus.MOVING_TO_BASE
                vehicle.assignedEmergencyID = null
                vehicle.currentRoute =
                    dataHolder.graph.calculateShortestRoute(
                        vehicle.lastVisitedVertex,
                        dataHolder.baseToVertex[vehicle.assignedBaseID]!!,
                        vehicle.height
                    )
                vehicle.roadProgress =
                    weightToTicks(vehicle.lastVisitedVertex.connectingRoads[vehicle.currentRoute[1]]!!.weight)
            }
        }
    }
}
