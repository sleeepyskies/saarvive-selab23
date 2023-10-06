package de.unisaarland.cs.se.selab.phases

import de.unisaarland.cs.se.selab.dataClasses.emergencies.EmergencyStatus
import de.unisaarland.cs.se.selab.dataClasses.vehicles.Ambulance
import de.unisaarland.cs.se.selab.dataClasses.vehicles.FireTruckWater
import de.unisaarland.cs.se.selab.dataClasses.vehicles.PoliceCar
import de.unisaarland.cs.se.selab.dataClasses.vehicles.Vehicle
import de.unisaarland.cs.se.selab.dataClasses.vehicles.VehicleStatus
import de.unisaarland.cs.se.selab.global.Log
import de.unisaarland.cs.se.selab.global.Number
import de.unisaarland.cs.se.selab.graph.Road
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
//        if (dataHolder.rechargingVehicles.isNotEmpty()) {
//            dataHolder.rechargingVehicles.forEach { vehicle -> updateRecharging(vehicle) }
//        }

        while (dataHolder.rechargingVehicles.isNotEmpty()) {
            val vehicle = dataHolder.rechargingVehicles.first()
            updateRecharging(vehicle)
        }
        // update each active vehicle position
        if (dataHolder.activeVehicles.isNotEmpty()) {
            // update only moving vehicles
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
            vehicle.isAvailable = true
        }
    }

    /**
     * Moves a vehicle along its current route, and checks whether the vehicle
     * has reached the end of a road or route.
     */
    private fun updateVehiclePosition(vehicle: Vehicle) {
        // Do not need to account for events happening on vehicle current road
        /*
        // check if an event has affected the current road
        if ((vehicle.currentRoad?.baseWeight ?: 0) != (vehicle.currentRoad?.weight ?: 0)) {
            updateIfEventOccured(vehicle)
        }
        */

        // decrease remainingRouteWeight by 10
        vehicle.remainingRouteWeight =
            max(0, vehicle.remainingRouteWeight - Number.TEN) // ensures no negative road progress
        // increase currentRouteProgress by 10
        vehicle.currentRouteWeightProgress += Number.TEN

        // check if destination has been reached
        if (vehicle.remainingRouteWeight <= 0 && vehicle.currentRoute.isNotEmpty()) {
            updateRouteEndReached(vehicle)
        } // check if a vertex has been crossed
        else if (
            vehicle.weightTillLastVisitedVertex +
            (vehicle.currentRoad?.weight ?: 0) <= vehicle.currentRouteWeightProgress
        ) {
            // quick fix: doing this to avoid checking empty list, pls check sky
            // change from is not empty to size > 1 so tha that the last vertex is not removed
            if (vehicle.currentRoute.size > 1) updateRoadEndReached(vehicle)
        }
    }

    /*
    /**
     * Updates vehicle weightTillDestination if an event has occurred on its current road
     */
    private fun updateIfEventOccured(vehicle: Vehicle) {
        var vehicleEvent: Event?

        if (vehicle.currentRoad?.activeEvents?.isNotEmpty() == true) {
            vehicleEvent = vehicle.currentRoad?.activeEvents?.get(0)

            if (vehicleEvent == null) return

            val weightAlongRoad = vehicle.currentRouteWeightProgress - vehicle.weightTillLastVisitedVertex
            val weightTillNextVertex = (vehicle.currentRoad?.baseWeight ?: 0) - weightAlongRoad
            var factor: Int

            when (vehicleEvent) {
                is Construction -> factor = vehicleEvent.factor
                is TrafficJam -> factor = vehicleEvent.factor
                is RushHour -> factor = vehicleEvent.factor
                else -> return
            }

            val increasedWeightTillNextVertex = weightTillNextVertex * factor
            val weightToAdd = increasedWeightTillNextVertex - weightTillNextVertex
            vehicle.remainingRouteWeight += weightToAdd
        }
    }
    */

    /**
     * Is called if a vehicle has reached/passed a vertex on its route,
     * updates accordingly
     */
    private fun updateRoadEndReached(vehicle: Vehicle) {
        // we know that we will not cross our emergency vertex since we check first for this
        // -> we may assume that we will always have enough vertices on our rout
        // we also know that we will cross at least one vertex

        // update vehicle position for exact movement
        vehicle.remainingRouteWeight += Number.TEN
        // increase currentRouteProgress by 10
        vehicle.currentRouteWeightProgress -= Number.TEN

        // find the amount of vertices we have crossed
        // set current road
        val nextRoad: Road? = vehicle.lastVisitedVertex.connectingRoads[vehicle.currentRoute[1].id]
        var totalRoadWeight = 0
        var verticesCrossed = 0
        vehicle.currentRoad = nextRoad

        // while the sum of traveled roads(in this tick) is less than 10
        // unsure if < or <=
        // quick fix: not sure if > 1 or > 0
        while (totalRoadWeight < Number.TEN && vehicle.currentRoute.size > 1) {
            // add the weight until next vertex
            totalRoadWeight += weightTillNextVertex(vehicle)
            // if we have crossed the weight move limit(10), break the loop
            if (totalRoadWeight > Number.TEN) {
                break
            }
            // increase vertices crossed
            verticesCrossed++
            // remove vertex from route
            vehicle.currentRoute = vehicle.currentRoute.drop(1)
            // update our last visited vertex
            // quick fix: added if statement
            if (vehicle.currentRoute.isNotEmpty()) vehicle.lastVisitedVertex = vehicle.currentRoute[0]

            // set current road
            vehicle.currentRoad = nextRoad
            if (vehicle.currentRoute.size > 1) {
                // update next road
                vehicle.currentRoad = vehicle.lastVisitedVertex.connectingRoads[vehicle.currentRoute[1].id]
            }
        }

        // update the vehicle position
        // decrease remainingRouteWeight by 10
        vehicle.remainingRouteWeight =
            max(0, vehicle.remainingRouteWeight - Number.TEN) // ensures no negative road progress
        // increase currentRouteProgress by 10
        vehicle.currentRouteWeightProgress += Number.TEN
    }

    /**
     * Returns the weight still needed to travel to reach the end of the road
     */
    private fun weightTillNextVertex(vehicle: Vehicle): Int {
        return (vehicle.currentRoad?.weight ?: 0) -
            vehicle.currentRouteWeightProgress +
            vehicle.weightTillLastVisitedVertex
    }

    /**
     * Is called if a vehicle has reached the end of its route,
     * updates accordingly
     */
    private fun updateRouteEndReached(vehicle: Vehicle) {
        // set last vertex to emergency vertex and currentRoute to only contain emergency vertex
        if (vehicle.currentRoute.size > 1) {
            vehicle.currentRoute = listOf(vehicle.currentRoute.last())
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
        // reset vehicle attributes
        vehicle.remainingRouteWeight = 0
        vehicle.currentRouteWeightProgress = 0
        vehicle.currentRouteWeightProgress = 0
        // vehicle.lastVisitedVertex = dataHolder.baseToVertex[dataHolder.vehiclesToBase[vehicle.id].baseID]
        // Log asset arrival at its base
        Log.displayAssetArrival(vehicle.id, dataHolder.vehiclesToBase[vehicle.id]?.vertexID ?: 0)
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
