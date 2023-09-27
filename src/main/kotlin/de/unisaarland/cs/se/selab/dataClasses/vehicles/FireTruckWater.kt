package de.unisaarland.cs.se.selab.dataClasses.vehicles

import de.unisaarland.cs.se.selab.graph.Vertex

/**
 * an inheritance of vehicle that has an additional attribute water capacity
 */
class FireTruckWater(
    override val vehicleType: VehicleType,
    override val id: Int,
    override val staffCapacity: Int,
    override val height: Int,
    override val assignedBaseID: Int,
    val maxWaterCapacity: Int
) : Vehicle(
    vehicleType,
    id,
    staffCapacity,
    height,
    assignedBaseID
) {
    override var isAvailable: Boolean = true
    override lateinit var vehicleStatus: VehicleStatus
    override var assignedEmergencyID: Int? = null
    override lateinit var lastVisitedVertex: Vertex
    override var roadProgress: Int = 0
    override lateinit var currentRoute: MutableList<Vertex>
    override var ticksStillUnavailable: Int = 0
    private var currentWaterCapacity: Int = 0
}
