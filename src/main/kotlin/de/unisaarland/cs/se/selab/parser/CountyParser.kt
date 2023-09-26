package de.unisaarland.cs.se.selab.parser

import de.unisaarland.cs.se.selab.graph.Graph
import de.unisaarland.cs.se.selab.graph.Road
import de.unisaarland.cs.se.selab.graph.Vertex
import java.io.File
import kotlin.system.exitProcess

/**
 * Class for parsing the .dot file that contains information about Map. Receives [dotFilePath] as path to the .dot data
 * file
 */
class CountyParser(private val dotFilePath: String) {
    private val dotFile: File
    private val data: String
    private val roads: List<Road> = mutableListOf() // List of roads on the map (for compatability)

    init {
        try {
            // Convert path to a file
            this.dotFile = File(dotFilePath)
        } catch (_: NullPointerException) {
            // File doesn't exist
            exitProcess(1)
        }
    }
   fun parse():Graph {
       // Creating it if the syntax is valid
       val blueprint = createBlueprint()
      return Graph()
   }

    private fun createBlueprint(): Map<String, String> {
        val blueprint = mutableMapOf<String, String>()
this.
        return blueprint
    }

    private fun validateBlueprint(blueprint: Map<String, String>): Boolean {
        return true
    }

    private fun createGraph(vertices: List<Vertex>): Graph {
        return Graph(vertices)
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
