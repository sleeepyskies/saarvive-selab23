package de.unisaarland.cs.se.selab.dataClasses.events

import de.unisaarland.cs.se.selab.graph.PrimaryType

/**
 * Creates an object of the RushHour Event, inherits from Event abstract class.
 * Takes the [roadType] - the types of roads that are affected by the current event;
 * [factor] by which the length of a road is changed during the event
 */
class RushHour(
    eventID: Int,
    duration: Int,
    startTick: Int,
    private val roadType: PrimaryType,
    private val factor: Int,
) : Event(eventID, duration, startTick)
