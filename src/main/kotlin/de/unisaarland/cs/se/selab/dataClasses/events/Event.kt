package de.unisaarland.cs.se.selab.dataClasses.events

abstract class Event(
    open val eventID: Int,
    var duration: Int, // the property we will change and have condition on?
    val startTick: Int
) {
}

