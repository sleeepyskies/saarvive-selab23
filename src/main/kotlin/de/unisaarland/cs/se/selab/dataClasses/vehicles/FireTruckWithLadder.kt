package de.unisaarland.cs.se.selab.dataClasses.vehicles

import de.unisaarland.cs.se.selab.graph.Road
import de.unisaarland.cs.se.selab.graph.Vertex

/**
 * an inheritance of vehicle that has an additional attribute ladder length
 */
class FireTruckWithLadder(
    override val vehicleType: VehicleType,
    override val id: Int,
    override val staffCapacity: Int,
    override val height: Int,
    override val assignedBaseID: Int,
    val ladderLength: Int
) : Vehicle(
    vehicleType,
    id,
    staffCapacity,
    height,
    assignedBaseID
) {
    private var isAvailable: Boolean = true
    private lateinit var vehicleStatus: VehicleStatus
    private var assignedEmergencyID: Int? = null
    private lateinit var currentRoad: Road
    private lateinit var lastVisitedVertex: String
    private var ticksTillDestination: Int = 0
    private var roadProgress: Int = 0
    private lateinit var currentRoute: List<Vertex>
    private var ticksStillUnavailable: Int = 0
}
