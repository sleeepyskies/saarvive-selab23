package de.unisaarland.cs.se.selab.parser

import de.unisaarland.cs.se.selab.dataClasses.emergencies.EmergencyType
import de.unisaarland.cs.se.selab.dataClasses.events.Event
import org.everit.json.schema.Schema
import org.everit.json.schema.loader.SchemaLoader
import org.json.*
import java.io.File

/**
 * Parses the event configuration file.
 * @param schemaFile the path to the JSON schema file
 * @param jsonFile the path to the JSON data file
 */
class EventParser(private val schemaFile: String, private val jsonFile: String) {
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

    fun parse(): List<Event> {
        val parsedEvents = mutableListOf<Event>()
        for (i in 0 until json.length()) {
            val jsonEvent = json.getJSONObject(i)
            schema.validate(jsonEvent)

            val eventType = jsonEvent.getEnum(

        }
    }


}