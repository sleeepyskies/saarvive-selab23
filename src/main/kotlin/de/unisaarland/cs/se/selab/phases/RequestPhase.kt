package de.unisaarland.cs.se.selab.phases

import de.unisaarland.cs.se.selab.dataClasses.Request
import de.unisaarland.cs.se.selab.dataClasses.bases.Base
import de.unisaarland.cs.se.selab.dataClasses.vehicles.CapacityType
import de.unisaarland.cs.se.selab.dataClasses.vehicles.Vehicle
import de.unisaarland.cs.se.selab.dataClasses.vehicles.VehicleStatus
import de.unisaarland.cs.se.selab.dataClasses.vehicles.VehicleType
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
    private fun getAssignableAssets(base: Base, request: Request): List<Vehicle> {
        val requiredVehicle = base.vehicles.filter { it.status == VehicleStatus.IN_BASE }
        return requiredVehicle
    }

    private fun getRequiredAssets(request: Request): Map<VehicleType, Int> {
        return request.requiredVehicles
    }

    private fun getRequiredCapacity(request: Request): Map<CapacityType, Int> {
        return request.requiredCapacity
    }

    private fun assignBasedOnCapacity(vehicles: List<Vehicle>) {
        //for (vehicle in Vehicle)
    }
}
