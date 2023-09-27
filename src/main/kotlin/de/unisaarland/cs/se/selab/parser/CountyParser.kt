package de.unisaarland.cs.se.selab.parser

import de.unisaarland.cs.se.selab.global.Log
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
            this.data = this.dotFile.readText()
        } catch (_: NullPointerException) {
            // File doesn't exist
            Log.displayInitializationInfoInvalid(dotFilePath) // the file doesn't exist
            exitProcess(1)
        }
    }

    fun parse(): Graph {
        // Creating it if the syntax is valid
        val blueprint = createBlueprint()
        if (!validateBlueprint(blueprint)) exitProcess(1)
        return Graph()
    }


    /**
     * Creates blueprint of id or id->if for Vertices and Roads
     */
    private fun createBlueprint(): Map<String, String> {
        val blueprint = mutableMapOf<String, String>()

    }

    private fun validateBlueprint(blueprint: Map<String, String>): Boolean {

    }

    private fun createGraph(vertices: List<Vertex>): Graph {

    }

    protected fun createRoad(road: String): Road {

    }

    private fun createRoadList(blueprint: Map<String, String>): List<Road> {

    }

    private fun createVertexList(blueprint: Map<String, String>): List<Vertex> {

    }

    private fun createVertex(vertex: String): Vertex {

    }

    private fun connectVertices(vertices: List<Vertex>, roads: List<Road>, blueprint: Map<String, String>) {

    }
}
