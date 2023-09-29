package de.unisaarland.cs.se.selab.parser

import de.unisaarland.cs.se.selab.global.Log
import de.unisaarland.cs.se.selab.global.Number
import de.unisaarland.cs.se.selab.global.StringLiterals
import de.unisaarland.cs.se.selab.graph.Graph
import de.unisaarland.cs.se.selab.graph.PrimaryType
import de.unisaarland.cs.se.selab.graph.Road
import de.unisaarland.cs.se.selab.graph.SecondaryType
import de.unisaarland.cs.se.selab.graph.Vertex
import java.io.File
import java.util.regex.Pattern

/**
 * Class for parsing the .dot file that contains information about Map. Receives [dotFilePath] as path to the .dot data
 * file
 */
class CountyParser(private val dotFilePath: String) {
    private var fileName: String = ""
    private var data: String = ""
    private val roads = mutableListOf<Road>() // List of roads on the map (for compatability)
    private val vertices = mutableListOf<Vertex>() // List of roads on the map (for compatability)
    var digraphName: String = ""

    private val listOfVerticesData = mutableListOf<Int>()
    private val listOfVerticesToRoads = mutableMapOf<Pair<Int, Int>, Road>()
    private val listOfRoadAttributes = mutableListOf(mutableMapOf<String, String>()) // data in strings for validation
    private val idToVertexMapping = mutableMapOf<Int, Vertex>()

    /**
     * Save the file and data in string
     */
    init {
        try {
            // Convert path to a file
            this.fileName = File(dotFilePath).name
            this.data = File(dotFilePath).readText()
        } catch (_: NullPointerException) {
            // File doesn't exist
            outputInvalidAndFinish()
        }
    }

    /**
     * Receives results in parsing, validating, creates objects and returns Graph
     */
    fun parse(): Graph {
        val dataInScope = retrieveDataInScope()
        if (!parsedAndValid(dataInScope)) {
            outputInvalidAndFinish()
        }
        Log.displayInitializationInfoValid(this.fileName)
        // Start creation
        this.listOfVerticesData.forEach { vertex -> this.vertices.add(createVertex(vertex)) } // create Vertex List
        addConnectionsToVertices()
        return Graph(this.vertices, this.roads)
    }

    /**
     * Creates a single object of Vertex
     */
    private fun createVertex(vertex: Int): Vertex {
        val vertexObject = Vertex(vertex, mutableMapOf())
        this.idToVertexMapping.put(vertex, vertexObject)
        return vertexObject
    }

    /**
     * Assigns list of created Road objects
     */
    private fun addConnectionsToVertices() {
        this.listOfVerticesToRoads.forEach { edge ->
            val vertex1 = edge.key.first // vertex id
            val vertex2 = edge.key.second // vertex id

            val vertex1Obj = this.idToVertexMapping.getValue(vertex1)
            val vertex2Obj = this.idToVertexMapping.getValue(vertex2)

            val road = edge.value
            vertex1Obj.connectingRoads.put(vertex2, road)
            vertex2Obj.connectingRoads.put(vertex1, road)
        }
    }

    /**
     * Creates a single object of road
     */
    private fun createRoad(edge: MutableMap<String, String>): Road {
        val villageName = edge[StringLiterals.VILLAGE] ?: "Saarbrucken"
        val roadName = edge[StringLiterals.NAME] ?: "Dudweiler-Straße"
        val weight = edge["weight"]?.toInt() ?: 2
        val heightLimit = edge["heightLimit"]?.toInt() ?: 2
        val pType = when (edge[StringLiterals.PRIMARY_TYPE] ?: "StringLiterals.PRIMARY_TYPE") {
            "mainStreet" -> PrimaryType.MAIN_STREET
            "sideStreet" -> PrimaryType.SIDE_STREET
            else -> {
                PrimaryType.COUNTY_ROAD
            }
        }
        val sType = when (edge[StringLiterals.SECONDARY_TYPE] ?: "secondaryType") {
            "oneWayStreet" -> SecondaryType.ONE_WAY_STREET
            "tunnel" -> SecondaryType.TUNNEL
            else -> {
                SecondaryType.NONE
            }
        }
        return Road(pType, sType, villageName, roadName, weight, heightLimit)
    }

    /**
     * Parse and create separate data structures, return the result
     */
    private fun parsedAndValid(dataInScope: String): Boolean {
        val vPat = Pattern.compile("\\A(\\s*(\\d+(\\.\\d+)?)\\s*;\\s*)+").toRegex() // pattern for vertex

        val vertexMatched = vPat.find(dataInScope)
        val stringVertices = vertexMatched?.groupValues?.get(0).orEmpty()
        val stringEdges = dataInScope.substring((vertexMatched?.range?.last ?: 1) + 1)

        val parsedVertices = parseVertices(stringVertices)
        val parsedEdges = parseEdges(stringEdges)
        if (!roadNameIsUnique() || !commonVertex()) return false
        if (!villageHasMainStreet() || !sideStreetExists()) return false
        return if (parsedEdges && parsedVertices == true) {
            vertexConnectedToAnother() && edgesConnectExistingVertices()
        } else {
            false
        }
    }

    /**
     * All edges connect two existing vertices
     */
    private fun edgesConnectExistingVertices(): Boolean {
        this.listOfVerticesToRoads.keys.forEach { key ->
            if (!(this.listOfVerticesData.contains(key.first) || this.listOfVerticesData.contains(key.second))) {
                return false
            }
        }
        return true
    }

    /**
     * Each vertex is connected to at least one other vertex
     */
    private fun vertexConnectedToAnother(): Boolean {
        this.listOfVerticesData.forEach { vertex ->
            this.listOfVerticesToRoads.keys.forEach { pair ->
                if (!(pair.first == vertex || pair.second == vertex)) return false
            }
        }
        return true
    }

    /**
     * Parses and validates Edges individually and creates mapping
     */
    private fun parseEdges(stringEdges: String): Boolean {
        val edgeSkeleton = Pattern.compile(
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
            if (this.listOfVerticesToRoads.containsKey(check1) ||
                this.listOfVerticesToRoads.containsKey(check2) || !parsedAttributes(matchedEdge)
            ) {
                return false
            }
            val attributesMapping = parseAttributes(matchedEdge) // parse attributes
            this.listOfRoadAttributes.add(attributesMapping)
            val singleRoadObj = createRoad(attributesMapping) // create road obj
            this.roads.add(singleRoadObj)

            this.listOfVerticesToRoads[Pair(id1.toInt(), id2.toInt())] = singleRoadObj
        }
        this.listOfRoadAttributes.removeAt(0)
        return true
    }

    /**
     * Checks if each village has main street
     */
    private fun villageHasMainStreet(): Boolean {
        val villageToRoadTypeMap = mutableMapOf<String, Boolean>()
        for (singleData in this.listOfRoadAttributes) {
            if (!villageToRoadTypeMap.contains(singleData.get(StringLiterals.VILLAGE)) &&
                singleData.get(StringLiterals.VILLAGE) != digraphName
            ) {
                villageToRoadTypeMap[singleData.getValue(StringLiterals.VILLAGE)] =
                    singleData.getValue(StringLiterals.PRIMARY_TYPE) == StringLiterals.MAIN_STREET
            } else {
                if (singleData.getValue(StringLiterals.PRIMARY_TYPE) == StringLiterals.MAIN_STREET) {
                    villageToRoadTypeMap.put(singleData.getValue(StringLiterals.VILLAGE), true)
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
        // String - village name, MutableList<String> - names of the roads in the village
        this.listOfRoadAttributes.forEach { dataPiece ->
            if (mapping.containsKey(dataPiece.getValue(StringLiterals.VILLAGE))) {
                if (mapping[dataPiece.getValue(StringLiterals.VILLAGE)]
                    ?.contains(dataPiece.getValue(StringLiterals.NAME)) == true
                ) {
                    return false
                } else {
                    mapping[dataPiece.getValue(StringLiterals.VILLAGE)]?.add(dataPiece.getValue("name")) ?: "Saarburg"
                }
            } else {
                val newMutableList = mutableListOf<String>()
                val newVillageName = dataPiece.getValue(StringLiterals.VILLAGE)
                val newRoadName = dataPiece.getValue(StringLiterals.NAME)
                newMutableList.add(newRoadName)
                mapping[newVillageName] = newMutableList
            }
        }
        return true
    }

    /**
     * All edges connected to the same vertex belong to the same village or are a countyRoad
     */
    private fun commonVertex(): Boolean {
        val mappingVertexToEdges = mutableMapOf<Int, MutableList<MutableMap<String, String>>>()
        for ((key, road) in listOfVerticesToRoads) {
            val (firstVertex, secondVertex) = key
            listOf(firstVertex, secondVertex).forEach {
                mappingVertexToEdges.computeIfAbsent(it) { mutableListOf() }
                    .add(mutableMapOf(road.villageName to road.pType.toString()))
            }
        }
        return mappingVertexToEdges.all { (_, edges) ->
            val uniqueVillageCount = edges.map { it["village"] }.distinct().size
            uniqueVillageCount == 1 || edges.all { it["primaryType"] == "countyRoad" }
        }
    }

    /**
     * Checks if map has at least one sideStreet road
     */
    private fun sideStreetExists(): Boolean {
        for (edge in this.listOfRoadAttributes) {
            if (edge.getValue(StringLiterals.PRIMARY_TYPE) == "sideStreet") {
                return true
            }
        }
        return false
    }

    /**
     * Returns bool value if the attributes can be parsed
     */
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
        return artPatter.find()
    }

    /**
     * Outputs invalidity log, terminates the program
     */
    private fun outputInvalidAndFinish() {
        Log.displayInitializationInfoInvalid(this.fileName)
        System.err.println("Invalid file format")
    }

    /**
     * Returns Map of parsed attributes
     */
    private fun parseAttributes(matchedEdge: String): MutableMap<String, String> {
        val attributes = mutableMapOf<String, String>()
        val assignmentsArray = matchedEdge.split(";").filter { it.isNotEmpty() }
        assignmentsArray.forEach { assignment ->
            val keyValue = assignment.split("=")
            attributes[keyValue.elementAt(0).trim()] = keyValue.elementAt(1).trim()
            when (keyValue.elementAt(0)) {
                "weight" -> if (keyValue.elementAt(1).toInt() <= 0) outputInvalidAndFinish()
                "height" -> if (keyValue.elementAt(1).toInt() < 1) outputInvalidAndFinish()
            }
        }
        if (checkTunnel(attributes) || checkVillageName(attributes)) {
            outputInvalidAndFinish()
        }
        return attributes
    }

    /**
     * Check tunnel
     */
    private fun checkTunnel(attributes: MutableMap<String, String>): Boolean {
        return attributes.getValue("secondaryType") == "tunnel" && attributes.getValue("heightLimit").toDouble() > 3
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
        val intVertices = mutableListOf<Int>()
        for (strInt in stringVerticesList) {
            var n = strInt
            n = n.trim()
            if (n.isEmpty()) {
                continue
            }
            val k = n.toInt()
            intVertices.add(k)
        }
        val distinctVertices = intVertices.distinct()
        if (distinctVertices.count() != intVertices.count()) {
            return false
        }
        intVertices.forEach { int -> if (int < 0) return false }
        this.listOfVerticesData.addAll(intVertices)
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
            outputInvalidAndFinish()
        }
        this.digraphName = mapMatcher.group(2)
        return mapMatcher.group(Number.FOUR) // returns data in the scope
    }
}
