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
    public val factor: Int,
    public val sourceID: Int, // id of the vertex
    public val targetID: Int,
    public val streetClosed: Boolean
) : Event(eventID, duration, startTick) {
    public lateinit var affectedRoad: Road
    // oneWayStreet
}
