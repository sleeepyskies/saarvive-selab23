package de.unisaarland.cs.se.selab.parser

import de.unisaarland.cs.se.selab.dataClasses.events.*
import org.everit.json.schema.Schema
import org.everit.json.schema.loader.SchemaLoader
import org.json.*
import java.io.File
import java.util.*

/**
 * Parses the event configuration file.
 * @param schemaFile the path to the JSON schema file
 * @param jsonFile the path to the JSON data file
 */
class EventParser(private val schemaFile: String, private val jsonFile: String) {
    private val schema: Schema
    private val json: JSONArray
    private val id = "id"
    private val duration = "duration"
    private val startTick = "startTick"


    init {
        // Load and validate the JSON schema
        val schemaJson = JSONObject(File(schemaFile).readText())
        schema = SchemaLoader.load(schemaJson)

        // Load and parse the JSON data
        val jsonData = File(jsonFile).readText()
        json = JSONArray(jsonData)
    }

    /** Parses the JSON data and returns a list of events.
     * @return a list of events
     */
    fun parse(): List<Event> {
        val parsedEvents = mutableListOf<Event>()
        for (i in 0 until json.length()) {
            val jsonEvent = json.getJSONObject(i)
            schema.validate(jsonEvent)

            val event = when (val eventType = jsonEvent.getString("type")) {
                "RUSH_HOUR" -> parseRushHour(jsonEvent)
                "TRAFFIC_JAM" -> parseTrafficJam(jsonEvent)
                "ROAD_CLOSURE" -> parseRoadClosure(jsonEvent)
                "VEHICLE_UNAVAILABLE" -> vehicleUnavailable(jsonEvent)
                else -> error("Unknown event type: $eventType")
                }
            parsedEvents.add(event)
        }
        return parsedEvents
    }

    private fun parseRushHour(jsonEvent: JSONObject): RushHour {
        return RushHour(
            eventID = jsonEvent.getInt(id),
            factor = jsonEvent.getInt("factor"),
            duration = jsonEvent.getInt(duration),
            startTick = jsonEvent.getInt(startTick),
            roadType = enumValueOf(jsonEvent.getString("roadType").uppercase(Locale.getDefault()))
            // ask about enum valueOf
        )
        }

    private fun parseTrafficJam(jsonEvent: JSONObject): TrafficJam {
        return TrafficJam(
            eventID = jsonEvent.getInt(id),
            factor = jsonEvent.getInt("factor"),
            duration = jsonEvent.getInt(duration),
            startTick = jsonEvent.getInt(startTick),
            sourceID = jsonEvent.getInt("source"),
            targetID = jsonEvent.getInt("target")
        )
    }

    private fun parseRoadClosure(jsonEvent: JSONObject): RoadClosure {
        return RoadClosure(
            eventID = jsonEvent.getInt(id),
            duration = jsonEvent.getInt(duration),
            startTick = jsonEvent.getInt(startTick),
            sourceID = jsonEvent.getInt("source"),
            targetID = jsonEvent.getInt("target")
        )
    }

    private fun vehicleUnavailable(jsonEvent: JSONObject): VehicleUnavailable {
        return VehicleUnavailable(
            eventID = jsonEvent.getInt(id),
            duration = jsonEvent.getInt(duration),
            startTick = jsonEvent.getInt(startTick),
            vehicleID = jsonEvent.getInt("vehicleID")
        )
    }
}