package kotlin.de.unisaarland.cs.se.selab.dataClasses

import de.unisaarland.cs.se.selab.dataClasses.Event
import kotlin.de.unisaarland.cs.se.selab.graph.PrimaryType

// Subclass for Rush Hour event
class RushHour(
    val roadType: PrimaryType,
    override val eventID: Int,
    duration: Int,
    tick: Int
) :
    Event(eventID, duration, tick)
