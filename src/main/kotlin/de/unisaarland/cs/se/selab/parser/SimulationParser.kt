package de.unisaarland.cs.se.selab.parser

import de.unisaarland.cs.se.selab.dataClasses.emergencies.Emergency
import de.unisaarland.cs.se.selab.dataClasses.emergencies.EmergencyType
import de.unisaarland.cs.se.selab.getSchema
import de.unisaarland.cs.se.selab.global.Log
import de.unisaarland.cs.se.selab.global.Number
import de.unisaarland.cs.se.selab.graph.Graph
import org.everit.json.schema.Schema
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File

/**
 * Parses the emergency configuration file.
 * @param schemaFile the path to the JSON schema file
 * @param jsonFile the path to the JSON data file
 */
class SimulationParser(private val schemaFile: String, private val jsonFile: String, private val graph: Graph) {
    private val schema: Schema
    private val json: JSONObject
    var fileName = "" // for Logging
    val parsedEmergencies = mutableListOf<Emergency>()

    // a set of all emergency IDs to make sure they are unique
    private val emergencyIDSet = mutableSetOf<Int>()

    // keys for the JSON data
    private val keyId = "id"
    private val keyTick = "tick"
    private val keyType = "emergencyType"
    private val keySeverity = "severity"
    private val keyHandleTime = "handleTime"
    private val keyMaxDuration = "maxDuration"
    private val keyVillage = "village"
    private val keyRoadName = "roadName"
    private val listInvalidKeys =
        listOf(
            "vehicleType", "baseID", "height", "staffCapacity", "waterCapacity", "ladderLength",
            "criminalCapacity", "dogs", "source", "target", "roadTypes", "oneWayStreet", "doctors",
            "baseType", "location", "staff", "vehicles"
        )

    init {
        // Load and parse the JSON schema
        try {
            this.fileName = File(jsonFile).name
        } catch (_: Exception) {
            outputInvalidAndFinish()
        }
        this.schema = getSchema(this.javaClass, schemaFile) ?: throw IllegalArgumentException("Schema not found")
        // Load and parse the JSON data
        val simulationJsonData = File(jsonFile).readText()
        json = JSONObject(simulationJsonData)
        try {
            schema.validate(json)
        } catch (_: Exception) {
            outputInvalidAndFinish()
        }
    }

    /**
     * Parses the emergencies from the JSON file
     */
    fun parse(): List<Emergency> {
        parseEmergencyCalls()
        return parsedEmergencies
    }

    /** Parses the JSON data and returns a list of emergencies, uses private method
     * to parse single emergencies.
     */
    fun parseEmergencyCalls() {
        var emergencyCallsArray: JSONArray
        try {
            emergencyCallsArray = json.getJSONArray("emergencyCalls")
        } catch (_: JSONException) {
            throw IllegalArgumentException("No emergencies found")
        }
        if (emergencyCallsArray.length() == 0) {
            System.err.println("No emergencies found")
            outputInvalidAndFinish()
        }
        for (i in 0 until emergencyCallsArray.length()) {
            val jsonEmergency = emergencyCallsArray.getJSONObject(i)

            if (validateEmergency(jsonEmergency) && checkForInvalidKeys(jsonEmergency, listInvalidKeys)) {
                val emergency = Emergency(
                    id = jsonEmergency.getInt(keyId),
                    emergencyType = EmergencyType.valueOf(jsonEmergency.getString(keyType)),
                    severity = jsonEmergency.getInt(keySeverity),
                    startTick = jsonEmergency.getInt(keyTick),
                    handleTime = jsonEmergency.getInt(keyHandleTime),
                    maxDuration = jsonEmergency.getInt(keyMaxDuration),
                    villageName = jsonEmergency.getString(keyVillage),
                    roadName = jsonEmergency.getString(keyRoadName)
                )
                parsedEmergencies.add(emergency)
            } else {
                outputInvalidAndFinish()
            }
        }
    }
    private fun checkForInvalidKeys(jsonObject: JSONObject, invalidKeys: List<String>): Boolean {
        for (key in invalidKeys) {
            if (jsonObject.has(key)) {
                System.err.println("Invalid key found in JSON data: $key")
                return false
            }
        }
        return true
    }

    /** Validates the JSON data of a single emergency.
     */
    fun validateEmergency(jsonEmergency: JSONObject): Boolean {
        val requiredFields =
            setOf(keyId, keyType, keySeverity, keyTick, keyHandleTime, keyMaxDuration, keyVillage, keyRoadName)
        val jsonFields = mutableSetOf<String>()
        for (key in jsonEmergency.keys()) {
            val keyString = key.toString()
            jsonFields.add(keyString)
        }
        if (!jsonFields.containsAll(requiredFields)) {
            System.err.println("Missing one or more required fields in the JSON emergency data.")
            return false
        }
        val id = jsonEmergency.getInt(keyId)
        val emergencyType = jsonEmergency.getString(keyType)
        val severity = jsonEmergency.getInt(keySeverity)
        val startTick = jsonEmergency.getInt(keyTick)
        val handleTime = jsonEmergency.getInt(keyHandleTime)
        val maxDuration = jsonEmergency.getInt(keyMaxDuration)
        val villageName = jsonEmergency.getString(keyVillage)
        val roadName = jsonEmergency.getString(keyRoadName)

        return validateEmergencyId(id) &&
            validateEmergencyType(emergencyType) &&
            validateSeverity(severity) &&
            validateEmergencyTick(startTick) &&
            validateHandleTime(handleTime) &&
            validateMaxDuration(maxDuration, handleTime) &&
            validateVillageName(villageName) &&
            validateRoadName(roadName)
    }

    /** Validates the ID of emergencies, check if it is unique.
     */
    fun validateEmergencyId(id: Int): Boolean {
        if (id < 0) {
            System.err.println("Emergency ID must be positive")
            return false
        } else if (emergencyIDSet.contains(id)) {
            System.err.println("Emergency ID must be unique")
            return false
        } else { emergencyIDSet.add(id) }
        return true
    }

    /** Validates the severity of emergencies
     * Checks whether the specified severity value belongs to the range of valid values.
     */
    fun validateSeverity(severity: Int): Boolean {
        if (severity !in 1..3) {
            System.err.println("Invalid severity level")
            return false
        }
        return true
    }

    /** Validates the tick of emergencies
     */
    fun validateEmergencyTick(tick: Int): Boolean {
        if (tick <= 0 || tick > Number.TOO_BIG) {
            System.err.println("Emergency tick must be positive")
            return false
        }
        return true
    }

    /** Validates the emergency type of emergencies
     * Checks whether the specified emergency type belongs to EmergencyType.
     */
    fun validateEmergencyType(emergencyType: String): Boolean {
        val validTypes = listOf("FIRE", "ACCIDENT", "CRIME", "MEDICAL")
        if (emergencyType !in validTypes) {
            System.err.println("Invalid emergency type")
            return false
        }
        return true
    }

    /** Validates the handle time of emergencies
     */
    fun validateHandleTime(handleTime: Int): Boolean {
        if (handleTime < 1 || handleTime > Number.TOO_BIG) {
            System.err.println("Handle time must be positive")
            return false
        }
        return true
    }

    /** Validates the maximum duration of emergencies, checks whether the specified maximum duration
     * is greater than the handle time.
     */
    fun validateMaxDuration(maxDuration: Int, handleTime: Int): Boolean {
        if (maxDuration <= handleTime || maxDuration < 2 || maxDuration > Number.TOO_BIG) {
            System.err.println("Maximum duration must be greater than handle time")
            return false
        }
        return true
    }

    /** Validates the village name of emergencies --> will be changes after Ira is done with Parsing
     */
    fun validateVillageName(villageName: String): Boolean {
        val listOfVillages = mutableListOf<String>()
        for (v in graph.roads) {
            listOfVillages.add(v.villageName)
        }
        if (villageName !in listOfVillages.toString()) {
            System.err.println("Invalid village name")
            return false
        } else if (villageName == "") {
            System.err.println("Village name must not be empty")
            return false
        }
        return true
    }

    /** Outputs an error message and terminates the program.
     */
    fun outputInvalidAndFinish() {
        Log.displayInitializationInfoInvalid(fileName)
        throw IllegalArgumentException("Invalid simulator configuration")
    }

    /** Validates the road name of emergencies
     */
    fun validateRoadName(road: String): Boolean {
        val listValidRoads = mutableListOf<String>()
        for (r in graph.roads) {
            listValidRoads.add(r.roadName)
        }
        if (road !in listValidRoads.toString()) {
            System.err.println("Invalid road name")
            return false
        } else if (road == " ") {
            System.err.println("Road name must not be empty")
            return false
        }
        return true
    }
}
