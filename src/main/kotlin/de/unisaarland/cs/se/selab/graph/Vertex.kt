package de.unisaarland.cs.se.selab.graph

data class Vertex(
    private val id: Int,
    private val connectingRoads: Map<Vertex, Road>
)