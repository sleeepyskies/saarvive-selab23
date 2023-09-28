package de.unisaarland.cs.se.selab.dataClasses.vehicles

import de.unisaarland.cs.se.selab.graph.Vertex

/**
 * an inheritance of the abstract class 'vehicle'
 * has an additional attribute - has passenger
 */
class Ambulance(
    override val vehicleType: VehicleType,
    override val id: Int,
    override val staffCapacity: Int,
    override val height: Int,
    override val assignedBaseID: Int
) : Vehicle(
    vehicleType,
    id,
    staffCapacity,
    height,
    assignedBaseID
) {
    override var isAvailable: Boolean = true
    override var vehicleStatus: VehicleStatus = VehicleStatus.IN_BASE
    override var assignedEmergencyID: Int? = null
    override lateinit var lastVisitedVertex: Vertex
    override var roadProgress: Int = 0
    override var currentRoute: List<Vertex> = emptyList<Vertex>()
    override var ticksStillUnavailable: Int = 0
    var hasPatient: Boolean = false
}
