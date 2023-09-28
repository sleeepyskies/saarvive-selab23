package de.unisaarland.cs.se.selab.parser

import de.unisaarland.cs.se.selab.global.Log
import de.unisaarland.cs.se.selab.graph.Graph
import de.unisaarland.cs.se.selab.graph.Road
import de.unisaarland.cs.se.selab.graph.Vertex
import java.io.File
import java.util.regex.Pattern
import kotlin.system.exitProcess

/**
 * Class for parsing the .dot file that contains information about Map. Receives [dotFilePath] as path to the .dot data
 * file
 */
class CountyParser(private val dotFilePath: String) {
    private val dotFile: File
    private val data: String
    private val roads = mutableListOf<Road>() // List of roads on the map (for compatability)

    private val strPat = "[a-zA-Z][a-zA-Z_]*" // pattern for strings ID
    private val numPat = "\\d+(\\.\\d+)?"      // pattern for numbers ID

    private val mapPat =
        Pattern.compile("\\A(\\s*)digraph\\s+($strPat|$numPat)\\s*\\{([^}]*)\\}(\\s*)\\Z")

    // general pattern for the whole mapping
    private val vPat = Pattern.compile("\\s*($numPat)\\s*;\\s*") // pattern for vertex
    private val vilPat = Pattern.compile("\\s*village\\s*=\\s*($strPat|$numPat)\\s*;") // village Pattern
    private val namPat = Pattern.compile("\\s*name\\s*=\\s*($strPat|$numPat)\\s*;") // name Pattern
    private val hPat = Pattern.compile("\\s*heightLimit\\s*=\\s*($numPat)\\s*;") // height Pattern
    private val wPat = Pattern.compile("\\s*weight\\s*=\\s*($numPat)\\s*;") // weight Pattern
    private val pTPat = Pattern.compile("\\s*primaryType\\s*=\\s*(mainStreet|sideStreet|countyRoad)\\s*;")

    // primaryType Pattern
    private val sTPat = Pattern.compile("\\s*secondaryType\\s*=\\s*(oneWayStreet|tunnel|none)\\s*;")

    // secondaryType Pattern
    private val atPat = Pattern.compile("$vilPat$namPat$hPat$wPat$pTPat$sTPat") // attribute pattern
    private val ePat = Pattern.compile("\\s*($numPat)\\s*->\\s*($numPat)\\s*\\[(\\s*$atPat\\s*)\\]\\s*;\\s*")

    // pattern for edge
    private val listPattern =
        Pattern.compile("\\A($vPat)+($ePat)+\\s*\\Z")// general pattern for a list

    /**
     * Save the file and data in string
     */
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

    /**
     * Receives results in parsing, validating, creates objects and returns Graph
     */
    fun parse(): Graph {
        // Creating it if the syntax is valid
        val blueprint = createBlueprint()
        if (!validateBlueprint(blueprint)) exitProcess(1)
        Log.displayInitializationInfoValid(this.dotFile.name)
        val roads = createRoadList(blueprint)
        this.roads.addAll(roads)
        val vertices = createVertexList(blueprint)
        connectVertices(vertices, roads, blueprint)
        return createGraph(vertices, roads)
    }

    /**
     * Creates blueprint of digraph - name, id, id->id for Vertices and Roads
     */
    private fun createBlueprint(): Map<String, String> {
        val blueprint = mutableMapOf<String, String>()

        // Match digraph
        val mapMatcher = mapPat.matcher(this.data)
        if (mapMatcher.find()) {
            val mapID = mapMatcher.group(2) // returns name of the digraph
            val list = mapMatcher.group(3) // returns String of the inner data
            blueprint["digraph"] = mapID
            blueprint.putAll(parseDataInScopes(list, mapID))
        } else {
            Log.displayInitializationInfoValid(this.dotFile.name)
            exitProcess(1)
        }
        return blueprint
    }

    /**
     * Receives the [list] - whole data - and [mapID] for some validation
     */
    private fun parseDataInScopes(list: String?, mapID: String?): Map<String, String> {
        val mapping = mutableMapOf<String, String>()
        // Match list
        val listMatcher = listPattern.matcher(list!!)
        if (listMatcher.find()) {
            val vertices = listMatcher.group(1)
            val edges = listMatcher.group(2)
            mapping.putAll(parseVertices(vertices))
            mapping.putAll(parseEdges(edges, mapping, mapID))
        } else {
            Log.displayInitializationInfoValid(this.dotFile.name)
            exitProcess(1)
        }

        return mapping
    }

    private fun parseEdges(
        edges: String?,
        mapping: MutableMap<String, String>,
        mapID: String?
    ): Map<String, String> {
        TODO("Not yet implemented")
    }

    /**
     * Parses [vertices]
     */
    private fun parseVertices(vertices: String?): Map<String, String> {
        val v = mutableMapOf<String, String>()
        // Match vertex
        val vertexMatcher = vPat.matcher(vertices!!)
        while (vertexMatcher.find()) {
            val id = vertexMatcher.group(1)
            val intId = id.toInt()
            if (vertexSatisfies(v, intId)) {
                v[id] = "vertex"
            } else {
                Log.displayInitializationInfoValid(this.dotFile.name)
                exitProcess(1)
            }
        }

        return v
    }

    /**
     * Checks if vertexId [intId] is unique in [v], >=0 (1 condition)
     */
    private fun vertexSatisfies(v: MutableMap<String, String>, intId: Int): Boolean {
        return !v.contains(intId.toString()) && intId >= 0
    }

    /**
     * Return result in validating a [blueprint] (successful or not)
     */
    private fun validateBlueprint(blueprint: Map<String, String>): Boolean {
    }

    /**
     * Creates object of Graph with [vertices] and [roads]
     */
    private fun createGraph(vertices: List<Vertex>, roads: List<Road>): Graph {
    }

    /**
     * Creates single object of Road out of [road] data
     */
    private fun createRoad(road: String): Road {
    }

    /**
     * Returns list of Roads out of [blueprint] data
     */
    private fun createRoadList(blueprint: Map<String, String>): List<Road> {
    }

    /**
     * Returns list of Vertices out of [blueprint] data
     */
    private fun createVertexList(blueprint: Map<String, String>): List<Vertex> {
    }

    /**
     * Creates single object of Vertex out of [vertex] data
     */
    private fun createVertex(vertex: String): Vertex {
    }

    private fun connectVertices(vertices: List<Vertex>, roads: List<Road>, blueprint: Map<String, String>) {
    }
}
