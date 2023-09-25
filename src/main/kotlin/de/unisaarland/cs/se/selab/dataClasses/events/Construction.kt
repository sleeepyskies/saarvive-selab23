package de.unisaarland.cs.se.selab.dataClasses.events

// Subclass for Construction event
class Construction(
    val factor: Int,
    val streetClosed: Boolean,
    val sourceID: String,
    val targetID: String,
    override val eventID: Int,
    duration: Int,
    tick: Int
) : Event(eventID, duration, tick)