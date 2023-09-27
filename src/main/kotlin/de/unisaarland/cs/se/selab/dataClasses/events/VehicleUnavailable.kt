package de.unisaarland.cs.se.selab.dataClasses.events

/**
 * Creates an object of the TrafficJam Event, inherits from Event abstract class.
 * Takes the [vehicleID] that becomes unavailable
 */
class VehicleUnavailable(eventID: Int, duration: Int, startTick: Int, val vehicleID: Int) : Event(
    eventID,
    duration,
    startTick
)
