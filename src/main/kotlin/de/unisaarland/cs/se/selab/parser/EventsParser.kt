package de.unisaarland.cs.se.selab.parser

import de.unisaarland.cs.se.selab.dataClasses.events.Event
import de.unisaarland.cs.se.selab.dataClasses.events.RoadClosure
import de.unisaarland.cs.se.selab.dataClasses.events.RushHour
import de.unisaarland.cs.se.selab.dataClasses.events.TrafficJam
import de.unisaarland.cs.se.selab.dataClasses.events.VehicleUnavailable
import de.unisaarland.cs.se.selab.getSchema
import de.unisaarland.cs.se.selab.global.Log
import de.unisaarland.cs.se.selab.graph.PrimaryType
import org.everit.json.schema.Schema
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/**
 * Parses the Events configuration file.
 * @param schemaFile the path to the JSON schema file
 * @param jsonFile the path to the JSON data file
 */
class EventsParser(private val schemaFile: String, private val jsonFile: String) {
    private val schema: Schema
    private val json: JSONObject
    private var fileName = "" // for Logging
    val parsedEvents = mutableListOf<Event>()
    // a set of all emergency IDs to make sure they are unique

    private val eventIDSet = mutableSetOf<Int>()

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
     * Parses the events from the JSON file
     */
    fun parse(): List<Event> {
        try {
            parseEvents()
        } catch (_: IllegalArgumentException) {
            outputInvalidAndFinish()
        }
        Log.displayInitializationInfoValid(this.fileName)
        return parsedEvents
    }

    /** Parses the JSON data and returns a list of events
     */
    private fun parseEvents() {
        val eventsArray = json.getJSONArray("events")
        for (i in 0 until eventsArray.length()) {
            val jsonEvent = eventsArray.getJSONObject(i)
//            schema.validate(jsonEvent) -> detekt throws error
            // validation of single fields
            val id = validateEventId(jsonEvent.getInt("id"))
            val duration = validateDuration(jsonEvent.getInt("duration"))
            val startTick = validateEventTick(jsonEvent.getInt("tick"))
            val eventType = jsonEvent.getString("type")

            // parse single event
            when (eventType) {
                "RUSH_HOUR" -> {
                    val factor = validateEventFactor(jsonEvent.getInt("factor"))
                    val roadType = validateRoadTypes(jsonEvent.getJSONArray("roadTypes"))
                    // create a single RUSH HOUR event
                    val event = RushHour(id, duration, startTick, roadType, factor)
                    // add event to list of events
                    parsedEvents.add(event)
                }
                "TRAFFIC_JAM" -> {
                    val factor = validateEventFactor(jsonEvent.getInt("factor"))
                    val sourceId = validateSourceId(jsonEvent.getInt("source"))
                    val targetId = validateTargetId(jsonEvent.getInt("target"))
                    // create a single TRAFFIC JAM event
                    val event = TrafficJam(id, duration, startTick, factor, sourceId, targetId)
                    // add event to list of events
                    parsedEvents.add(event)
                }

                "ROAD_CLOSURE" -> {
                    val sourceId = validateSourceId(jsonEvent.getInt("source"))
                    val targetId = validateTargetId(jsonEvent.getInt("target"))
                    // create a single ROAD CLOSURE event
                    val event = RoadClosure(id, duration, startTick, sourceId, targetId)
                    // add event to list of events
                    parsedEvents.add(event)
                }
                "VEHICLE_UNAVAILABLE" -> {
                    val vehicleId = validateVehicleId(jsonEvent.getInt("vehicleID"))
                    // create a single VEHICLE UNAVAILABLE event
                    val event = VehicleUnavailable(id, duration, startTick, vehicleId)
                    // add event to list of events
                    parsedEvents.add(event)
                }
                else -> require(false) { "Invalid Event Type" }
            }
        }
    }

    /** Validates the duration of events
     * Checks whether the specified duration value belongs to the range of valid values.
     */
    private fun validateEventId(id: Int): Int {
        if (id < 0) {
            System.err.println("Event ID must be positive")
        } else if (eventIDSet.contains(id)) {
            System.err.println("Event ID must be unique")
        } else {
            eventIDSet.add(id)
        }
        return id
    }

    /** Validates the tick of events
     * Checks whether the specified tick value belongs to the range of valid values.
     */
    private fun validateEventTick(tick: Int): Int {
        if (tick < 0) {
            System.err.println("Event tick must be non-negative")
        }
        return tick
    }

    /** Validates duration of events
     */
    private fun validateDuration(duration: Int): Int {
        if (duration < 1) {
            System.err.println("Duration must be positive")
        }
        return duration
    }

    /** Validates the factor of events
     */
    private fun validateEventFactor(factor: Int): Int {
        if (factor < 1) {
            System.err.println("Factor must be positive")
        }
        return factor
    }

    /** Validates the road types of events
     * Checks whether the specified road type belongs to PrimaryType.
     */
    private fun validateRoadTypes(roadType: JSONArray): List<PrimaryType> {
        val validRoadTypes = listOf("MAIN_STREET", "SIDE_STREET", "COUNTY_ROAD")
        for (type in roadType) {
            if (type !in validRoadTypes) {
                System.err.println("Invalid road type")
            }
        }
        return roadType.map { enumValueOf(it.toString()) }
    }

    /** Validates the source ID of events
     * Will be changed after Ira is done with map
     */
    private fun validateSourceId(sourceId: Int): Int {
        if (sourceId < 0) {
            System.err.println("Source ID must be positive")
        }
        return sourceId
    }

    /** Validates the target ID of events
     * Will be changed after Ira is done with map
     */
    private fun validateTargetId(targetId: Int): Int {
        if (targetId < 0) {
            System.err.println("Target ID must be positive")
        }
        return targetId
    }

    /** Validates the vehicle ID of events
     * Will be changed after Min is done with map
     */
    private fun validateVehicleId(vehicleId: Int): Int {
        if (vehicleId < 0) {
            System.err.println("Vehicle ID must be positive")
        }
        return vehicleId
    }

    /**
     * Outputs invalidity log, terminates the program
     */
    private fun outputInvalidAndFinish() {
        Log.displayInitializationInfoInvalid(this.fileName)
        throw java.lang.IllegalArgumentException("Invalid simulator configuration")
    }
}
