package de.unisaarland.cs.se.selab.dataClasses.vehicles

import de.unisaarland.cs.se.selab.graph.Road
import de.unisaarland.cs.se.selab.graph.Vertex

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
    public var isAvailable = true
    public var assignedEmergencyID: Int? = null
    public var currentRoad: Road? = null
    public var lastVisitedVertex: Vertex? = null
    public var ticksTillDestination: Int = 0
    public var roadProgrss: Int = 0
    public var currentRoute: MutableList<Vertex> = mutableListOf<Vertex>()
    public var ticksStillUnavailable: Int = 0

    /**
     * Returns the status of the vehicle
     */
    fun getVehicleStatus(): VehicleStatus {
        return status
    }
}



