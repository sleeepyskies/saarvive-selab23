package de.unisaarland.cs.se.selab.dataClasses.events

import de.unisaarland.cs.se.selab.graph.Road

/**
 * Creates an object of RoadClosure Event, inherits from Event abstract class.
 * Takes the [sourceID] - the source vertex of a road the event influences
 * [targetID] - the target vertex of a road the event influences
 */
class RoadClosure(
    eventID: Int,
    duration: Int,
    startTick: Int,
    public val affectedRoad: Road,
    public val sourceID: Int,
    public val targetID: Int
) : Event(eventID, duration, startTick)
