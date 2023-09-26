package de.unisaarland.cs.se.selab.parser
import de.unisaarland.cs.se.selab.dataClasses.emergencies.Emergency
import de.unisaarland.cs.se.selab.dataClasses.emergencies.EmergencyType
import de.unisaarland.cs.se.selab.dataClasses.events.*
import org.everit.json.schema.Schema
import org.everit.json.schema.loader.SchemaLoader
import org.json.*
import java.io.File
import java.util.*

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
    // a set of all emergency IDs to make sure they are unique
    private val emergencyIDSet = mutableSetOf<Int>()

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
                // Validation of fields
                val id = validateId(jsonEmergency.getInt("id"))
                val emergencyType = validateEmergencyType(jsonEmergency.getString("emergencyType"))
                val severity = validateSeverity(jsonEmergency.getInt("severity"))
                val startTick = validateEmergencyTick(jsonEmergency.getInt("startTick"))
                val handleTime = validateHandleTime(jsonEmergency.getInt("handleTime"))
                // create a single emergency
                val emergency = Emergency(
                    id = id,
                    emergencyType = emergencyType,
                    severity = severity,
                    startTick = startTick,
                    handleTime = handleTime,
                    maxDuration = jsonEmergency.getInt("maxDuration"),
                    villageName = jsonEmergency.getString("villageName"),
                    roadName = jsonEmergency.getString("roadName")
                )
                parsedEmergencies.add(emergency)
            }
        return parsedEmergencies
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
        else if (emergencyIDSet.contains(id)) {
            error("ID must be unique")
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

    /** Validates the tick of emergencies
     */
    private fun validateEmergencyTick(tick: Int): Int {
        if (tick < 1) {
            error("Tick must be greater than 1")
        }
        return tick
    }

    /** Validates the emergency type of emergencies
     * Checks whether the specified emergency type belongs to EmergencyType.
     */
    private fun validateEmergencyType(emergencyType: String): EmergencyType{
        if (emergencyType != EmergencyType.valueOf(emergencyType).toString()) {
            error("EmergencyType must be one of the following: ${EmergencyType.values()}")
        }
        return EmergencyType.valueOf(emergencyType)
    }

    /** Validates the handle time of emergencies
     */
    private fun validateHandleTime(handleTime: Int): Int {
        if (handleTime < 1) {
            error("Minimum handle time is 1")
        }
        return handleTime
    }



}
