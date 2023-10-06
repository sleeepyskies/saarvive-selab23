package de.unisaarland.cs.se.selab.dataClasses.events

/**
 * Abstract class that takes [eventID], which determines unique event;
 * [duration] - specifies for how long the event is active (also a condition of updating);
 * [startTick] - the tick the event is planned for
 */
open class Event(
    open val eventID: Int,
    open var duration: Int, // the property will be changed and have condition on
    open val startTick: Int
) {
    open var isApplied: Boolean = false
}
