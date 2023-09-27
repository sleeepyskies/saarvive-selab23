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
     * Moves a vehicle along its current route, and checks whether the vehicle
     * has reached the end of a road or route.
     */
    private fun updateVehiclePosition(vehicle: Vehicle) {
        // move vehicle 1 tick along road
        vehicle.roadProgress -= 1

        // check if vehicle has reached the end of a road
        if (vehicle.roadProgress == 0) {
            vehicle.currentRoute.removeAt(0)
            vehicle.lastVisitedVertex = vehicle.currentRoute[0]

            // check if vehicle has reached its destination
            if (vehicle.currentRoute.size == 0) {
                // check if vehicle is moving to emergency or to base
                if (vehicle.vehicleStatus == VehicleStatus.MOVING_TO_EMERGENCY) {
                    vehicle.vehicleStatus = VehicleStatus.ARRIVED
                    dataHolder.emergencyToVehicles[vehicle.assignedEmergencyID]!!.add(vehicle)
                    val requiredVehicles = dataHolder.vehicleToEmergency[vehicle.id]!!.requiredVehicles
                    requiredVehicles[vehicle.vehicleType] = requiredVehicles[vehicle.vehicleType]!! - 1
                    if (requiredVehicles[vehicle.vehicleType] == 0) {
                        requiredVehicles.remove(vehicle.vehicleType)
                    }
                } else if (vehicle.vehicleStatus == VehicleStatus.MOVING_TO_BASE) {
                    // check if vehicle needs to recharge
                    dataHolder.activeVehicles.remove(vehicle)

                }
            } else {
                // assign new roadProgress
                vehicle.roadProgress = roundTo10(vehicle.lastVisitedVertex.connectingRoads[vehicle.currentRoute[1]]!!.weight)
            }
        }
    }

    /**
     * Returns the number rounded up to the nearest 10
     */
    private fun roundTo10 (number: Int): Int {
        return if (number % 10 == 0) {
            number // number is already a multiple of ten
        } else {
            number + (10 - number % 10) // round up
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
}