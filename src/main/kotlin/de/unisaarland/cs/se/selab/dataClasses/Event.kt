package de.unisaarland.cs.se.selab.dataClasses
import de.unisaarland.cs.se.selab.graph.PrimaryType

abstract class Event(open val eventId: Int, val duration: Int, val tick: Int) {
}

// Subclass for Vehicle Unavailable event
class VehicleUnavailable(val vehicleId: Int, override val eventId:Int, duration: Int, tick: Int) : Event(eventId, duration, tick)

// Subclass for Road Closure event
class RoadClosure(val sourceID: Int, val targetID: Int, override val eventId: Int, duration: Int, tick: Int) : Event(eventId, duration, tick)

// Subclass for Rush Hour event
class RushHour(val roadType: PrimaryType, override val eventId:Int, duration: Int, tick: Int) : Event(eventId, duration, tick)

// Subclass for Traffic Jam event
class TrafficJam(val factor: Int, val sourceId: Int, val targetId: Int,override val eventId:Int, duration: Int, tick: Int) : Event(eventId, duration, tick)

// Subclass for Construction event
class Construction(val factor: Int, val streetClosed: Boolean, val sourceId: Int, val targetId: Int,override val eventId:Int, duration: Int, tick: Int) : Event(eventId, duration, tick)