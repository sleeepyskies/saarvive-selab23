package de.unisaarland.cs.se.selab.parser

import de.unisaarland.cs.se.selab.dataClasses.events.Event
import de.unisaarland.cs.se.selab.dataClasses.events.RushHour
import de.unisaarland.cs.se.selab.dataClasses.events.TrafficJam
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

            val eventType = jsonEvent.getString("type")
            val event = when (eventType) {
                "RUSH_HOUR" -> parseRushHour(jsonEvent)
                "TRAFFIC_JAM" -> parseTrafficJam(jsonEvent)
                else -> error("Unknown event type: $eventType")
                }
            parsedEvents.add(event)
        }
    }

    private fun parseRushHour(jsonEvent: JSONObject): RushHour {
        return RushHour(
            eventID = jsonEvent.getInt("id"),
            factor = jsonEvent.getInt("factor"),
            duration = jsonEvent.getInt("duration"),
            tick = jsonEvent.getInt("tick"),
            roadType = jsonEvent.get
        )
    }

    private fun parseTrafficJam(jsonEvent: JSONObject): TrafficJam {
        return TrafficJam(
            eventID = jsonEvent.getInt("id"),
            factor = jsonEvent.getInt("factor"),
            duration = jsonEvent.getInt("duration"),
            tick = jsonEvent.getInt("tick"),
            sourceID = jsonEvent.getString("source"),
            targetID = jsonEvent.getString("target"),
            roadType = jsonEvent.get
        )
    }





}