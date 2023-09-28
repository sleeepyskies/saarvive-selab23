package de.unisaarland.cs.se.selab.phases

import de.unisaarland.cs.se.selab.dataClasses.events.VehicleUnavailable
import de.unisaarland.cs.se.selab.global.Log
import de.unisaarland.cs.se.selab.simulation.DataHolder

/**
 * The MapUpdatePhase is responsible for updating the map according to the events.
 * It is executed in every tick.
 */
class MapUpdatePhase(private val dataHolder: DataHolder) : Phase {
    private var currentTick = 0
    override fun execute() {
        fun reduceEventDuration() {
            dataHolder.events.forEach { event ->
                when {
                    event.duration > 0 -> {
                        event.duration -= 1
                    }
                    event.duration == 0 -> {
                        if (event is VehicleUnavailable) {
                            dataHolder.unavailableVehicles.removeIf { vehicle -> vehicle.id == event.vehicleID }
                        }
                        dataHolder.graph.revertGraphEvent(event)
                        Log.displayEventEnded(event.eventID)
                    }
                    event.startTick == currentTick -> {
                        dataHolder.graph.applyGraphEvent(event)
                        Log.displayEventStarted(event.eventID)
                        dataHolder.activeVehicles.forEach { vehicle ->
                            val vehicleRoute = vehicle.currentRoute
                            val vehiclePosition = vehicle.lastVisitedVertex
                            vehicle.currentRoute = dataHolder.graph.calculateShortestRoute(
                                vehiclePosition,
                                vehicleRoute.last(),
                                vehicle.height
                            )
                        }
                    }
                }
            }
            // Remove completed events from the list
            dataHolder.events = dataHolder.events.filter { it.duration > 0 }.toMutableList()
        }
        // need to add logic for vehicles
    }
}
