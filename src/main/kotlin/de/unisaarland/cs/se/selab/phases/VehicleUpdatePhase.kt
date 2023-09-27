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
        // update each active vehicle position
        for (vehicle in dataHolder.activeVehicles) {
            updateVehiclePosition(vehicle)
            updateRoadEndReached(vehicle)
            updateRouteEndReached(vehicle)
        }
    }

    /**
     * Moves a vehicle along its current route, and checks whether the vehicle
     * has reached the end of a road or route.
     */
    private fun updateVehiclePosition(vehicle: Vehicle) {
        // move vehicle 1 tick along road
        vehicle.roadProgress -= 1
    }

    /**
     * Checks if a vehicle has reached the end of a road and update it accordingly
     */
    private fun updateRoadEndReached(vehicle: Vehicle) {
        if (vehicle.roadProgress == 0) {
            vehicle.currentRoute.removeAt(0)
            vehicle.lastVisitedVertex = vehicle.currentRoute[0]
        }
    }

    /**
     * Checks if a vehicle has reached the end of its route and updates accordingly
     */
    private fun updateRouteEndReached(vehicle: Vehicle) {
        if (vehicle.currentRoute.size == 0) {
            // check if vehicle is moving to emergency or to base
            if (vehicle.vehicleStatus == VehicleStatus.MOVING_TO_EMERGENCY) {
                updateReachedEmergency(vehicle)
            } else if (vehicle.vehicleStatus == VehicleStatus.MOVING_TO_BASE) {
                updateReachedBase(vehicle)
            }
        } else {
            // assign new roadProgress
            vehicle.roadProgress = weightToTicks(vehicle.lastVisitedVertex.connectingRoads[vehicle.currentRoute[1]]!!.weight)
        }
    }

    /**
     * Updates the vehicle if it has reached an emergency
     */
    private fun updateReachedEmergency(vehicle: Vehicle) {
        vehicle.vehicleStatus = VehicleStatus.ARRIVED
        dataHolder.emergencyToVehicles[vehicle.assignedEmergencyID]!!.add(vehicle)
        val requiredVehicles = dataHolder.vehicleToEmergency[vehicle.id]!!.requiredVehicles
        requiredVehicles[vehicle.vehicleType] = requiredVehicles[vehicle.vehicleType]!! - 1
        if (requiredVehicles[vehicle.vehicleType] == 0) {
            requiredVehicles.remove(vehicle.vehicleType)
        }
    }

    /**
     * Updates the vehicle if it has reached the base.
     * Also checks if vehicle needs to recharge
     */
    private fun updateReachedBase(vehicle: Vehicle) {
        // check if vehicle needs to recharge
        dataHolder.activeVehicles.remove(vehicle)
    }

    /**
     * Returns the weight as ticks need to travel
     */
    private fun weightToTicks (weight: Int): Int {
        if (weight < 10) return 1
        return if (weight % 10 == 0) {
            weight // number is already a multiple of ten
        } else {
            weight + (10 - weight % 10) // round up
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