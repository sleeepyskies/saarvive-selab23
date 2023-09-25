package de.unisaarland.cs.se.selab.dataClasses

import de.unisaarland.cs.se.selab.graph.Road
import de.unisaarland.cs.se.selab.graph.Vertex

enum class VehicleStatus {
    RECHARGING,
    IN_BASE,
    HANDLING,
    MOVING_TO_EMERGENCY,
    MOVING_TO_BASE,
    ARRIVED,
    WAITING_AT_EMERGENCY
}
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

class FireTruckWithLadder (
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
    private val ladderLength: Int
): Vehicle(
    vehicleType, id, staffCapacity, height, isAvailable, vehicleStatus,
    assignedEmergencyID, currentRoad, lastVisitedVertex, ticksTillDestination,
    roadProgress, currentRoute, ticksStillUnavailable, assignedBaseID)

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

class FireTruckWater (
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
    private val maxWaterCapacity: Int,
    private var currentWaterCapacity: Int
): Vehicle(
    vehicleType, id, staffCapacity, height, isAvailable, vehicleStatus,
    assignedEmergencyID, currentRoad, lastVisitedVertex, ticksTillDestination,
    roadProgress, currentRoute, ticksStillUnavailable, assignedBaseID)

class Ambulance (
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
    private val hasPassenger: Boolean
): Vehicle(
    vehicleType, id, staffCapacity, height, isAvailable, vehicleStatus,
    assignedEmergencyID, currentRoad, lastVisitedVertex, ticksTillDestination,
    roadProgress, currentRoute, ticksStillUnavailable, assignedBaseID)
