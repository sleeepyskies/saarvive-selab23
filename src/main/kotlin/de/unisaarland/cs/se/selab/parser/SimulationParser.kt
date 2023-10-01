package de.unisaarland.cs.se.selab.parser
import de.unisaarland.cs.se.selab.dataClasses.emergencies.Emergency
import de.unisaarland.cs.se.selab.dataClasses.emergencies.EmergencyType
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
 * Parses the emergency configuration file.
 * @param schemaFile the path to the JSON schema file
 * @param jsonFile the path to the JSON data file
 */
class SimulationParser(private val schemaFile: String, private val jsonFile: String) {
    private val schema: Schema
    private val json: JSONObject
    private var fileName = "" // for Logging
    val parsedEmergencies = mutableListOf<Emergency>()
    val parsedEvents = mutableListOf<Event>()

    // a set of all emergency IDs to make sure they are unique
    private val emergencyIDSet = mutableSetOf<Int>()
    private val eventIDSet = mutableSetOf<Int>()

    init {
        // Load and validate the JSON schema
//        val schemaJson = JSONObject(File(schemaFile).readText())
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
     * Parses the JSON data and returns a pair of emergencies and events
     */
    fun parse(): Pair<List<Emergency>, List<Event>> {
        try {
            parseEmergencyCalls()
            parseEvents()
        } catch (_: IllegalArgumentException) {
            outputInvalidAndFinish()
        }
        Log.displayInitializationInfoValid(this.fileName)
        return Pair(parsedEmergencies, parsedEvents)
    }

    /** Parses the JSON data and returns a list of emergencies, uses private method
     * to parse single emergencies.
     */
    private fun parseEmergencyCalls() {
        val emergencyCallsArray = json.getJSONArray("emergencyCalls")
        for (i in 0 until emergencyCallsArray.length()) {
            val jsonEmergency = emergencyCallsArray.getJSONObject(i)
//            schema.validate(jsonEmergency) -> detekt throws error
            // Validation of fields
            val id = validateEmergencyId(jsonEmergency.getInt("id"))
            val emergencyType = validateEmergencyType(jsonEmergency.getString("emergencyType"))
            val severity = validateSeverity(jsonEmergency.getInt("severity"))
            val startTick = validateEmergencyTick(jsonEmergency.getInt("tick"))
            val handleTime = validateHandleTime(jsonEmergency.getInt("handleTime"))
            val maxDuration = validateMaxDuration(jsonEmergency.getInt("maxDuration"), handleTime)
            val villageName = validateVillageName(jsonEmergency.getString("village"))
            // will be changed after Ira is done with parsing
            val roadName = jsonEmergency.getString("roadName") // will be changed after Ira is done with parsing

            // create a single emergency
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
            // add emergency to list of emergencies
            parsedEmergencies.add(emergency)
        }
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

    /** Validates the ID of emergencies, check if it is unique.
     */
    private fun validateEmergencyId(id: Int): Int {
        if (id < 0) {
            System.err.println("Emergency ID must be positive")
        } else if (emergencyIDSet.contains(id)) {
            System.err.println("Emergency ID must be unique")
        } else { emergencyIDSet.add(id) }
        return id
    }

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

    /** Validates the severity of emergencies
     * Checks whether the specified severity value belongs to the range of valid values.
     */
    private fun validateSeverity(severity: Int): Int {
        if (severity !in 1..3) {
            System.err.println("Severity must be between 1 and 3")
        }
        return severity
    }

    /** Validates the tick of emergencies
     */
    private fun validateEmergencyTick(tick: Int): Int {
        if (tick <= 0) {
            System.err.println("Emergency tick must be positive")
        }
        return tick
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

    /** Validates the emergency type of emergencies
     * Checks whether the specified emergency type belongs to EmergencyType.
     */
    private fun validateEmergencyType(emergencyType: String): EmergencyType {
        val validTypes = listOf("FIRE", "ACCIDENT", "CRIME", "MEDICAL")
        if (emergencyType !in validTypes) {
            System.err.println("Invalid emergency type")
        }
        return EmergencyType.valueOf(emergencyType)
    }

    /** Validates the handle time of emergencies
     */
    private fun validateHandleTime(handleTime: Int): Int {
        if (handleTime < 1) {
            System.err.println("Handle time must be positive")
        }
        return handleTime
    }

    /** Validates the duration of events
     */
    private fun validateDuration(duration: Int): Int {
        if (duration < 1) {
            System.err.println("Duration must be positive")
        }
        return duration
    }

    /** Validates the maximum duration of emergencies, checks whether the specified maximum duration
     * is greater than the handle time.
     */
    private fun validateMaxDuration(maxDuration: Int, handleTime: Int): Int {
        if (maxDuration < handleTime) {
            System.err.println("Max duration must be greater than handle time")
        }
        return maxDuration
    }

    /** Validates the village name of emergencies --> will be changes after Ira is done with Parsing
     */
    private fun validateVillageName(villageName: String): String {
        if (villageName.isBlank()) {
            System.err.println("Village name must not be blank")
        }
        return villageName
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
