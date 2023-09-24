package kotlin.de.unisaarland.cs.se.selab.dataClasses

import de.unisaarland.cs.se.selab.dataClasses.Event

// Subclass for Vehicle Unavailable event
class VehicleUnavailable(
    val vehicleID: Int,
    override val eventID: Int,
    duration: Int,
    tick: Int
) :
    Event(eventID, duration, tick)
