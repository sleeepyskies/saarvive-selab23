package de.unisaarland.cs.se.selab.dataClasses.events

/**
 * Creates an object of the TrafficJam Event, inherits from Event abstract class.
 * Takes the [factor] by which the length of a road is changed during the event;
 * [sourceID] - the source vertex of a road the event influences
 * [targetID] - the target vertex of a road the event influences
 */
class TrafficJam(
    eventID: Int,
    duration: Int,
    startTick: Int,
    private val factor: Int,
    private val sourceID: Int,
    private val targetID: Int
) : Event(eventID, duration, startTick)
