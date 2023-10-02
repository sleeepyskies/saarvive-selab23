package de.unisaarland.cs.se.selab.parser

import de.unisaarland.cs.se.selab.dataClasses.emergencies.Emergency
import de.unisaarland.cs.se.selab.dataClasses.emergencies.EmergencyType
import de.unisaarland.cs.se.selab.getSchema
import de.unisaarland.cs.se.selab.global.Log
import de.unisaarland.cs.se.selab.graph.Graph
import org.everit.json.schema.Schema
import org.json.JSONObject
import java.io.File
import java.util.logging.Logger

/**
 * Parses the emergency configuration file.
 * @param schemaFile the path to the JSON schema file
 * @param jsonFile the path to the JSON data file
 */
class SimulationParser(private val schemaFile: String, private val jsonFile: String, private val graph: Graph) {
    private val schema: Schema
    private val json: JSONObject
    private var fileName = "" // for Logging
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

        schema.validate(json)
    }

    /**
     * Parses the emergencies from the JSON file
     */
    fun parse(): List<Emergency> {
        try {
            parseEmergencyCalls()
        } catch (_: IllegalArgumentException) {
            outputInvalidAndFinish()
        }
        return parsedEmergencies
    }

    /** Parses the JSON data and returns a list of emergencies, uses private method
     * to parse single emergencies.
     */
    internal fun parseEmergencyCalls() {
        val emergencyCallsArray = json.getJSONArray("emergencyCalls")

        for (i in 0 until emergencyCallsArray.length()) {
            val jsonEmergency = emergencyCallsArray.getJSONObject(i)

            if (validateEmergency(jsonEmergency)) {
                val id = jsonEmergency.getInt(keyId)
                val emergencyType = EmergencyType.valueOf(jsonEmergency.getString(keyType))
                val severity = jsonEmergency.getInt(keySeverity)
                val startTick = jsonEmergency.getInt(keyTick)
                val handleTime = jsonEmergency.getInt(keyHandleTime)
                val maxDuration = jsonEmergency.getInt(keyMaxDuration)
                val villageName = jsonEmergency.getString(keyVillage)
                val roadName = jsonEmergency.getString(keyRoadName)

                val emergency = Emergency(
                    id = id,
                    emergencyType = emergencyType,
                    severity = severity,
                    startTick = startTick,
                    handleTime = handleTime,
                    maxDuration = maxDuration,
                    villageName = villageName,
                    roadName = roadName
                )

                parsedEmergencies.add(emergency)
            } else {
                outputInvalidAndFinish()
            }
        }
    }

    private fun validateEmergency(jsonEmergency: JSONObject): Boolean {
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
    private fun validateEmergencyId(id: Int): Boolean {
        if (id < 0) {
            Logger.getLogger("Emergency ID must be positive")
            return false
        } else if (emergencyIDSet.contains(id)) {
            Logger.getLogger("Emergency ID must be unique")
            return false
        } else { emergencyIDSet.add(id) }
        return true
    }

    /** Validates the severity of emergencies
     * Checks whether the specified severity value belongs to the range of valid values.
     */
    private fun validateSeverity(severity: Int): Boolean {
        if (severity !in 1..3) {
            Logger.getLogger("Invalid severity level")
            return false
        }
        return true
    }

    /** Validates the tick of emergencies
     */
    private fun validateEmergencyTick(tick: Int): Boolean {
        if (tick <= 0) {
            Logger.getLogger("Emergency tick must be positive")
            return false
        }
        return true
    }

    /** Validates the emergency type of emergencies
     * Checks whether the specified emergency type belongs to EmergencyType.
     */
    private fun validateEmergencyType(emergencyType: String): Boolean {
        val validTypes = listOf("FIRE", "ACCIDENT", "CRIME", "MEDICAL")
        if (emergencyType !in validTypes) {
            Logger.getLogger("Invalid emergency type")
            return false
        }
        return true
    }

    /** Validates the handle time of emergencies
     */
    private fun validateHandleTime(handleTime: Int): Boolean {
        if (handleTime < 1) {
            Logger.getLogger("Handle time must be positive")
            return false
        }
        return true
    }

    /** Validates the maximum duration of emergencies, checks whether the specified maximum duration
     * is greater than the handle time.
     */
    private fun validateMaxDuration(maxDuration: Int, handleTime: Int): Boolean {
        if (maxDuration < handleTime) {
            Logger.getLogger("Maximum duration must be greater than handle time")
            return false
        }
        return true
    }

    /** Validates the village name of emergencies --> will be changes after Ira is done with Parsing
     */
    private fun validateVillageName(villageName: String): Boolean {
        val listOfVillages = mutableListOf(graph.roads.forEach { it.villageName })
        if (villageName !in listOfVillages.toString()) {
            Logger.getLogger("Invalid village name")
            return false
        } else if (villageName == "") {
            Logger.getLogger("Village name must not be empty")
            return false
        }
        return true
    }

    private fun outputInvalidAndFinish() {
        Log.displayInitializationInfoInvalid(this.fileName)
        throw java.lang.IllegalArgumentException("Invalid simulator configuration")
    }

    private fun validateRoadName(road: String): Boolean {
        val listValidRoads = mutableListOf(graph.roads.forEach { it.roadName })
        if (road !in listValidRoads.toString()) {
            Logger.getLogger("Invalid road name")
            return false
        } else if (road == "") {
            Logger.getLogger("Road name must not be empty")
            return false
        }
        return true
    }
}
