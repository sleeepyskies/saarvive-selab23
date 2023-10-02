package de.unisaarland.cs.se.selab.parser

import de.unisaarland.cs.se.selab.dataClasses.events.Construction
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
import java.util.logging.Logger

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

    // keys for the JSON data
    private val keyId = "id"
    private val keyTick = "tick"
    private val keyType = "type"
    private val keyFactor = "factor"
    private val keyMaxDuration = "duration"
    private val keySource = "source"
    private val keyTarget = "target"
    private val keyVehicleID = "vehicleID"
    private val keyRoadTypes = "roadTypes"
    private val keyOneWay = "oneWayStreet"

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
            if (validateEvent(jsonEvent)) {
                val event = createEvent(jsonEvent)
                parsedEvents.add(event)
            } else {
                outputInvalidAndFinish()
            }
        }
    }

    private fun validateEvent(jsonEvent: JSONObject): Boolean {
        val id = jsonEvent.getInt(keyId)
        val eventType = jsonEvent.getString(keyType)

        when (eventType) {
            "RUSH_HOUR" -> {
                val factor = jsonEvent.getInt(keyFactor)
                val roadTypesArray = jsonEvent.getJSONArray(keyRoadTypes)
                return validateRushHourEvent(id, jsonEvent, factor, roadTypesArray)
            }
            "TRAFFIC_JAM", "ROAD_CLOSURE" -> {
                val source = jsonEvent.getInt(keySource)
                val target = jsonEvent.getInt(keyTarget)
                return validateOtherEvents(id, jsonEvent, source, target)
            }
            "VEHICLE_UNAVAILABLE" -> {
                val vehicleId = jsonEvent.getInt(keyVehicleID)
                return validateVehicleUnavailableEvent(id, jsonEvent, vehicleId)
            }
            "CONSTRUCTION_SITE" -> {
                return validateConstructionEvent(id, jsonEvent)
            }
            else -> return false
        }
    }

    private fun validateRushHourEvent(id: Int, jsonEvent: JSONObject, factor: Int, roadTypesArray: JSONArray): Boolean {
        val duration = jsonEvent.getInt(keyMaxDuration)
        val tick = jsonEvent.getInt(keyTick)
        val valid1 = validateEventId(id) && validateDuration(duration)
        val valid2 = validateEventTick(tick) && validateEventFactor(factor) && validateRoadTypes(roadTypesArray)
        return valid1 && valid2
    }
    private fun validateOtherEvents(id: Int, jsonEvent: JSONObject, source: Int, target: Int): Boolean {
        val duration = jsonEvent.getInt(keyMaxDuration)
        val tick = jsonEvent.getInt(keyTick)
        val valid1 = validateEventId(id) && validateDuration(duration)
        val valid2 = validateEventTick(tick) && validateSourceAndTarget(source, target)
        return valid1 && valid2
    }
    private fun validateVehicleUnavailableEvent(id: Int, jsonEvent: JSONObject, vehicleId: Int): Boolean {
        val duration = jsonEvent.getInt(keyMaxDuration)
        val tick = jsonEvent.getInt(keyTick)
        val valid1 = validateEventId(id) && validateDuration(duration)
        val valid2 = validateEventTick(tick) && validateVehicleId(vehicleId)
        return valid1 && valid2
    }
    private fun validateConstructionEvent(id: Int, jsonEvent: JSONObject): Boolean {
        val duration = jsonEvent.getInt(keyMaxDuration)
        val tick = jsonEvent.getInt(keyTick)
        val source = jsonEvent.getInt(keySource)
        val target = jsonEvent.getInt(keyTarget)
        val factor = jsonEvent.getInt(keyFactor)
        val valid1 = validateEventId(id) && validateDuration(duration) && validateEventFactor(factor)
        val valid2 = validateEventTick(tick) && validateSourceAndTarget(source, target)
        return valid1 && valid2
    }
    private fun createEvent(jsonEvent: JSONObject): Event {
        val id = jsonEvent.getInt(keyId)
        val eventType = jsonEvent.getString(keyType)
        when (eventType) {
            "RUSH_HOUR" -> {
                val factor = jsonEvent.getInt(keyFactor)
                val roadTypesArray = jsonEvent.getJSONArray(keyRoadTypes)
                val roadTypes = createRoadTypes(roadTypesArray)
                val duration = jsonEvent.getInt(keyMaxDuration)
                val tick = jsonEvent.getInt(keyTick)
                return RushHour(id, duration, tick, roadTypes, factor)
            }

            "TRAFFIC_JAM" -> {
                val duration = jsonEvent.getInt(keyMaxDuration)
                val tick = jsonEvent.getInt(keyTick)
                val factor = jsonEvent.getInt(keyFactor)
                val source = jsonEvent.getInt(keySource)
                val target = jsonEvent.getInt(keyTarget)

                return TrafficJam(id, duration, tick, factor, source, target)
            }

            "ROAD_CLOSURE" -> {
                val duration = jsonEvent.getInt(keyMaxDuration)
                val tick = jsonEvent.getInt(keyTick)
                val source = jsonEvent.getInt(keySource)
                val target = jsonEvent.getInt(keyTarget)

                return RoadClosure(id, duration, tick, source, target)
            }

            "VEHICLE_UNAVAILABLE" -> {
                val duration = jsonEvent.getInt(keyMaxDuration)
                val tick = jsonEvent.getInt(keyTick)
                val vehicleID = jsonEvent.getInt(keyVehicleID)

                return VehicleUnavailable(id, duration, tick, vehicleID)
            }
            else -> {
                val duration = jsonEvent.getInt(keyMaxDuration)
                val tick = jsonEvent.getInt(keyTick)
                val source = jsonEvent.getInt(keySource)
                val target = jsonEvent.getInt(keyTarget)
                val factor = jsonEvent.getInt(keyFactor)
                val streetClosed = jsonEvent.getBoolean(keyOneWay)
                return Construction(id, duration, tick, factor, source, target, streetClosed)
            }
        }
    }

    /** Validates the duration of events
     * Checks whether the specified duration value belongs to the range of valid values.
     */
    private fun validateEventId(id: Int): Boolean {
        if (id < 0) {
            Logger.getLogger("Event ID must be non-negative")
            return false
        } else if (eventIDSet.contains(id)) {
            Logger.getLogger("Event ID must be unique")
            return false
        } else {
            eventIDSet.add(id)
        }
        return true
    }

    /** Validates the tick of events
     * Checks whether the specified tick value belongs to the range of valid values.
     */
    private fun validateEventTick(tick: Int): Boolean {
        if (tick < 0) {
            Logger.getLogger("Event tick must be non-negative")
            return false
        }
        return true
    }

    /** Validates duration of events
     */
    private fun validateDuration(duration: Int): Boolean {
        if (duration < 1) {
            Logger.getLogger("Duration must be positive")
            return false
        }
        return true
    }

    /** Validates the factor of events
     */
    private fun validateEventFactor(factor: Int): Boolean {
        if (factor < 1) {
            Logger.getLogger("Factor must be positive")
            return false
        }
        return true
    }

    /** Validates the road types of events
     * Checks whether the specified road type belongs to PrimaryType.
     */
    private fun createRoadTypes(roadType: JSONArray): List<PrimaryType> {
        return roadType.map { enumValueOf(it.toString()) }
    }

    private fun validateRoadTypes(roadType: JSONArray): Boolean {
        val validRoadTypes = listOf("MAIN_STREET", "SIDE_STREET", "COUNTY_ROAD")
        for (type in roadType) {
            if (type !in validRoadTypes) {
                Logger.getLogger("Invalid road type")
                return false
            }
        }
        if (roadType.length() == 0) {
            Logger.getLogger("Road type must not be empty")
            return false
        }
        return true
    }

    /** Validates the source ID and target ID of events
     */
    private fun validateSourceAndTarget(sourceId: Int, targetId: Int): Boolean {
        if (sourceId < 0 || targetId < 0) {
            Logger.getLogger("Source and Target IDs must be positive")
            return false
        }
        return true
    }

    /** Validates the vehicle ID of events
     * Will be changed after Min is done with map
     */
    private fun validateVehicleId(vehicleId: Int): Boolean {
        if (vehicleId < 0) {
            Logger.getLogger("Vehicle ID must be positive")
            return false
        }
        return true
    }

    /**
     * Outputs invalidity log, terminates the program
     */
    private fun outputInvalidAndFinish() {
        Log.displayInitializationInfoInvalid(this.fileName)
        throw IllegalArgumentException("Invalid simulator configuration")
    }
}
