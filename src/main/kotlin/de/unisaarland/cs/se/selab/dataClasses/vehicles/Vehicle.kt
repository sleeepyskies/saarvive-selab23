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
)
