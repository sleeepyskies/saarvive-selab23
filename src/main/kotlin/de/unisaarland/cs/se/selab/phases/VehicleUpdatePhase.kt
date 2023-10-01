package de.unisaarland.cs.se.selab.phases

import de.unisaarland.cs.se.selab.dataClasses.emergencies.EmergencyStatus
import de.unisaarland.cs.se.selab.dataClasses.vehicles.Ambulance
import de.unisaarland.cs.se.selab.dataClasses.vehicles.FireTruckWater
import de.unisaarland.cs.se.selab.dataClasses.vehicles.PoliceCar
import de.unisaarland.cs.se.selab.dataClasses.vehicles.Vehicle
import de.unisaarland.cs.se.selab.dataClasses.vehicles.VehicleStatus
import de.unisaarland.cs.se.selab.global.Log
import de.unisaarland.cs.se.selab.global.Number
import de.unisaarland.cs.se.selab.simulation.DataHolder
import kotlin.math.ceil
import kotlin.math.max

/**
 * This phase is responsible for moving all active vehicles,
 * checks if vehicles have reached their emergency,
 * and updates both vehicle and emergency statuses
 */
class VehicleUpdatePhase(private val dataHolder: DataHolder) : Phase {

    /**
     * The main execute method of the phase.
     */
    override fun execute() {
        // update each recharging vehicle
        for (vehicle in dataHolder.rechargingVehicles) {
            updateRecharging(vehicle)
        }
        // update each active vehicle position
        for (vehicle in dataHolder.activeVehicles) {
            updateVehiclePosition(vehicle)
            updateRoadEndReached(vehicle)
            updateRouteEndReached(vehicle)
        }
    }

    /**
     * Updates a recharging vehicle
     */
    private fun updateRecharging(vehicle: Vehicle) {
        vehicle.ticksStillUnavailable -= 1
        if (vehicle.ticksStillUnavailable == 0) {
            dataHolder.rechargingVehicles.remove(vehicle)
            vehicle.vehicleStatus = VehicleStatus.IN_BASE
            dataHolder.activeVehicles.add(vehicle) // Add it back to active vehicles when finish recharge
        }
    }

    /**
     * Moves a vehicle along its current route, and checks whether the vehicle
     * has reached the end of a road or route.
     */
    private fun updateVehiclePosition(vehicle: Vehicle) {
        // move vehicle 1 tick along road
        vehicle.roadProgress = max(0, vehicle.roadProgress - 1) // ensures no negative road progress
    }

    /**
     * Checks if a vehicle has reached the end of a road and update it accordingly
     */
    private fun updateRoadEndReached(vehicle: Vehicle) {
        if (vehicle.roadProgress == 0) {
            // assign new route without first vertex
            vehicle.currentRoute = vehicle.currentRoute.drop(1)
            vehicle.lastVisitedVertex = vehicle.currentRoute[0]
        }
    }

    /**
     * Checks if a vehicle has reached the end of its route and updates accordingly
     */
    private fun updateRouteEndReached(vehicle: Vehicle) {
        if (vehicle.currentRoute.isEmpty()) {
            // check if vehicle is moving to emergency or to base
            if (vehicle.vehicleStatus == VehicleStatus.MOVING_TO_EMERGENCY) {
                updateReachedEmergency(vehicle)
            } else if (vehicle.vehicleStatus == VehicleStatus.MOVING_TO_BASE) {
                updateReachedBase(vehicle)
            }
        } else {
            // assign new roadProgress if route end not reached
            vehicle.roadProgress =
                vehicle.lastVisitedVertex.connectingRoads[vehicle.currentRoute[1].id]
                    ?.weight
                    ?.let { weightToTicks(it) }
                    ?: 0/* Default value or action when null */
        }
    }

    /**
     * Updates the vehicle if it has reached an emergency
     */
    private fun updateReachedEmergency(vehicle: Vehicle) {
        vehicle.vehicleStatus = VehicleStatus.ARRIVED
        // log vehicle arrival
        Log.displayAssetArrival(vehicle.id, vehicle.lastVisitedVertex.id)
        // add vehicle to emergency's list of vehicles
        dataHolder.emergencyToVehicles[vehicle.assignedEmergencyID]?.add(vehicle)

        // update the emergency's required vehicles
        val requiredVehicles = dataHolder.vehicleToEmergency[vehicle.id]?.requiredVehicles ?: mutableMapOf()
        requiredVehicles[vehicle.vehicleType] = requiredVehicles[vehicle.vehicleType]?.minus(1) ?: 0
        if (requiredVehicles[vehicle.vehicleType] == 0) {
            requiredVehicles.remove(vehicle.vehicleType)
        }

        // check if all vehicles have reached emergency, if so change status to HANDLING
        if (dataHolder.vehicleToEmergency[vehicle.id]?.requiredCapacity?.isEmpty() == true) {
            dataHolder.vehicleToEmergency[vehicle.id]?.emergencyStatus = EmergencyStatus.HANDLING
            // Log emergency handling TODO Might have to be in EmergencyUpdatePhase...
            dataHolder.vehicleToEmergency[vehicle.id]?.let { Log.displayEmergencyHandlingStart(it.id) }
            // Update all vehicle statuses to HANDLING
            val vehicles = dataHolder.emergencyToVehicles[dataHolder.vehicleToEmergency[vehicle.id]?.id]
            if (vehicles != null) {
                for (v in vehicles) {
                    v.vehicleStatus = VehicleStatus.HANDLING
                }
            }
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
            dataHolder.rechargingVehicles.add(vehicle)
            vehicle.vehicleStatus = VehicleStatus.RECHARGING
            vehicle.currentCriminalCapcity = 0
            vehicle.ticksStillUnavailable = 2
        } else if (vehicle is FireTruckWater && vehicle.currentWaterCapacity < vehicle.maxWaterCapacity) {
            dataHolder.rechargingVehicles.add(vehicle)
            vehicle.vehicleStatus = VehicleStatus.RECHARGING
            vehicle.currentWaterCapacity = vehicle.maxWaterCapacity
            vehicle.ticksStillUnavailable =
                ceil((vehicle.maxWaterCapacity - vehicle.currentWaterCapacity) / Number.THREE_HUNDRED_FLOAT).toInt()
        } else if (vehicle is Ambulance && vehicle.hasPatient) {
            dataHolder.rechargingVehicles.add(vehicle)
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
