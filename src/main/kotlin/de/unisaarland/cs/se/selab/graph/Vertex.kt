package de.unisaarland.cs.se.selab.graph

/**
 * Represents a vertex in the simulation, contains
 * all connecting vertices via their respective roads
 * @param id The id of the vertex
 * @param connectingRoads The connected vertices and roads
 */
data class Vertex(
    internal val id: Int,
    internal val connectingRoads: MutableMap<Vertex, Road>
)
