package de.unisaarland.cs.se.selab.dataClasses.vehicles

import de.unisaarland.cs.se.selab.graph.Road
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
    val maxCriminalCapacity: Int
) : Vehicle(
    vehicleType,
    id,
    staffCapacity,
    height,
    assignedBaseID
) {
    var isAvailable: Boolean = true
    lateinit var vehicleStatus: VehicleStatus
    var assignedEmergencyID: Int? = null
    lateinit var currentRoad: Road
    lateinit var lastVisitedVertex: String
    var ticksTillDestination: Int = 0
    var roadProgress: Int = 0
    lateinit var currentRoute: List<Vertex>
    var ticksStillUnavailable: Int = 0
    var currentCriminalCapcity: Int = 0
}
