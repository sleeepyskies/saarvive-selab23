package de.unisaarland.cs.se.selab.dataClasses.events

import de.unisaarland.cs.se.selab.graph.PrimaryType

// Subclass for Rush Hour event
class RushHour(
    val roadType: PrimaryType,
    override val eventID: Int,
    val factor: Int,
    duration: Int,
    tick: Int,
) :
    Event(eventID, duration, tick)
