package de.unisaarland.cs.se.selab.dataClasses.events

import de.unisaarland.cs.se.selab.graph.Road

/**
 * Creates an object of the Construction Event, inherits from Event abstract class.
 * Takes the [factor] by which the length of a road is changed during the event;
 * [sourceID] - the source vertex of a road the event influences
 * [targetID] - the target vertex of a road the event influences
 */
class Construction(
    eventID: Int,
    duration: Int,
    startTick: Int,
    val factor: Int,
    val sourceID: Int, // id of the vertex
    val targetID: Int,
    var oneWayStreet: Boolean // if true, the road becomes a one-way street
) : Event(eventID, duration, startTick) {
    lateinit var affectedRoad: Road

    // keeps track of wether the event is applied
    // var isApplied: Boolean = false
    // oneWayStreet
}
