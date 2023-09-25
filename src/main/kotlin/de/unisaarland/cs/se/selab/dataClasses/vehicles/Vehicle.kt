package de.unisaarland.cs.se.selab.dataClasses.vehicles

import de.unisaarland.cs.se.selab.dataClasses.VehicleType
import de.unisaarland.cs.se.selab.graph.Road
import de.unisaarland.cs.se.selab.graph.Vertex

/**
 * a super class which defines vehicles
 */
open class Vehicle(
    private val vehicleType: VehicleType,
    private val id: Int,
    private val staffCapacity: Int,
    private val height: Int,
    private val assignedBaseID: Int
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
