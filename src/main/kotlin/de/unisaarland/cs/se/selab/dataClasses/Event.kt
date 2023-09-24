package de.unisaarland.cs.se.selab.dataClasses
import de.unisaarland.cs.se.selab.graph.PrimaryType

abstract class Event(open val eventID: Int, val duration: Int, val startTick: Int) {
}

