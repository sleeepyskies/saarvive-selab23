package de.unisaarland.cs.se.selab.graph

import de.unisaarland.cs.se.selab.dataClasses.Request
import java.util.Queue

/**
 * Represents an edge in the graph. Also contains all active requests currently effecting the edge.
 */
class Road(
    private val pType: PrimaryType,
    private val sType: SecondaryType,
    private val villageName: String,
    private val roadName: String,
    internal var weight: Int,
    internal val heightLimit: Int,
    public val activeRequests: Queue<Request>
)
