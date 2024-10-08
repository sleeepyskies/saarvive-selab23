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
    open var vehicleStatus: VehicleStatus = VehicleStatus.IN_BASE
    open var isAvailable = true
    open var assignedEmergencyID: Int? = null
    open lateinit var lastVisitedVertex: Vertex
    open var currentRoute: List<Vertex> = emptyList<Vertex>()
    open var remainingRouteWeight: Int = 0
    open var weightTillLastVisitedVertex: Int = 0
    open var currentRouteWeightProgress: Int = 0
    open var currentRoad: Road? = null
    open var ticksStillUnavailable: Int = 0
}
