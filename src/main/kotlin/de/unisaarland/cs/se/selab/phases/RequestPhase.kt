package de.unisaarland.cs.se.selab.phases

import de.unisaarland.cs.se.selab.dataClasses.Request
import de.unisaarland.cs.se.selab.dataClasses.bases.Base
import de.unisaarland.cs.se.selab.dataClasses.vehicles.*
import de.unisaarland.cs.se.selab.simulation.DataHolder

/**
 * deals with all requests created in the current tick
 */
class RequestPhase(val dataHolder: DataHolder) {
    /**
     * excutes all the private functions based on logic
     */
    fun execute() {
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
                VehicleType.FIRE_TRUCK_TECHNICAL
            )
        }
        return requiredVehicle
    }

    private fun getSpecialVehicles(vehicles: List<Vehicle>): List<Vehicle> {
        val requiredVehicle = vehicles.filter { vehicle ->
            vehicle.vehicleType !in setOf(
                VehicleType.POLICE_MOTORCYCLE,
                VehicleType.FIREFIGHTER_TRANSPORTER,
                VehicleType.FIRE_TRUCK_TECHNICAL
            )
        }
        return requiredVehicle
    }

    private fun assignWithoutCapacity(vehicles: List<Vehicle>, request: Request){
        for (vehicle in vehicles) {
            request.requiredVehicles[] = request.requiredVehicles[vehicle.vehicleType]!! - 1
        }
    }
    private fun assignBasedOnCapacity(vehicles: List<Vehicle>, capacity: Map<CapacityType, Int>, request: Request) {
        for (vehicle in vehicles) {
            when (vehicle) {
                is FireTruckWater ->
            }
        }
    }
}
