package de.unisaarland.cs.se.selab.dataClasses.vehicles

import de.unisaarland.cs.se.selab.graph.Vertex

/**
 * an inheritance of the abstract class 'vehicle'
 * has an additional attribute - criminal capacity
 */
class PoliceCar(
    override val vehicleType: VehicleType,
    override val id: Int,
    override val staffCapacity: Int,
    override val height: Int,
    override val assignedBaseID: Int,
    public val maxCriminalCapacity: Int
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
    var currentCriminalCapcity: Int = 0
}
