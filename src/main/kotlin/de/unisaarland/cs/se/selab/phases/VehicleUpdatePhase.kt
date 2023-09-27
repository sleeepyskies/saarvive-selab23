package de.unisaarland.cs.se.selab.phases

import de.unisaarland.cs.se.selab.dataClasses.emergencies.Emergency
import de.unisaarland.cs.se.selab.dataClasses.emergencies.EmergencyStatus
import de.unisaarland.cs.se.selab.dataClasses.vehicles.Vehicle
import de.unisaarland.cs.se.selab.dataClasses.vehicles.VehicleStatus
import de.unisaarland.cs.se.selab.graph.Vertex
import de.unisaarland.cs.se.selab.simulation.DataHolder

/**
 * This phase is responsible for moving all active vehicles,
 * checks if vehicles have reached their emergency,
 * and updates both vehicle and emergency statuses
 */
class VehicleUpdatePhase(private val dataHolder: DataHolder) {

    /**
     * The main execute method of the phase.
     */
    public fun execute() {

    }

    /**
     * Moves a vehicle along its current route
     */
    private fun updateVehiclePosition(vehicle: Vehicle) {
        // move vehicle 1 tick along road
        vehicle.roadProgress -= 1

        // check if vehicle has reached the end of a road
        if (vehicle.roadProgress == 0) {
            // check if vehicle has reached the end of its route
            if (vehicle.currentRoute.size == 1) {
                vehicle.status = VehicleStatus.ARRIVED
            } else {
                vehicle.lastVisitedVertex = vehicle.currentRoute[0]
                vehicle.currentRoute.removeAt(0)
            }
        }
    }

    /**
     * Checks if a vehicle has reached it's assigned emergency.
     */
    private fun checkVehicleReachedEmergency(vehicle: Vehicle): Boolean {

    }

    /**
     * Updates the given emergency's status
     */
    private fun updateEmergencyStatus(emergency: Emergency, status: EmergencyStatus) {

    }

    /**
     * Changes the vehicles
     */
    private fun updateVehicleAvailability(vehicle: Vehicle) {

    }
}