package de.unisaarland.cs.se.selab.parser

import de.unisaarland.cs.se.selab.graph.Graph
import de.unisaarland.cs.se.selab.graph.Road
import de.unisaarland.cs.se.selab.graph.Vertex

class CountyParser {
//    public fun parse():Graph {
//        return Graph()
//    }

    protected fun createBlueprint(): Map<String, String>{
        val blueprint = mutableMapOf<String, String>()
        return blueprint
    }

    protected fun validateBlueprint(blueprint: Map<String, String>):Boolean{
        return true
    }

    protected fun createGraph(vertices: List<Vertex>):Graph{
        return Graph(vertices)
    }

//    protected fun createRoad(road: String): Road {
//        return Road()
//    }

    protected fun createRoadList(blueprint: Map<String, String>): List<Road> {
        val roads = mutableListOf<Road>()
        return roads
    }

    protected fun createVertexList(blueprint: Map<String, String>): List<Vertex> {
        val vertices = mutableListOf<Vertex>()
        return vertices
    }

//    protected fun createVertex(vertex: String): Vertex {
//        return Vertex()
//    }

    protected fun connectVertices(vertices: List<Vertex>, roads: List<Road>, blueprint: Map<String, String>): Unit {
        return Unit
    }
}