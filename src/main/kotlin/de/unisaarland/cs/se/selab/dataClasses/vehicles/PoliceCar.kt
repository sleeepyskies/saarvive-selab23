package de.unisaarland.cs.se.selab.dataClasses.vehicles

import de.unisaarland.cs.se.selab.dataClasses.Vehicle
import de.unisaarland.cs.se.selab.dataClasses.VehicleStatus
import de.unisaarland.cs.se.selab.dataClasses.VehicleType
import de.unisaarland.cs.se.selab.graph.Road
import de.unisaarland.cs.se.selab.graph.Vertex

class PoliceCar (
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
    private val assignedBaseID: Int,
    private val maxCriminalCapacity: Int,
    private var currentCriminalCapcity: Int
): Vehicle(
    vehicleType, id, staffCapacity, height, isAvailable, vehicleStatus,
    assignedEmergencyID, currentRoad, lastVisitedVertex, ticksTillDestination,
    roadProgress, currentRoute, ticksStillUnavailable, assignedBaseID)
