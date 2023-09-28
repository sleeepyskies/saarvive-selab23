package de.unisaarland.cs.se.selab.parser

import de.unisaarland.cs.se.selab.global.Log
import de.unisaarland.cs.se.selab.global.Number
import de.unisaarland.cs.se.selab.global.StringLiterals
import de.unisaarland.cs.se.selab.graph.Graph
import de.unisaarland.cs.se.selab.graph.Road
import de.unisaarland.cs.se.selab.graph.Vertex
import java.io.File
import java.lang.IllegalArgumentException
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
    private val vertices = mutableListOf<Vertex>() // List of roads on the map (for compatability)
    private var digraphName: String = ""

    private val listOfVertices = mutableListOf<Int>()
    private val listOfEdges = mutableMapOf<Pair<Int, Int>, MutableMap<String, String>>()

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
        val dataInScope = retrieveDataInScope()
        if (!parsedAndValid(dataInScope)) {
            Log.displayInitializationInfoInvalid(this.dotFile.name)
            exitProcess(1)
        }
        Log.displayInitializationInfoValid(this.dotFile.name)
        exitProcess(1)
        val listRoads = mutableListOf<Road>()
    }

    /**
     * Parse and create separate data structures, return the result
     */
    private fun parsedAndValid(dataInScope: String): Boolean {
        val vPat = Pattern.compile("\\A(\\s*(\\d+(\\.\\d+)?)\\s*;\\s*)+").toRegex() // pattern for vertex

        val vertexMatched = vPat.find(dataInScope)
        if (vertexMatched == null) {
            Log.displayInitializationInfoInvalid(this.dotFile.name)
            exitProcess(1)
        }
        val stringVertices = vertexMatched.groupValues[1]
        val stringEdges = dataInScope.substring(vertexMatched.range.last + 1)

        val parsedVertices = parseVertices(stringVertices)
        val parsedEdges = parseEdges(stringEdges)

        return if (parsedEdges && parsedVertices) validateTogether() else false
    }

    /**
     * Validates vertices and edges together
     */
    private fun validateTogether(): Boolean {
        return vertexConnectedToAnother() && edgesConnectExistingVertices()
    }

    /**
     * All edges connect two existing vertices
     */
    private fun edgesConnectExistingVertices(): Boolean {
        this.listOfEdges.keys.forEach { key ->
            run {
                if (!(this.listOfVertices.contains(key.first) || this.listOfVertices.contains(key.second))) return false
            }
        }
        return true
    }

    private fun vertexConnectedToAnother(): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * Parses and validates Edges individually and creates mapping
     */
    private fun parseEdges(stringEdges: String): Boolean {
        val edgeSkeleton =
            Pattern.compile(
                "\\A(\\s*(\\d+(\\.\\d+)?)\\s*->\\s*(\\d+(\\.\\d+)?)\\s*\\[(\\s*[^\\]]+\\s*)\\]\\s*;\\s*)+\\Z"
            )
        val edgePattern =
            Pattern.compile("\\s*(\\d+(\\.\\d+)?)\\s*->\\s*(\\d+(\\.\\d+)?)\\s*\\[(\\s*[^\\]]+\\s*)\\]\\s*;\\s*")

        val skeleton = edgeSkeleton.matcher(stringEdges)
        if (!skeleton.find()) return false

        val edge = edgePattern.matcher(stringEdges)
        while (edge.find()) {
            val id1 = edge.group(1)
            val id2 = edge.group(3)
            if (id1 == id2) return false
            val check1 = Pair(id1.toInt(), id2.toInt())
            val check2 = Pair(id2.toInt(), id1.toInt())
            val matchedEdge = edge.group(Number.FIVE)!!
            if (this.listOfEdges.containsKey(check1) || this.listOfEdges.containsKey(check2) || !parsedAttributes(
                    matchedEdge
                )
            ) {
                return false
            }
            val attributesMapping = parseAttributes(matchedEdge) // parse attributes
            this.listOfEdges[Pair(id1.toInt(), id2.toInt())] = attributesMapping
        }
        if (!roadNameIsUnique() || !commonVertex()) return false
        if (!villageHasMainStreet() || !sideStreetExists()) return false
        return true
    }

    /**
     * Checks if each village has main street
     */
    private fun villageHasMainStreet(): Boolean {
        val villageToRoadTypeMap = mutableMapOf<String, Boolean>()
        for ((_, valueMap) in this.listOfEdges) {
            if (!villageToRoadTypeMap.contains(valueMap.getValue(StringLiterals.VILLAGE))) {
                if (valueMap.getValue(StringLiterals.PRIMARY_TYPE) == StringLiterals.MAIN_STREET) {
                    villageToRoadTypeMap.put(valueMap.getValue(StringLiterals.VILLAGE), true)
                } else {
                    villageToRoadTypeMap.put(valueMap.getValue(StringLiterals.VILLAGE), false)
                }
            } else {
                if (valueMap.getValue(StringLiterals.PRIMARY_TYPE) == StringLiterals.MAIN_STREET) {
                    villageToRoadTypeMap.put(valueMap.getValue(StringLiterals.VILLAGE), true)
                }
            }
        }
        return villageToRoadTypeMap.values.all { it }
    }

    /**
     * Checks if the road name is unique within a village.
     */
    private fun roadNameIsUnique(): Boolean {
        val mapping = mutableMapOf<String, MutableList<String>>()
        this.listOfEdges.values.forEach { key ->
            run {
                if (mapping.containsKey(key.getValue("village"))) {
                    if (!mapping.get(key.getValue("village"))!!
                            .contains(key.getValue("name"))
                    ) {
                        mapping.get(key.getValue("village"))!!.add(key.getValue("name"))
                    } else {
                        return false
                    }
                } else {
                    val newMutableList = mutableListOf<String>()
                    val newVillageName = key.getValue("village")
                    val newRoadName = key.getValue("name")
                    newMutableList.add(newRoadName)
                    mapping[newVillageName] = newMutableList
                }
            }
        }
        return true
    }

    private fun commonVertex(): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * Checks if map has at least one sideStreet road
     */
    private fun sideStreetExists(): Boolean {
        for ((_, valueMap) in this.listOfEdges) {
            for (value in valueMap.values) {
                if (value == "sideStreet") {
                    return true
                }
            }
        }
        return false
    }

    private fun parsedAttributes(matchedEdge: String): Boolean {
        val strPat = "[a-zA-Z][a-zA-Z_]*" // pattern for strings ID
        val numPat = "\\d+(\\.\\d+)?" // pattern for numbers ID
        val vilPat = Pattern.compile("\\s*village\\s*=\\s*($strPat|$numPat)\\s*;") // village Pattern
        val namPat = Pattern.compile("\\s*name\\s*=\\s*($strPat|$numPat)\\s*;") // name Pattern
        val hPat = Pattern.compile("\\s*heightLimit\\s*=\\s*($numPat)\\s*;") // height Pattern
        val wPat = Pattern.compile("\\s*weight\\s*=\\s*($numPat)\\s*;") // weight Pattern
        val pTPat = Pattern.compile("\\s*primaryType\\s*=\\s*(mainStreet|sideStreet|countyRoad)\\s*;")
        // primaryType Pattern
        val sTPat = Pattern.compile("\\s*secondaryType\\s*=\\s*(oneWayStreet|tunnel|none)\\s*;")
        // secondaryType Pattern
        val atPat = Pattern.compile("\\A$vilPat$namPat$hPat$wPat$pTPat$sTPat\\Z") // attribute pattern

        val artPatter = atPat.matcher(matchedEdge)
        if (!artPatter.find()) return false
        return true
    }

    private fun parseAttributes(matchedEdge: String): MutableMap<String, String> {
        val attributes = mutableMapOf<String, String>()
        try {
            val assignmentsArray = matchedEdge.split(";")
            assignmentsArray.forEach { assignment ->
                run {
                    val keyValue = assignment.split("=")
                    attributes[keyValue.elementAt(0)] = keyValue.elementAt(1)
                    when (keyValue.elementAt(0)) {
                        "weight" -> if (keyValue.elementAt(1).toInt() <= 0) throw IllegalArgumentException()
                        "height" -> if (keyValue.elementAt(1).toInt() < 1) throw IllegalArgumentException()
                    }
                }
            }
        } catch (_: Exception) {
            Log.displayInitializationInfoInvalid(this.dotFile.name)
            exitProcess(1)
        }
        if (checkTunnel(attributes) || checkVillageName(attributes)) {
            throw IllegalArgumentException()
        }
        return attributes
    }

    /**
     * Check tunnel
     */
    private fun checkTunnel(attributes: MutableMap<String, String>): Boolean {
        return attributes.getValue("secondaryType") == "tunnel" && attributes.getValue("height")
            .toDouble() > 3
    }

    /**
     * Check village name
     */
    private fun checkVillageName(attributes: MutableMap<String, String>): Boolean {
        return attributes.getValue("primaryType") != "countyRoad" && attributes.getValue("village") == this.digraphName
    }

    /**
     * Parses and validates Vertices individually and creates a list of Int
     */
    private fun parseVertices(stringVertices: String): Boolean {
        val stringVerticesList = stringVertices.split(";")
        val intVertices = stringVerticesList.map { it.toInt() }
        val distinctVertices = intVertices.distinct()
        if (distinctVertices.count() != intVertices.count()) {
            return false
        }
        intVertices.forEach { int -> if (int < 0) return false }
        this.listOfVertices.addAll(intVertices)
        return true
    }

    /**
     * Returns only data in scopes
     */
    private fun retrieveDataInScope(): String {
        val strPat = "[a-zA-Z][a-zA-Z_]*" // pattern for strings ID
        val numPat = "\\d+(\\.\\d+)?" // pattern for numbers ID
        val mapPat =
            Pattern.compile("\\A(\\s*)digraph\\s+($strPat|$numPat)\\s*\\{([^}]*)\\}(\\s*)\\Z") // pattern for map

        val mapMatcher = mapPat.matcher(this.data)
        if (!mapMatcher.find()) {
            Log.displayInitializationInfoInvalid(this.dotFile.name)
            exitProcess(1)
        }
        this.digraphName = mapMatcher.group(2)
        return mapMatcher.group(Number.FOUR) // returns data in the scope
    }
}
