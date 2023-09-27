package de.unisaarland.cs.se.selab.phases

import de.unisaarland.cs.se.selab.global.Log
import de.unisaarland.cs.se.selab.simulation.DataHolder

/**
 * The MapUpdatePhase is responsible for updating the map according to the events.
 * It is executed in every tick.
 */
class MapUpdatePhase(private val dataHolder: DataHolder) : Phase {
    private var currentTick = 0
    override fun execute() {
        fun reduceEventDuration(): Unit {
            for (event in dataHolder.events) {
                if (event.duration > 0) {
                    event.duration -= 1
                }
                else if (event.duration == 0) {
                    dataHolder.events.remove(event)
                    dataHolder.graph.revertGraphEvent(event)
                    Log.displayEventEnded(event.eventID)
                }
                else if (event.startTick == currentTick) {
                   dataHolder.graph.applyGraphEvent(event)
                    Log.displayEventStarted(event.eventID)
                    for (vehicle in dataHolder.activeVehicles){
                        val vehicleRoute = vehicle.currentRoute
                        val vehiclePosition = vehicle.lastVisitedVertex
                        vehicle.currentRoute =
                            dataHolder.graph.calculateShortestRoute(vehiclePosition,vehicleRoute.last(), vehicle.height)
                    }
                }
            }
        }

    }
}
