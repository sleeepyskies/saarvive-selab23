package de.unisaarland.cs.se.selab.parser

import de.unisaarland.cs.se.selab.global.Log
import de.unisaarland.cs.se.selab.global.StringLiterals
import de.unisaarland.cs.se.selab.graph.Graph
import de.unisaarland.cs.se.selab.graph.PrimaryType
import de.unisaarland.cs.se.selab.graph.Road
import de.unisaarland.cs.se.selab.graph.SecondaryType
import de.unisaarland.cs.se.selab.graph.Vertex
import java.io.File
import java.lang.IllegalArgumentException
import java.util.regex.Pattern

/**
 * Class for parsing the .dot file that contains information about Map. Receives [dotFilePath] as path to the .dot data
 * file
 */
class CountyParser(private val dotFilePath: String) {
    private var fileName: String = "" // For logging
    private var data: String = "" // Data from the file in String
    private val roads = mutableListOf<Road>() // List of roads on the map (for compatability)
    private val vertices = mutableListOf<Vertex>() // List of roads on the map (for compatability)
    var digraphName: String = ""

    private val listOfVerticesData =
        mutableListOf<Int>() // List of vertices in string (whenever work with Vertices, work with this str)
    private val listOfVerticesToRoads = mutableMapOf<Pair<Int, Int>, Road>() // List of mapping vertices ids to the road
    private val listOfRoadAttributes = mutableListOf(mutableMapOf<String, String>()) // Data in strings for validation
    private val idToVertexMapping = mutableMapOf<Int, Vertex>() // For easies access to the Vertex object

    private val sPat = "[a-zA-Z][a-zA-Z_]*" // Pattern for strings ID
    private val nPat = "\\d+" // Pattern for numbers ID

    /**
     * Save the file and data in string
     */
    init {
        try {
            // Convert path to a file
            this.fileName = File(dotFilePath).name
            this.data = File(dotFilePath).readText()
        } catch (_: Exception) {
            // Something wrong with a file/ filepath
            outputInvalidAndFinish()
        }
    }

    /**
     * Runs the parser. Receives results in parsing, validating, creates objects and returns Graph
     */
    fun parse(): Graph {
        val dataInScope = retrieveDataInScope() // Get the string of the "in-scope" data
        if (!parsedAndValid(dataInScope)) { // Checks for parsing and validation
            System.err.println("Parsing and validation invalid. Error called in parse()")
            outputInvalidAndFinish()
        }
        Log.displayInitializationInfoValid(this.fileName)
        this.listOfVerticesData.forEach { vertex -> this.vertices.add(createVertex(vertex)) } // create Vertex List
        addConnectionsToVertices() // Adds connections needed for each Vertex object
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
            StringLiterals.SIDE_STREET -> PrimaryType.SIDE_STREET
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
        val vPat = Pattern.compile("\\A(\\s*\\d+\\s*;)\\s*") // Pattern for vertex

        var stringEdges = dataInScope // will delete first matching lines for vertices
        var stringVertices = ""
        while (vPat.matcher(stringEdges).find()) {
            val vertexFound = vPat.toRegex().find(stringEdges)
            val startIndex = (vertexFound?.range?.last ?: 1) + 1
            stringVertices += vertexFound?.groupValues?.get(0).orEmpty()
            stringEdges =
                stringEdges.takeIf { startIndex <= it.length }?.substring(startIndex).orEmpty()
        }

        // String for parsing edges part
        if (!stringEdges.isEmpty() || !stringVertices.isEmpty()) {
            val parsedVertices = parseVertices(stringVertices)
            val parsedEdges = parseEdges(stringEdges)
            if (!roadNameIsUnique() || !commonVertex()) {
                System.err.println("Road name is not unique or vertex is not common. Called in parsedAndValid().")
                return false
            }
            if (!villageHasMainStreet() || !sideStreetExists()) {
                System.err.println(
                    "Village has no main street or side street does not exist. " +
                        "Called in parsedAndValid()."
                )
                return false
            }
            return if (parsedEdges && parsedVertices == true) {
                vertexConnectedToAnother() && edgesConnectExistingVertices()
            } else {
                System.err.println("Validating invalid. Called in parsedAndValid().")
                false
            }
        }
        System.err.println("String are empty. Called in parsedAndValid().")
        return false
    }

    /**
     * (6. All edges connect two existing vertices)
     */
    private fun edgesConnectExistingVertices(): Boolean {
        this.listOfVerticesToRoads.keys.forEach { key ->
            if (!(this.listOfVerticesData.contains(key.first) && this.listOfVerticesData.contains(key.second))) {
                System.err.println("Edge does not connect existing vertices. Called in edgesConnectExistingVertices().")
                return false
            }
        }
        return true
    }

    /**
     * (2. Each vertex is connected to at least one other vertex)
     */
    private fun vertexConnectedToAnother(): Boolean {
        this.listOfVerticesData.forEach { vertex ->
            var connects = false
            this.listOfVerticesToRoads.keys.forEach { pair ->
                if (pair.first == vertex || pair.second == vertex) {
                    connects = true
                }
            }
            if (!connects) {
                System.err.println("Vertex is not connected to another vertex. Called in vertexConnectedToAnother().")
                return false
            }
        }
        return true
    }

    /**
     * Parses and validates Edges individually and creates mapping
     */
    private fun parseEdges(stringEdges: String): Boolean {
        val edgePattern =
            Pattern.compile("\\A\\s*($nPat)\\s*->\\s*($nPat)\\s*\\[(\\s*[^\\]]+\\s*)\\]\\s*;\\s*")
// Pattern for individual edge
        var stringEdgesForChanges = stringEdges
        while (edgePattern.matcher(stringEdgesForChanges).find()) { // Get one "edge" by one
            val edgeFound = edgePattern.toRegex().find(stringEdgesForChanges)
            val startIndex = (edgeFound?.range?.last ?: 1) + 1
            val id1 = edgeFound?.groupValues?.get(1).orEmpty().trim().toInt() // The 1st vertex
            val id2 = edgeFound?.groupValues?.get(2).orEmpty().trim().toInt() // The 2nd vertex
            if (id1 == id2) {
                System.err.println("Edge connects vertex to itself. Called in parseEdges().")
                return false
            } // (4. No edges from one vertex to itself)
            val check1 = Pair(id1, id2) // Check for already existing pair in the list of roads
            val check2 = Pair(id2, id1) // Check for already existing pair in the list of roads
            val matchedEdge = edgeFound?.groupValues?.get(3).orEmpty() // Attributes
            if (this.listOfVerticesToRoads.containsKey(check1) ||
                this.listOfVerticesToRoads.containsKey(check2) || !attributesCanBeParsed(matchedEdge)
            ) {
                System.err.println(
                    "There is more than one edge between edges or attributes are not parsing. Called in parseEdges()."
                )
                return false // (5. There is at most one edge between two vertices)
            }
            val attributesMapping = parseAttributes(matchedEdge) // Parse attributes
            this.listOfRoadAttributes.add(attributesMapping)
            val singleRoadObj = createRoad(attributesMapping) // Create road obj
            this.roads.add(singleRoadObj)
            this.listOfVerticesToRoads[Pair(id1, id2)] = singleRoadObj

            // Remove already read data from the string
            stringEdgesForChanges =
                stringEdgesForChanges.takeIf { startIndex <= it.length }?.substring(startIndex).orEmpty()
        }
        if (!stringEdgesForChanges.matches("\\s*".toRegex())) {
            System.err.println("Edges syntax failed. Called in parseEdges().")
            outputInvalidAndFinish()
        }
        this.listOfRoadAttributes.removeAt(0) // Remove empty obj
        return true
    }

    /**
     * (8. Each village has at least one road with type mainStreet)
     */
    private fun villageHasMainStreet(): Boolean {
        val villageToRoadTypeMap = mutableMapOf<String, Boolean>()
        for (singleData in this.listOfRoadAttributes) {
            if (!villageToRoadTypeMap.contains(singleData.get(StringLiterals.VILLAGE)) &&
                singleData.get(StringLiterals.VILLAGE) != digraphName
            ) {
                villageToRoadTypeMap[singleData.getOrDefault(StringLiterals.VILLAGE, StringLiterals.ERROR)] =
                    singleData.getOrDefault(StringLiterals.PRIMARY_TYPE, StringLiterals.ERROR) ==
                    StringLiterals.MAIN_STREET
            } else {
                if (singleData.getValue(StringLiterals.PRIMARY_TYPE) == StringLiterals.MAIN_STREET &&
                    singleData.get(StringLiterals.VILLAGE) != digraphName
                ) {
                    villageToRoadTypeMap.put(singleData.getValue(StringLiterals.VILLAGE), true)
                }
            }
        }

        return villageToRoadTypeMap.values.all { it }
    }

    /**
     * Checks if the road name is unique within a village. (3)
     */
    private fun roadNameIsUnique(): Boolean {
        val mapping = mutableMapOf<String, MutableList<String>>()
        // String - village name, MutableList<String> - names of the roads in the village
        this.listOfRoadAttributes.forEach { dataPiece ->
            if (mapping.containsKey(dataPiece.getOrDefault(StringLiterals.VILLAGE, StringLiterals.ERROR))) {
                if (
                    mapping[dataPiece.getValue(StringLiterals.VILLAGE)]
                        ?.contains(dataPiece.getValue(StringLiterals.NAME)) == true
                ) {
                    System.err.println("Road name is not unique within the village. Called in roadNameIsUnique().")
                    return false
                } else {
                    mapping[dataPiece.getValue(StringLiterals.VILLAGE)]?.add(dataPiece.getValue("name")) ?: "Saarburg"
                }
            } else {
                val newMutableList = mutableListOf<String>()
                val newVillageName = dataPiece.getOrDefault(StringLiterals.VILLAGE, "ERROR")
                val newRoadName = dataPiece.getOrDefault(StringLiterals.NAME, "ERROR")
                newMutableList.add(newRoadName)
                mapping[newVillageName] = newMutableList
            }
        }
        return true
    }

    /**
     * (7. All edges connected to the same vertex belong to the same village or are a countyRoad)
     */
    private fun commonVertex(): Boolean {
        val mappingVertexToEdges =
            mutableMapOf<Int, MutableList<MutableMap<String, String>>>()
        // Int - vertex id, Mutable map - for primary type and village name
        for ((key, road) in listOfVerticesToRoads) {
            val (firstVertex, secondVertex) = key
            listOf(firstVertex, secondVertex).forEach {
                var primaryT = ""
                when (road.pType.toString()) {
                    "MAIN_STREET" -> primaryT = "mainStreet"
                    "SIDE_STREET" -> primaryT = "sideStreet"
                    "COUNTY_ROAD" -> primaryT = StringLiterals.COUNTY_ROAD
                }
                mappingVertexToEdges.computeIfAbsent(it) { mutableListOf() }
                    .add(
                        mutableMapOf(
                            StringLiterals.VILLAGE to road.villageName,
                            StringLiterals.PRIMARY_TYPE to primaryT
                        )
                    )
            }
        }

        return mappingVertexToEdges.all { (_, edges) ->
            val filteredWithoutCountyRoad = edges.filter {
                it[StringLiterals.PRIMARY_TYPE] != StringLiterals.COUNTY_ROAD
            }
            val uniqueVillageCount = filteredWithoutCountyRoad.map { it[StringLiterals.VILLAGE] }.distinct().size
            uniqueVillageCount == 1
        }
    }

    /**
     * Checks if map has at least one sideStreet road (9)
     */
    private fun sideStreetExists(): Boolean {
        for (edge in this.listOfRoadAttributes) {
            if (edge.getValue(StringLiterals.PRIMARY_TYPE) == "sideStreet") {
                return true
            }
        }
        System.err.println("Map has no sideStreet road. Called in sideStreetExists().")
        return false
    }

    /**
     * Returns bool value if the attributes can be parsed
     */
    private fun attributesCanBeParsed(matchedEdge: String): Boolean {
        val vilPat = Pattern.compile("\\s*village\\s*=\\s*($sPat)\\s*;") // Pattern for village
        val namPat = Pattern.compile("\\s*name\\s*=\\s*($sPat)\\s*;") // Pattern for name
        val hPat = Pattern.compile("\\s*heightLimit\\s*=\\s*($nPat)\\s*;") // Pattern for height
        val wPat = Pattern.compile("\\s*weight\\s*=\\s*($nPat)\\s*;") // Pattern for weight
        val pTPat = Pattern.compile("\\s*primaryType\\s*=\\s*(mainStreet|sideStreet|countyRoad)\\s*;")
        // Pattern for primaryType
        val sTPat = Pattern.compile("\\s*secondaryType\\s*=\\s*(oneWayStreet|tunnel|none)\\s*;")
        // Pattern for secondaryType
        val atPat = Pattern.compile("\\A$vilPat$namPat$hPat$wPat$pTPat$sTPat\\Z") // Pattern for attributes

        val artPatter = atPat.matcher(matchedEdge)
        return artPatter.find() // Result of required syntax
    }

    /**
     * Outputs invalidity log, terminates the program
     */
    private fun outputInvalidAndFinish() {
        Log.displayInitializationInfoInvalid(this.fileName)
        throw IllegalArgumentException("Invalid map")
    }

    /**
     * Returns Map of parsed attributes
     */
    private fun parseAttributes(matchedEdge: String): MutableMap<String, String> {
        val attributes = mutableMapOf<String, String>()
        val assignmentsArray = matchedEdge.split(";").filter { it.isNotEmpty() }
        assignmentsArray.forEach { assignment ->
            val keyValue = assignment.split("=") // Retrieve keys
            attributes[keyValue.elementAt(0).trim()] = keyValue.elementAt(1).trim() // Put attributes in mapping
            when (keyValue.elementAt(0)) {
                "weight" -> if (keyValue.elementAt(1).toInt() <= 0
                ) {
                    outputInvalidAndFinish() // (10. The weight of the road must be greater than 0)
                }

                "height" -> if (keyValue.elementAt(1).toInt() < 1) {
                    outputInvalidAndFinish() // (11. The height of the road is at least 1)
                }
            }
        }
        if (!tunnelIsValid(attributes) || !villageNameIsValid(attributes)) {
            System.err.println("Tunnel attributes or village name are invalid. Called in parseAttributes().")
            outputInvalidAndFinish()
        }
        return attributes
    }

    /**
     * Check tunnel requirements
     */
    private fun tunnelIsValid(attributes: MutableMap<String, String>): Boolean {
        if (attributes.getValue("secondaryType") == "tunnel") { // (12. The height of a tunnel is at most 3)
            if (attributes.getValue("heightLimit").toInt() <= 3) {
                return true
            }
            System.err.println("Tunnel height is invalid. Called in tunnelIsValid(). Height of a tunnel is at most 3")
            return false
        }
        return true
    }

    /**
     * Checks village name (13. No village name is equal to a county name)
     */
    private fun villageNameIsValid(attributes: MutableMap<String, String>): Boolean {
        if (attributes.getValue("village") != this.digraphName) {
            return attributes.getValue("primaryType") != "countyRoad"
        }
        return attributes.getValue("primaryType") == "countyRoad"
    }

    /**
     * Parses and validates Vertices individually and creates a list of Int
     */
    private fun parseVertices(stringVertices: String): Boolean {
        val stringVerticesList = stringVertices.split(";")
        val intVertices = mutableListOf<Int>() // List for int vertices values
        for (strInt in stringVerticesList) { // Convert str ids to int
            var n = strInt
            n = n.trim()
            if (n.isEmpty()) {
                continue
            }
            val k = n.toInt()
            intVertices.add(k)
        }
        val distinctVertices = intVertices.distinct() // The number of unique numbers (1. validation)
        if (distinctVertices.count() != intVertices.count()) {
            System.err.println("There are duplicate vertices. Called in parseVertices().")
            return false
        }
        intVertices.forEach { int ->
            if (int < 0) {
                System.err.println("Vertex id is negative. Called in parseVertices().")
                return false
            }
        } // The smallest possible id is 10 (1. validation)
        this.listOfVerticesData.addAll(intVertices) // Update empty list for global attribute
        return true
    }

    /**
     * Returns only data in scopes
     */
    private fun retrieveDataInScope(): String {
        val mapPat =
            Pattern.compile("\\A\\s*digraph\\s+($sPat|$nPat)\\s*\\{([^}]+)\\}\\s*\\Z") // Pattern for map

        val mapMatcher = mapPat.matcher(this.data)
        if (!mapMatcher.find()) {
            outputInvalidAndFinish() // General structure is wrong
        }
        this.digraphName = mapMatcher.group(1) // Puts the digraph name
        return mapMatcher.group(2) // Returns data in the scope
    }
}
