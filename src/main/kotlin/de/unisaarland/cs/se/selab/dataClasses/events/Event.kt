package de.unisaarland.cs.se.selab.dataClasses.events

/**
 * Abstract class that takes [eventID], which determines unique event;
 * [duration] - specifies for how long the event is active (also a condition of updating);
 * [startTick] - the tick the event is planned for
 */
abstract class Event(
    private val eventID: Int,
    private var duration: Int, // the property will be changed and have condition on
    private val startTick: Int
)
