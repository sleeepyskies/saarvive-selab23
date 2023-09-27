package de.unisaarland.cs.se.selab.graph

import de.unisaarland.cs.se.selab.dataClasses.events.Event

/**
 * Represents an edge in the graph. Also contains all active requests currently effecting the edge.
 */
class Road(
    val pType: PrimaryType,
    val sType: SecondaryType,
    internal val villageName: String,
    internal val roadName: String,
    internal var weight: Int,
    internal val heightLimit: Int,
) {
    val activeEvents: MutableList<Event> = mutableListOf()
}
