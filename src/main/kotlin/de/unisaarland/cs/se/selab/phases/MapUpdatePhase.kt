package de.unisaarland.cs.se.selab.phases

import de.unisaarland.cs.se.selab.dataClasses.events.Event
import de.unisaarland.cs.se.selab.dataClasses.events.VehicleUnavailable
import de.unisaarland.cs.se.selab.global.Log
import de.unisaarland.cs.se.selab.simulation.DataHolder

/**
 * The MapUpdatePhase is responsible for updating the map according to the events.
 * It is executed in every tick.
 */
class MapUpdatePhase(private val dataHolder: DataHolder) : Phase {
    public var currentTick = 0
    public var shouldReroute = false

    override fun execute() {
        val activeEvents = dataHolder.events.filter { event: Event -> event.startTick <= currentTick }.toMutableList()
        if (activeEvents.isNotEmpty()) {
            // apply/revert relevant events
            applyRevertEvents(activeEvents)
            // reduce active event durations
            activeEvents.forEach { event: Event -> if (event.duration > 0) event.duration -= 1 }
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
    public fun triggerEvent(event: Event) {
        if (event is VehicleUnavailable){
            // add vehicle id to unavailable vehicles
            dataHolder.unavailableVehicles.add(event.vehicleID)
            val vehicleBase = dataHolder.vehiclesToBase[event.vehicleID]
            val vehicle = vehicleBase?.vehicles?.find { v -> v.id == event.vehicleID }
            if (vehicle != null) {
                vehicle.isAvailable = false
            }
        } else {
            // apply graph event
            dataHolder.graph.applyGraphEvent(event)
            shouldReroute = true
        }
        Log.displayEventStarted(event.eventID)
    }

    /**
     * Ends the passed event
     */
    private fun endEvent(event: Event) {
        if (event is VehicleUnavailable){
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
            dataHolder.graph.revertGraphEvent(event)
            shouldReroute = true
        }
        Log.displayEventEnded(event.eventID)
        dataHolder.events.remove(event)
    }

    /**
     * Checks if events should be applied/reverted and does so accordingly
     */
    public fun applyRevertEvents(events: MutableList<Event>) {
        events.forEach { event ->
            when {
                event.duration == 0 -> {
                    endEvent(event)
                }
                event.startTick == currentTick -> {
                    triggerEvent(event)
                }
            }
        }
    }

    /**
     * Reroutes all active vehicles if an event ends/starts
     */
    private fun rerouteVehicles() {
        // Should only reroute vehicles if  there is a faster path/current path is no longer viable
        var assetsRerouted = 0
        dataHolder.activeVehicles.forEach { vehicle ->
            if (dataHolder.graph.weightOfRoute(
                    vehicle.lastVisitedVertex,
                    vehicle.currentRoute.last(),
                    vehicle.height
            ) < vehicle.remainingRouteWeight) {
                // new route is faster -> reroute
                vehicle.currentRoute = dataHolder.graph.calculateShortestRoute(
                    vehicle.lastVisitedVertex,
                    vehicle.currentRoute.last(),
                    vehicle.height
                )
                vehicle.remainingRouteWeight = dataHolder.graph.weightOfRoute(
                    vehicle.lastVisitedVertex,
                    vehicle.currentRoute.last(),
                    vehicle.height
                )
                vehicle.currentRouteWeightProgress = 0
                assetsRerouted++
            }
        }
        // Log number of assets rerouted/
        if (assetsRerouted > 0) Log.displayAssetsRerouted(assetsRerouted)
        dataHolder.assetsRerouted += assetsRerouted
    }
}
