package de.unisaarland.cs.se.selab.graph

data class Vertex(
    internal val id: Int,
    internal val connectingRoads: Map<Vertex, Road>
)
