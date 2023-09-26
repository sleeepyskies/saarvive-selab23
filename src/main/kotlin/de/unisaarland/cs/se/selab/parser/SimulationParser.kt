package de.unisaarland.cs.se.selab.parser
import de.unisaarland.cs.se.selab.dataClasses.emergencies.Emergency
import de.unisaarland.cs.se.selab.dataClasses.emergencies.EmergencyType
import de.unisaarland.cs.se.selab.dataClasses.events.*
import org.everit.json.schema.Schema
import org.everit.json.schema.loader.SchemaLoader
import org.json.*
import java.io.File
import java.util.*
import javax.print.attribute.standard.Severity


/**
 * Parses the emergency configuration file.
 * @param schemaFile the path to the JSON schema file
 * @param jsonFile the path to the JSON data file
 */
class SimulationParser(private val schemaFile: String, private val jsonFile: String) {
    private val schema: Schema
    private val json: JSONObject
    private val id = "id"
    private val duration = "duration"
    private val startTick = "startTick"

    init {
        // Load and validate the JSON schema
        val schemaJson = JSONObject(File(schemaFile).readText())
        schema = SchemaLoader.load(schemaJson)

        // Load and parse the JSON data
        val jsonData = File(jsonFile).readText()
        json = JSONObject(jsonData)

        schema.validate(json)
    }

    /** Parses the JSON data and returns a list of emergencies, uses private method
     * to parse single emergencies.
     */
    fun parseEmergencyCalls(): List<Emergency> {
        val emergencyCallsArray = json.getJSONArray("emergencyCalls")
        val parsedEmergencies = mutableListOf<Emergency>()
            for (i in 0 until json.length()) {
                val jsonEmergency = emergencyCallsArray.getJSONObject(i)
                // parse single emergency
                val emergency = parseEmergency(jsonEmergency)
                // add emergency to list of emergencies
                parsedEmergencies.add(emergency)
            }
        return parsedEmergencies
    }

    /** Parses the JSON data and returns an emergency, that is added to the list of emergencies.
     */
    private fun parseEmergency(jsonEmergency: JSONObject): Emergency{
        val emergency = Emergency(
            id = validateId(jsonEmergency.getInt("id")),
            emergencyType = EmergencyType.valueOf(jsonEmergency.getString("emergencyType")),
            severity = validateSeverity(jsonEmergency.getInt("severity")),
            startTick = jsonEmergency.getInt("startTick"),
            handleTime = jsonEmergency.getInt("handleTime"),
            maxDuration = jsonEmergency.getInt("maxDuration"),
            villageName = jsonEmergency.getString("villageName"),
            roadName = jsonEmergency.getString("roadName")
        )
        return emergency
    }

    /** Parses the JSON data and returns a list of events, uses private method
     * to parse single events.
     */
    fun parseEvents():List<Event> {
        val eventsArray = json.getJSONArray("events")
        val parsedEvents = mutableListOf<Event>()
        for (i in 0 until json.length()) {
            val jsonEvent = eventsArray.getJSONObject(i)
            // parse single event
            val event = parseEvent(jsonEvent)
            // add event to list of events
            parsedEvents.add(event)
        }
        return parsedEvents
    }

    /** Parses the JSON data and returns an event, that is added to the list of events.
     * Uses private methods to parse single events according to their type.
     */
    private fun parseEvent(jsonEvent: JSONObject): Event{
        val event = when (val eventType = jsonEvent.getString("type")) {
            "RUSH_HOUR" -> parseRushHour(jsonEvent)
            "TRAFFIC_JAM" -> parseTrafficJam(jsonEvent)
            "ROAD_CLOSURE" -> parseRoadClosure(jsonEvent)
            "VEHICLE_UNAVAILABLE" -> vehicleUnavailable(jsonEvent)
            else -> error("Unknown event type: $eventType")
        }
        return event
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

    /** Validates the ID of emergencies and events.
     */
    private fun validateId(id: Int): Int {
        if (id < 0) {
            error("ID must be positive")
        }
        return id
    }

    /** Validates the severity of emergencies
     * Checks whether the specified severity value belongs to the range of valid values.
     */
    private fun validateSeverity(severity: Int): Int {
        if (severity !in 1..3) {
            error("Severity must be positive")
        }
        return severity
    }
}
