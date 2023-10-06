package de.unisaarland.cs.se.selab.phases

import de.unisaarland.cs.se.selab.dataClasses.emergencies.Emergency
import de.unisaarland.cs.se.selab.dataClasses.events.Event
import de.unisaarland.cs.se.selab.dataClasses.events.VehicleUnavailable
import de.unisaarland.cs.se.selab.dataClasses.vehicles.Vehicle
import de.unisaarland.cs.se.selab.dataClasses.vehicles.VehicleStatus
import de.unisaarland.cs.se.selab.global.Log
import de.unisaarland.cs.se.selab.global.Number
import de.unisaarland.cs.se.selab.graph.Vertex
import de.unisaarland.cs.se.selab.simulation.DataHolder

/**
 * The MapUpdatePhase is responsible for updating the map according to the events.
 * It is executed in every tick.
 */
class MapUpdatePhase(private val dataHolder: DataHolder) : Phase {
    // used to avoid !!
    private val v = Vertex(-Number.FIVE, mutableMapOf())

    var currentTick = 0
    var shouldReroute = false

    override fun execute() {
        val activeEvents = dataHolder.events.filter { event: Event -> event.startTick <= currentTick }.toMutableList()
        if (activeEvents.isNotEmpty()) {
            // events are always sorted by their ID and applied in that order
            val sortedActiveEvents = mutableListOf<Event>()
            sortedActiveEvents.addAll(activeEvents.sortedBy { it.eventID })
            // apply/revert relevant events
            applyRevertEvents(sortedActiveEvents)
            // reduce active event durations
            sortedActiveEvents.forEach { event: Event ->
                if (event.duration > 0 && event !is VehicleUnavailable && event.isApplied) {
                    event.duration -= 1
                } else if (event is VehicleUnavailable && event.duration > 0 && event.isApplied) {
                    // if the event is a vehicle unavailable event, reduce the duration and the ticks still unavailable
                    event.duration -= 1
                    reduceTimeForVehicleUnavailable(event)
                }
            }
            // reroute vehicles if event ended/triggered
            if (shouldReroute) {
                rerouteVehicles()
                shouldReroute = false
            }
        }
        this.currentTick++
    }

    /**
     * Triggers the passed event
     */
    fun triggerEvent(event: Event) {
        if (event is VehicleUnavailable) {
            // add vehicle id to unavailable vehicles
            dataHolder.unavailableVehicles.add(event.vehicleID)
            // change the event to applied
            event.isApplied = true
            val vehicleBase = dataHolder.vehiclesToBase[event.vehicleID]
            val vehicle = vehicleBase?.vehicles?.find { v -> v.id == event.vehicleID }
            if (vehicle != null) {
                vehicle.isAvailable = false
                vehicle.ticksStillUnavailable = event.duration
            }
        } else {
            // apply graph event
            dataHolder.graph.applyGraphEvent(event, currentTick)
            shouldReroute = true
        }
        // Log.displayEventStarted(event.eventID)
    }

    /**
     * Ends the passed event
     */
    private fun endEvent(event: Event, eventStartingList: MutableList<Event>) {
        if (event is VehicleUnavailable) {
            // remove vehicle id to unavailable vehicles
            dataHolder.unavailableVehicles.remove(event.vehicleID)
            // get relevant vehicle and set to unavailable
            val vehicleBase = dataHolder.vehiclesToBase[event.vehicleID]
            val vehicle = vehicleBase?.vehicles?.find { v -> v.id == event.vehicleID }
            if (vehicle != null) {
                vehicle.isAvailable = true
            }
        } else {
            // revert graph event
            dataHolder.graph.revertGraphEvent(event, eventStartingList)
            shouldReroute = true
        }
        Log.displayEventEnded(event.eventID)
        dataHolder.events.remove(event)
    }

    /**
     * Checks if events should be applied/reverted and does so accordingly
     */
    fun applyRevertEvents(events: MutableList<Event>) {
        val endingList = mutableListOf<Event>()
        val startingList = mutableListOf<Event>()
        events.forEach { event ->
            when {
                event.duration == 0 -> {
                    endingList.add(event)
                    // endEvent(event)
                }

                event.startTick == currentTick -> {
                    startingList.add(event)
                    // triggerEvent(event)
                }
            }
        }
        val sortedEndingList = endingList.sortedBy { it.eventID }
        for (event in sortedEndingList) endEvent(event, startingList)
        // starting list includes the events that have started in this tick or the ones that are free'd from the queue
        val sortedStartingList = startingList.sortedBy { it.eventID }
        for (event in sortedStartingList) triggerEvent(event)
    }

    /**
     * Reduces the time a vehicle is unavailable
     */
    private fun reduceTimeForVehicleUnavailable(event: VehicleUnavailable) {
        val vehicle1 = dataHolder.unavailableVehicles.find { v -> v == event.vehicleID }
        if (vehicle1 != null) {
            val vehicleBase = dataHolder.vehiclesToBase[vehicle1]
            if (vehicleBase != null) {
                val vehicle = vehicleBase.vehicles.find { v -> v.id == vehicle1 }
                if (vehicle != null && vehicle.vehicleStatus == VehicleStatus.IN_BASE) {
                    vehicle.ticksStillUnavailable -= 1
                }
            }
        }
    }

    /**
     * Reroutes all active vehicles if an event ends/starts
     */
    private fun rerouteVehicles() {
        // only reroute vehicles that are moving, not at emergency
        val assets = dataHolder.activeVehicles.filter {

                it.vehicleStatus == VehicleStatus.MOVING_TO_EMERGENCY ||
                    it.vehicleStatus == VehicleStatus.MOVING_TO_BASE

        }

        var assetsReroutedNum: Int = 0

        for (vehicle in assets) {
            val vehicleRoad = vehicle.currentRoad

            // if the vehicle is currently on the road where an emergency has started it ignores it
            if (vehicleRoad != null) {
                if (vehicleRoad.activeEvents.first().startTick == currentTick) {
                    continue
                }
            }

            val vEmergency = dataHolder.vehicleToEmergency[vehicle.id]
            val currentRouteWeight = dataHolder.graph.weightOfRoute(
                vehicle.lastVisitedVertex,
                findClosestVertex(vehicle, vEmergency),
                vehicle.height
            )

            val timeToArrive1 = dataHolder.graph.weightOfRoute(
                vehicle.lastVisitedVertex,
                findClosestVertex(vehicle, vEmergency),
                vehicle.height
            )

            // New route is faster -> reroute
            val newRoute = dataHolder.graph.calculateShortestRoute(
                vehicle.lastVisitedVertex,
                findClosestVertex(vehicle, vEmergency),
                vehicle.height
            )

            // reroutes if there's a new better route
            if (newRoute != vehicle.currentRoute) {
                // add weight till end of road
                vehicle.currentRoute = newRoute
                vehicle.remainingRouteWeight = dataHolder.graph.weightOfRoute(
                    vehicle.lastVisitedVertex,
                    newRoute.last(),
                    vehicle.height
                )
                vehicle.currentRouteWeightProgress = 0
                assetsReroutedNum +=1
            }
            // if the weight of the path of vehicle changes while the route stays the same
            else if (currentRouteWeight != vehicle.remainingRouteWeight) {
                assetsReroutedNum +=1
            }
        }

        // Log the number of assets rerouted
        if (assetsReroutedNum > 0) {
            Log.displayAssetsRerouted(assetsReroutedNum)
        }
        dataHolder.assetsRerouted += assetsReroutedNum
    }

    /**
     * Finds the closest emergency vertex to a vehicle
     */
    private fun findClosestVertex(vehicle: Vehicle, emergency: Emergency?): Vertex {
        val v1 = emergency?.location?.first ?: v
        val v2 = emergency?.location?.second ?: v
        val d1 = dataHolder.graph.calculateShortestPath(vehicle.lastVisitedVertex, v1, vehicle.height)
        val d2 = dataHolder.graph.calculateShortestPath(vehicle.lastVisitedVertex, v2, vehicle.height)

        return if (d1 <= d2) v1 else v2
    }
}
