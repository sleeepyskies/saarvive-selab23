package de.unisaarland.cs.se.selab.dataClasses.events

import de.unisaarland.cs.se.selab.dataClasses.events.Event

// Subclass for Traffic Jam event
class TrafficJam(
    val factor: Int,
    val sourceID: String,
    val targetID: String,
    override val eventID: Int,
    duration: Int,
    tick: Int
) : Event(eventID, duration, tick)
