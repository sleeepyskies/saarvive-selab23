package de.unisaarland.cs.se.selab.dataClasses.events

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
    private val factor: Int,
    private val sourceID: Int, // id of the vertex
    private val targetID: Int
) : Event(eventID, duration, startTick) {
    private val streetClosed: Boolean = false
    // oneWayStreet
}
