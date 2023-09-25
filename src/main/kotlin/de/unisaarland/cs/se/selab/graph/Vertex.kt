package de.unisaarland.cs.se.selab.graph

/**
 * each vertex object is a node in the graph
 * @param connectingRoads is a mapping of other vertexes connected to this vertex
 * and the roads they're connected by
 */
data class Vertex(
    private val id: Int,
    private val connectingRoads: Map<Vertex, Road>
)
