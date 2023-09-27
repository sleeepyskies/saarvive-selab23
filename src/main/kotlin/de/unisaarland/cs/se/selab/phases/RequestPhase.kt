package de.unisaarland.cs.se.selab.phases

import de.unisaarland.cs.se.selab.dataClasses.Request
import de.unisaarland.cs.se.selab.dataClasses.bases.Base
import de.unisaarland.cs.se.selab.dataClasses.vehicles.*
import de.unisaarland.cs.se.selab.simulation.DataHolder

/**
 * deals with all requests created in the current tick
 */
class RequestPhase(val dataHolder: DataHolder) : Phase {
    /**
     * excutes all the private functions based on logic
     */
    override fun execute() {
    }

    /**
     * check if there are any requests in this tick
     */
    private fun requestsExits(): Boolean {
        if (dataHolder.requests.isNotEmpty()) return true else return false
    }

    /**
     * find all the vehicles that are in the base
     */
    private fun getAssignableAssets(base: Base, requestedVehicles: Map<VehicleType, Int>): List<Vehicle> {
        val requiredVehicle = base.vehicles.filter {
            it.vehicleStatus == VehicleStatus.IN_BASE && it.vehicleType in requestedVehicles
        }
        return requiredVehicle
    }

    private fun getRequiredAssets(request: Request): Map<VehicleType, Int> {
        return request.requiredVehicles
    }

    private fun getRequiredCapacity(request: Request): Map<CapacityType, Int> {
        return request.requiredCapacity
    }

    private fun getNormalVehicles(vehicles: List<Vehicle>): List<Vehicle> {
        val requiredVehicle = vehicles.filter { vehicle ->
            vehicle.vehicleType in setOf(
                VehicleType.POLICE_MOTORCYCLE,
                VehicleType.FIREFIGHTER_TRANSPORTER,
                VehicleType.FIRE_TRUCK_TECHNICAL,
                VehicleType.EMERGENCY_DOCTOR_CAR,
                VehicleType.K9_POLICE_CAR
            )
        }
        return requiredVehicle
    }

    private fun getSpecialVehicles(vehicles: List<Vehicle>): List<Vehicle> {
        val requiredVehicle = vehicles.filter { vehicle ->
            vehicle.vehicleType in setOf(
                VehicleType.POLICE_CAR,
                VehicleType.AMBULANCE,
                VehicleType.FIRE_TRUCK_WATER,
                VehicleType.FIRE_TRUCK_LADDER
            )
        }
        return requiredVehicle
    }

    private fun assignWithoutCapacity(vehicles: List<Vehicle>, request: Request) {
        val emergency = dataHolder.emergencies.first { it.id == request.emergencyID }
        val graph = dataHolder.graph
        val requiredVehicles = request.requiredVehicles
        val emergencyVertex = emergency.location.first

        // only procced to the next step if the request still needs this vehicle
        for (vehicle in vehicles) if (requiredVehicles.containsKey(vehicle.vehicleType)) {
            val shortestPath = graph.calculateShortestPath(vehicle.lastVisitedVertex, emergencyVertex, vehicle.height)
            // check if the car can reach in time
            if (shortestPath < emergency.maxDuration) {
                // update the required vehicles for the request
                if (requiredVehicles[vehicle.vehicleType] == 0) {
                    requiredVehicles.remove(vehicle.vehicleType)
                } else {
                    requiredVehicles[vehicle.vehicleType] = requiredVehicles[vehicle.vehicleType]!! - 1
                }
                // update the vehicle since its assigned an emergency
                vehicle.assignedEmergencyID = emergency.id
                vehicle.currentRoute = graph.calculateShortestRoute(
                    vehicle.lastVisitedVertex,
                    emergencyVertex,
                    vehicle.height
                ).toMutableList()
                // add this vehicle to the list of active vehicles
                dataHolder.activeVehicles.add(vehicle)
            }
        }
    }
    private fun assignBasedOnCapacity(vehicles: List<Vehicle>, request: Request) {
        val requiredVehicles = request.requiredVehicles
        val requiredCapacity = request.requiredCapacity
        for (vehicle in vehicles) if (requiredVehicles.isNotEmpty()) {
            if (vehicle.vehicleType in request.requiredVehicles) {
                when (vehicle) {
                    is FireTruckWater -> assignFireTruckWater(vehicle, request)
                }
            } else {
                break
            }
        }
    }

    private fun assignFireTruckWater(vehicle: FireTruckWater, request: Request) {
        val requiredNum = request.requiredVehicles[VehicleType.FIRE_TRUCK_WATER]
        val requiredGallons = request.requiredCapacity[CapacityType.WATER]
        if (request.requiredCapacity.containsKey(CapacityType.WATER)) {
            // checking if this vehicle has enough capacity to equally divide the gallon amount
            if (vehicle.currentWaterCapacity >= requiredGallons!! / requiredNum!!) {
                // update the request amount
                // just removing this truck contribution
                request.requiredCapacity[CapacityType.WATER] = requiredGallons - (requiredGallons / requiredNum)
                if (request.requiredCapacity[CapacityType.WATER] == 0) {
                    request.requiredCapacity.remove(CapacityType.WATER)
                }
            }
        }

        //request.requiredVehicles[VehicleType.FIRE_TRUCK_WATER] = request.requiredVehicles[VehicleType.FIRE_TRUCK_WATER]!! - 1
    }
}
