package de.unisaarland.cs.se.selab.graph

import de.unisaarland.cs.se.selab.dataClasses.events.Event

/**
 * Represents an edge in the graph. Also contains all active requests currently effecting the edge.
 */
class Road(
    public val pType: PrimaryType,
    public val sType: SecondaryType,
    private val villageName: String,
    private val roadName: String,
    internal var weight: Int,
    internal val heightLimit: Int,
    public val activeEvents: MutableList<Event>
)
