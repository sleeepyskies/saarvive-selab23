package de.unisaarland.cs.se.selab.dataClasses.vehicles

/**
 * a super class which defines vehicles
 */
open class Vehicle(
    open val vehicleType: VehicleType,
    open val id: Int,
    open val staffCapacity: Int,
    open val height: Int,
    open val assignedBaseID: Int
) {
    var status: VehicleStatus = VehicleStatus.IN_BASE

    /**
     * Returns the status of the vehicle
     */
    fun getVehicleStatus(): VehicleStatus {
        return status
    }
}



