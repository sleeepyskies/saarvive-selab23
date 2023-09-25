package de.unisaarland.cs.se.selab.dataClasses

import de.unisaarland.cs.se.selab.graph.Road
import de.unisaarland.cs.se.selab.graph.Vertex
import de.unisaarland.cs.se.selab.dataClasses.vehicles.VehicleStatus

open class Vehicle (
    private val vehicleType: VehicleType,
    private val id: Int,
    private val staffCapacity: Int,
    private val height: Int,
    private var isAvailable: Boolean,
    private var vehicleStatus: VehicleStatus,
    private var assignedEmergencyID: Int?,
    private var currentRoad: Road,
    private var lastVisitedVertex: String,
    private var ticksTillDestination: Int,
    private var roadProgress: Int,
    private var currentRoute: List<Vertex>,
    private var ticksStillUnavailable: Int,
    private val assignedBaseID: Int
)


