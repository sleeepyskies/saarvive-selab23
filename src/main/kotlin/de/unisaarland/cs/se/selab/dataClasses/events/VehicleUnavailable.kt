package de.unisaarland.cs.se.selab.dataClasses.events

import de.unisaarland.cs.se.selab.dataClasses.events.Event

// Subclass for Vehicle Unavailable event
class VehicleUnavailable(
    val vehicleID: Int,
    override val eventID: Int,
    duration: Int,
    tick: Int
) :
    Event(eventID, duration, tick)
