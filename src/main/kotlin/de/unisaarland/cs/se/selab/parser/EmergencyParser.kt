package de.unisaarland.cs.se.selab.parser
import de.unisaarland.cs.se.selab.dataClasses.emergencies.Emergency
import de.unisaarland.cs.se.selab.dataClasses.emergencies.EmergencyType
import org.everit.json.schema.Schema
import org.everit.json.schema.loader.SchemaLoader
import org.json.*
import java.io.File

/**
 * Exception thrown when the configuration file is invalid.
 */
class ValidationException(message: String) : Exception(message)

/**
 * Parses the emergency configuration file.
 * @param schemaFile the path to the JSON schema file
 * @param jsonFile the path to the JSON data file
 */
class EmergencyParser(private val schemaFile: String, private val jsonFile: String) {
    private val schema: Schema
    private val json: JSONArray

    init {
        // Load and validate the JSON schema
        val schemaJson = JSONObject(File(schemaFile).readText())
        schema = SchemaLoader.load(schemaJson)

        // Load and parse the JSON data
        val jsonData = File(jsonFile).readText()
        json = JSONArray(jsonData)
    }

    /** Parses the JSON data and returns a list of emergencies.
     * @return a list of emergencies
     * @throws ValidationException if the JSON data is invalid
     */
    fun parse(): List<Emergency> {
        val parsedEmergencies = mutableListOf<Emergency>()
        try {
            for (i in 0 until json.length()) {
                val jsonEmergency = json.getJSONObject(i)
                schema.validate(jsonEmergency)

                val emergency = Emergency(
                    id = jsonEmergency.getInt("id"),
                    emergencyType = EmergencyType.valueOf(jsonEmergency.getString("emergencyType")),
                    severity = jsonEmergency.getInt("severity"),
                    startTick = jsonEmergency.getInt("startTick"),
                    handleTime = jsonEmergency.getInt("handleTime"),
                    maxDuration = jsonEmergency.getInt("maxDuration"),
                    villageName = jsonEmergency.getString("villageName"),
                    roadName = jsonEmergency.getString("roadName")
                )
                parsedEmergencies.add(emergency)
            }
        } catch (e: ValidationException) {
            println("Invalid emergency configuration file${e.message}")
        }
        return parsedEmergencies
    }
}
