package de.unisaarland.cs.se.selab.dataClasses.events

import de.unisaarland.cs.se.selab.graph.Road

/**
 * Creates an object of the TrafficJam Event, inherits from Event abstract class.
 * Takes the [factor] by which the length of a road is changed during the event;
 */
class TrafficJam(
    eventID: Int,
    duration: Int,
    startTick: Int,
    val factor: Int,
    val startVertex: Int,
    val endVertex: Int
) : Event(eventID, duration, startTick) {
    lateinit var affectedRoad: Road
    var isApplied: Boolean = false
}
