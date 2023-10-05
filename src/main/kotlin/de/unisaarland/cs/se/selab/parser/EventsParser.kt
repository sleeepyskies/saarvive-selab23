package de.unisaarland.cs.se.selab.parser

import de.unisaarland.cs.se.selab.dataClasses.events.Construction
import de.unisaarland.cs.se.selab.dataClasses.events.Event
import de.unisaarland.cs.se.selab.dataClasses.events.RoadClosure
import de.unisaarland.cs.se.selab.dataClasses.events.RushHour
import de.unisaarland.cs.se.selab.dataClasses.events.TrafficJam
import de.unisaarland.cs.se.selab.dataClasses.events.VehicleUnavailable
import de.unisaarland.cs.se.selab.dataClasses.vehicles.Vehicle
import de.unisaarland.cs.se.selab.getSchema
import de.unisaarland.cs.se.selab.global.Log
import de.unisaarland.cs.se.selab.global.Number
import de.unisaarland.cs.se.selab.graph.PrimaryType
import io.github.oshai.kotlinlogging.KotlinLogging
import org.everit.json.schema.Schema
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File

/**
 * Parses the Events configuration file.
 * @param schemaFile the path to the JSON schema file
 * @param jsonFile the path to the JSON data file
 */
class EventsParser(private val schemaFile: String, private val jsonFile: String, private val vehicles: List<Vehicle>) {
    private val schema: Schema
    private val json: JSONObject
    var fileName = "" // for Logging
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
    private val roadClosure = "ROAD_CLOSURE"

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
     * Parses the events from the JSON file
     */
    fun parse(): List<Event> {
        try {
            parseEvents()
        } catch (_: JSONException) {
            outputInvalidAndFinish()
        }
        // Log.displayInitializationInfoValid(this.fileName)
        return parsedEvents
    }

    /** Parses the JSON data and returns a list of events
     */
    private fun parseEvents() {
        val eventsArray = json.getJSONArray("events")
        if (eventsArray.length() == 0) {
            KotlinLogging.logger("EventsParser: parseEvents()").error { "Events must not be empty" }
            outputInvalidAndFinish()
        }
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
            roadClosure, "TRAFFIC_JAM" -> {
                return validateTogether(eventType, id, jsonEvent)
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
        val hasExtraAttributes1 = !jsonEvent.has(keyOneWay) && !jsonEvent.has(keySource)
        val hasExtraAttributes2 = !jsonEvent.has(keyVehicleID) && !jsonEvent.has(keyTarget)
        return valid1 && valid2 && hasExtraAttributes1 && hasExtraAttributes2
    }

    private fun validateTogether(eventType: String, id: Int, jsonEvent: JSONObject): Boolean {
        return if (eventType == roadClosure) {
            val source = jsonEvent.getInt(keySource)
            val target = jsonEvent.getInt(keyTarget)
            validateRoadClosureEvents(id, jsonEvent, source, target)
        } else {
            val source = jsonEvent.getInt(keySource)
            val target = jsonEvent.getInt(keyTarget)
            validateTrafficJamEvent(id, jsonEvent, source, target)
        }
    }
    private fun validateRoadClosureEvents(id: Int, jsonEvent: JSONObject, source: Int, target: Int): Boolean {
        val duration = jsonEvent.getInt(keyMaxDuration)
        val tick = jsonEvent.getInt(keyTick)
        val valid1 = validateEventId(id) && validateDuration(duration)
        val valid2 = validateEventTick(tick) && validateSourceAndTarget(source, target)
        val hasExtraAttributes1 = !jsonEvent.has(keyOneWay) && !jsonEvent.has(keyFactor)
        val hasExtraAttributes2 = !jsonEvent.has(keyRoadTypes) && !jsonEvent.has(keyVehicleID)
        return valid1 && valid2 && hasExtraAttributes1 && hasExtraAttributes2
    }
    private fun validateTrafficJamEvent(id: Int, jsonEvent: JSONObject, source: Int, target: Int): Boolean {
        val duration = jsonEvent.getInt(keyMaxDuration)
        val tick = jsonEvent.getInt(keyTick)
        val factor = jsonEvent.getInt(keyFactor)
        val valid1 = validateEventId(id) && validateDuration(duration) && validateEventFactor(factor)
        val valid2 = validateEventTick(tick) && validateSourceAndTarget(source, target)
        val hasExtraAttributes1 = !jsonEvent.has(keyOneWay) && !jsonEvent.has(keyRoadTypes)
        val hasExtraAttributes2 = !jsonEvent.has(keyVehicleID)
        return valid1 && valid2 && hasExtraAttributes1 && hasExtraAttributes2
    }

    private fun validateVehicleUnavailableEvent(id: Int, jsonEvent: JSONObject, vehicleId: Int): Boolean {
        val duration = jsonEvent.getInt(keyMaxDuration)
        val tick = jsonEvent.getInt(keyTick)
        val valid1 = validateEventId(id) && validateDuration(duration)
        val valid2 = validateEventTick(tick) && validateVehicleId(vehicleId)
        val hasExtraAttributes1 = !jsonEvent.has(keyOneWay) && !jsonEvent.has(keySource) && !jsonEvent.has(keyRoadTypes)
        val hasExtraAttributes2 = !jsonEvent.has(keyTarget) && !jsonEvent.has(keyFactor)
        return valid1 && valid2 && hasExtraAttributes1 && hasExtraAttributes2
    }
    private fun validateConstructionEvent(id: Int, jsonEvent: JSONObject): Boolean {
        val duration = jsonEvent.getInt(keyMaxDuration)
        val tick = jsonEvent.getInt(keyTick)
        val source = jsonEvent.getInt(keySource)
        val target = jsonEvent.getInt(keyTarget)
        val factor = jsonEvent.getInt(keyFactor)
        val valid1 = validateEventId(id) && validateDuration(duration) && validateEventFactor(factor)
        val valid2 = validateEventTick(tick) && validateSourceAndTarget(source, target)
        val hasExtraAttributes = !jsonEvent.has(keyRoadTypes) && !jsonEvent.has(keyVehicleID)
        return valid1 && valid2 && hasExtraAttributes
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
        if (id < 0 || id > Number.TOO_BIG) {
            KotlinLogging.logger("EventsParser: validateEventId()").error { "Event ID must be positive" }
            return false
        } else if (eventIDSet.contains(id)) {
            KotlinLogging.logger("EventsParser: validateEventId()").error { "Event ID must be unique" }
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
        if (tick < 0 || tick > Number.TOO_BIG) {
            KotlinLogging.logger("EventsParser: validateEventTick()").error { "Event tick must be positive" }
            return false
        }
        return true
    }

    /** Validates duration of events
     */
    private fun validateDuration(duration: Int): Boolean {
        if (duration < 1 || duration > Number.TOO_BIG) {
            KotlinLogging.logger("EventsParser: validateDuration()").error { "Duration must be positive" }
            return false
        }
        return true
    }

    /** Validates the factor of events
     */
    private fun validateEventFactor(factor: Int): Boolean {
        if (factor < 1 || factor > Number.TOO_BIG) {
            KotlinLogging.logger("EventsParser: validateEventFactor()").error { "Factor must be positive" }
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
                KotlinLogging.logger("EventsParser: validateRoadTypes()").error { "Invalid road type" }
                return false
            }
        }
        if (roadType.length() == 0) {
            KotlinLogging.logger("EventsParser: validateRoadTypes()").error { "Road types must not be empty" }
            return false
        }
        return true
    }

    /** Validates the source ID and target ID of events
     */
    private fun validateSourceAndTarget(sourceId: Int, targetId: Int): Boolean {
        val condition1 = sourceId < 0 || targetId < 0
        val condition2 = sourceId > Number.TOO_BIG || targetId > Number.TOO_BIG
        if (condition1 || condition2) {
            KotlinLogging.logger("EventsParser: vertex validation").error { "Source and target ID must be positive" }
            return false
        }
        return true
    }

    /** Validates the vehicle ID of events
     * Will be changed after Min is done with map
     */
    private fun validateVehicleId(vehicleId: Int): Boolean {
        val listOfVehicles = mutableListOf<Int>()
        for (v in vehicles) {
            listOfVehicles.add(v.id)
        }
        if (vehicleId < 0 || vehicleId > Number.TOO_BIG) {
            KotlinLogging.logger("EventsParser: validateVehicleId()").error { "Vehicle ID must be positive" }
            return false
        } else if (vehicleId !in listOfVehicles) {
            KotlinLogging.logger("EventsParser: validateVehicleId()").error { "Invalid vehicle ID" }
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
