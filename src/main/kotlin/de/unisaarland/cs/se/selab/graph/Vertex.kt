package de.unisaarland.cs.se.selab.graph

/**
 * Represents a vertex in the simulation, contains
 * all connecting vertices via their respective roads
 * @param id The id of the vertex
 * @param connectingRoads The connected vertices and roads
 */
data class Vertex(
    val id: Int,
    // the key is the ID of the vertex that this vertes is connected
    val connectingRoads: MutableMap<Int, Road>
)
