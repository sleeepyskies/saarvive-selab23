package de.unisaarland.cs.se.selab.parser

import de.unisaarland.cs.se.selab.graph.Graph
import de.unisaarland.cs.se.selab.graph.Road
import de.unisaarland.cs.se.selab.graph.Vertex

class CountyParser {
//    public fun parse():Graph {
//        return Graph()
//    }

    private fun createBlueprint(): Map<String, String> {
        val blueprint = mutableMapOf<String, String>()
        return blueprint
    }

    private fun validateBlueprint(blueprint: Map<String, String>): Boolean {
        return true
    }

    private fun createGraph(vertices: List<Vertex>, roads: List<Road>): Graph {
        return Graph(vertices, roads)
    }

//    protected fun createRoad(road: String): Road {
//        return Road()
//    }

    private fun createRoadList(blueprint: Map<String, String>): List<Road> {
        val roads = mutableListOf<Road>()
        return roads
    }

    private fun createVertexList(blueprint: Map<String, String>): List<Vertex> {
        val vertices = mutableListOf<Vertex>()
        return vertices
    }

//    protected fun createVertex(vertex: String): Vertex {
//        return Vertex()
//    }

    private fun connectVertices(vertices: List<Vertex>, roads: List<Road>, blueprint: Map<String, String>) {
        return Unit
    }
}
