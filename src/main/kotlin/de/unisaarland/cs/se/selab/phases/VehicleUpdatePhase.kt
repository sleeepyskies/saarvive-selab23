package de.unisaarland.cs.se.selab.phases

import de.unisaarland.cs.se.selab.dataClasses.emergencies.EmergencyStatus
import de.unisaarland.cs.se.selab.dataClasses.vehicles.*
import de.unisaarland.cs.se.selab.global.Number
import de.unisaarland.cs.se.selab.simulation.DataHolder
import kotlin.math.ceil

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
            // assign new roadProgress if route end not reached
            vehicle.roadProgress =
                weightToTicks(vehicle.lastVisitedVertex.connectingRoads[vehicle.currentRoute[1]]!!.weight)
        }
    }

    /**
     * Updates the vehicle if it has reached an emergency
     */
    private fun updateReachedEmergency(vehicle: Vehicle) {
        vehicle.vehicleStatus = VehicleStatus.ARRIVED
        // add vehicle to emergency's list of vehicles
        dataHolder.emergencyToVehicles[vehicle.assignedEmergencyID]!!.add(vehicle)

        // update the emergency's required vehicles
        val requiredVehicles = dataHolder.vehicleToEmergency[vehicle.id]!!.requiredVehicles
        requiredVehicles[vehicle.vehicleType] = requiredVehicles[vehicle.vehicleType]!! - 1
        if (requiredVehicles[vehicle.vehicleType] == 0) {
            requiredVehicles.remove(vehicle.vehicleType)
        }

        // check if all vehicles have reached emergency, if so change status to HANDLING
        if (dataHolder.vehicleToEmergency[vehicle.id]!!.requiredCapacity == mutableMapOf<CapacityType, Int>()) {
            dataHolder.vehicleToEmergency[vehicle.id]!!.emergencyStatus = EmergencyStatus.HANDLING
        }
    }

    /**
     * Updates the vehicle if it has reached the base.
     * Also checks if vehicle needs to recharge
     */
    private fun updateReachedBase(vehicle: Vehicle) {
        dataHolder.activeVehicles.remove(vehicle)
        // check if vehicle needs to recharge
        if (vehicle is PoliceCar && vehicle.currentCriminalCapcity > 0) {
            vehicle.vehicleStatus = VehicleStatus.RECHARGING
            vehicle.currentCriminalCapcity = 0
            vehicle.ticksStillUnavailable = 2
        } else if (vehicle is FireTruckWater && vehicle.currentWaterCapacity < vehicle.maxWaterCapacity) {
            vehicle.vehicleStatus = VehicleStatus.RECHARGING
            vehicle.currentWaterCapacity = vehicle.maxWaterCapacity
            vehicle.ticksStillUnavailable =
                ceil((vehicle.maxWaterCapacity - vehicle.currentWaterCapacity) / 300.0).toInt()
        } else if (vehicle is Ambulance && vehicle.hasPatient) {
            vehicle.hasPatient = false
            vehicle.vehicleStatus = VehicleStatus.RECHARGING
            vehicle.ticksStillUnavailable = 1
        } else {
            vehicle.vehicleStatus = VehicleStatus.IN_BASE
        }
    }

    /**
     * Returns the weight as ticks need to travel
     */
    private fun weightToTicks(weight: Int): Int {
        if (weight < Number.TEN) return 1
        return if (weight % Number.TEN == 0) {
            weight // number is already a multiple of ten
        } else {
            weight + (Number.TEN - weight % Number.TEN) // round up
        }
    }
}
