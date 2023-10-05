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
        if (dataHolder.rechargingVehicles.isNotEmpty()) {
            dataHolder.rechargingVehicles.forEach { vehicle -> updateRecharging(vehicle) }
        }
        // update each active vehicle position
        if (dataHolder.activeVehicles.isNotEmpty()) {
            // update all moving vehicles, exclude only assigned ones
            val movingVehicles =
                dataHolder.activeVehicles.filter {
                    it.vehicleStatus == VehicleStatus.MOVING_TO_BASE ||
                        it.vehicleStatus == VehicleStatus.MOVING_TO_EMERGENCY
                }
            movingVehicles.forEach { vehicle -> updateVehiclePosition(vehicle) }
            // convert assigned to emergency vehicles to moving to emergency vehicles
            val stagedVehicles =
                dataHolder.activeVehicles.filter { it.vehicleStatus == VehicleStatus.ASSIGNED_TO_EMERGENCY }
            stagedVehicles.forEach { it.vehicleStatus = VehicleStatus.MOVING_TO_EMERGENCY }
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
        }
    }

    /**
     * Moves a vehicle along its current route, and checks whether the vehicle
     * has reached the end of a road or route.
     */
    private fun updateVehiclePosition(vehicle: Vehicle) {
        // decrease remainingRouteWeight by 10
        vehicle.remainingRouteWeight =
            max(0, vehicle.remainingRouteWeight - Number.TEN) // ensures no negative road progress
        // increase currentRouteProgress by 10
        vehicle.currentRouteWeightProgress += Number.TEN

        // check if destination has been reached
        if (vehicle.remainingRouteWeight <= 0) {
            updateRouteEndReached(vehicle)
        } // check if a vertex has been crossed
        else if (
            vehicle.weightTillLastVisitedVertex +
            (vehicle.currentRoad?.weight ?: 0) <= vehicle.currentRouteWeightProgress
        ) {
            updateRoadEndReached(vehicle)
        }
    }

    /**
     * Is called if a vehicle has reached/passed a vertex on its route,
     * updates accordingly
     */
    private fun updateRoadEndReached(vehicle: Vehicle) {
        vehicle.weightTillLastVisitedVertex += vehicle.currentRoad?.weight ?: 0
        // updates the current road in vehicle
        vehicle.currentRoad = vehicle.currentRoute[0].connectingRoads[1]
        vehicle.lastVisitedVertex = vehicle.currentRoute[0]
        // removes the passed vertex from vehicles route
        vehicle.currentRoute = vehicle.currentRoute.drop(1)
    }

    /**
     * Is called if a vehicle has reached the end of its route,
     * updates accordingly
     */
    private fun updateRouteEndReached(vehicle: Vehicle) {
        if (vehicle.currentRoute.size > 1) {
            vehicle.currentRoute = vehicle.currentRoute.drop(1)
        }
        vehicle.lastVisitedVertex = vehicle.currentRoute[0]
        if (vehicle.currentRoute.size == 1) {
            if (vehicle.vehicleStatus == VehicleStatus.MOVING_TO_EMERGENCY) {
                updateReachedEmergency(vehicle)
            } else if (vehicle.vehicleStatus == VehicleStatus.MOVING_TO_BASE) {
                updateReachedBase(vehicle)
            }
        }
    }

    /**
     * Updates the vehicle if it has reached an emergency
     */
    private fun updateReachedEmergency(vehicle: Vehicle) {
        val emergency = dataHolder.vehicleToEmergency[vehicle.id] ?: return
        if (emergency.emergencyStatus != EmergencyStatus.ONGOING) return

        vehicle.vehicleStatus = VehicleStatus.ARRIVED
        Log.displayAssetArrival(vehicle.id, vehicle.lastVisitedVertex.id)
        val vehicleList = dataHolder.emergencyToVehicles.getOrDefault(emergency.id, mutableListOf())
        vehicleList.add(vehicle)

        /* updateRequiredVehicles(vehicle, emergency)

        if (emergency.requiredCapacity.isEmpty()) {
            handleAllVehiclesReached(emergency)
        } */
    }

    /*
    private fun updateRequiredVehicles(vehicle: Vehicle, emergency: Emergency) {
        val requiredVehicles = emergency.requiredVehicles
        val vehicleTypeCount = requiredVehicles[vehicle.vehicleType] ?: return

        if (vehicleTypeCount == 1) {
            requiredVehicles.remove(vehicle.vehicleType)
        } else {
            requiredVehicles[vehicle.vehicleType] = vehicleTypeCount - 1
        }
    }

    private fun handleAllVehiclesReached(emergency: Emergency) {
        emergency.emergencyStatus = EmergencyStatus.HANDLING
        Log.displayEmergencyHandlingStart(emergency.id)

        dataHolder.emergencyToVehicles[emergency.id]?.forEach {
            it.vehicleStatus = VehicleStatus.HANDLING
        }
    }
    */

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
                // ------------------------------------T0DO Remove float-------------------------------------------
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
}
