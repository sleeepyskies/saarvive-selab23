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
    public val events = dataHolder.events

    override fun execute() {
        if (events.isNotEmpty()) {
            triggerEvent(events)
            reduceEventDuration(events)
            if (shouldReroute) rerouteVehicles()
            shouldReroute = false
        }
        this.currentTick++
    }

    public fun triggerEvent(events: MutableList<Event>) {
        for (event in events) {
            if (event.startTick == this.currentTick) {
                dataHolder.graph.applyGraphEvent(event)
                Log.displayEventStarted(event.eventID)
                shouldReroute = true
            }
        }
    }

    private fun endEvent(event: Event) {
        if (event.duration == 0) {
            Log.displayEventEnded(event.eventID)
        }
        shouldReroute = true
    }
    public fun reduceEventDuration(events: MutableList<Event>) {
        events.forEach { event ->
            when {
                event.duration > 0 -> {
                    event.duration -= 1
                }
                event.duration == 0 -> {
                    if (event is VehicleUnavailable) {
                        dataHolder.unavailableVehicles.removeIf { id: Int -> id == event.vehicleID }
                    }
                    dataHolder.graph.revertGraphEvent(event)
                    endEvent(event)
                }
                // is this needed?? We have triggerEvents already
                event.startTick == currentTick -> {
                    dataHolder.graph.applyGraphEvent(event)
                    // Log.displayEventStarted(event.eventID)
                }
            }
        }
        // Remove completed events from the list
        events.removeIf { event -> event.duration == 0 }
    }

    /**
     * Reroutes all active vehicles if an event ends/starts
     */
    private fun rerouteVehicles() {
        var assetsRerouted = 0
        dataHolder.activeVehicles.forEach { vehicle ->
            val vehicleRoute = vehicle.currentRoute
            val vehiclePosition = vehicle.lastVisitedVertex
            vehicle.currentRoute = dataHolder.graph.calculateShortestRoute(
                vehiclePosition,
                vehicleRoute.last(),
                vehicle.height
            )
            assetsRerouted++
        }
        if (assetsRerouted > 0) Log.displayAssetsRerouted(assetsRerouted)
        dataHolder.assetsRerouted += assetsRerouted
    }
}
