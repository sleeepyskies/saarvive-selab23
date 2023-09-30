package de.unisaarland.cs.se.selab.parser
import de.unisaarland.cs.se.selab.dataClasses.emergencies.Emergency
import de.unisaarland.cs.se.selab.dataClasses.emergencies.EmergencyType
import de.unisaarland.cs.se.selab.dataClasses.events.Event
import de.unisaarland.cs.se.selab.dataClasses.events.RoadClosure
import de.unisaarland.cs.se.selab.dataClasses.events.RushHour
import de.unisaarland.cs.se.selab.dataClasses.events.TrafficJam
import de.unisaarland.cs.se.selab.dataClasses.events.VehicleUnavailable
import de.unisaarland.cs.se.selab.getSchema
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
    public val parsedEmergencies = mutableListOf<Emergency>()
    public val parsedEvents = mutableListOf<Event>()

    // a set of all emergency IDs to make sure they are unique
    private val emergencyIDSet = mutableSetOf<Int>()
    private val eventIDSet = mutableSetOf<Int>()
    public var validEmergency = true
    public var validEvent = true

    init {
        // Load and validate the JSON schema
//        val schemaJson = JSONObject(File(schemaFile).readText())
        schema = getSchema(this.javaClass, schemaFile) ?: throw IllegalArgumentException("Schema not found")

        // Load and parse the JSON data
        val simulationJsonData = File(jsonFile).readText()
        json = JSONObject(simulationJsonData)

        schema.validate(json)
    }

    /** Parses the JSON data and returns a list of emergencies, uses private method
     * to parse single emergencies.
     */
    fun parseEmergencyCalls() {
        val emergencyCallsArray = json.getJSONArray("emergencyCalls")
        for (i in 0 until emergencyCallsArray.length()) {
            if (!validEmergency) {
                return
            }
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
    fun parseEvents() {
        val eventsArray = json.getJSONArray("events")
        for (i in 0 until eventsArray.length()) {
            if (!validEvent) {
                return
            }
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
            validEmergency = false
        } else if (emergencyIDSet.contains(id)) {
            validEmergency = false
        } else { emergencyIDSet.add(id) }
        return id
    }

    private fun validateEventId(id: Int): Int {
        if (id < 0) {
            validEvent = false
        } else if (eventIDSet.contains(id)) {
            validEvent = false
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
            validEmergency = false
        }
        return severity
    }

    /** Validates the tick of emergencies
     */
    private fun validateEmergencyTick(tick: Int): Int {
        if (tick <= 0) {
            validEmergency = false
        }
        return tick
    }

    /** Validates the tick of events
     * Checks whether the specified tick value belongs to the range of valid values.
     */
    private fun validateEventTick(tick: Int): Int {
        if (tick < 0) {
            validEvent = false
        }
        return tick
    }

    /** Validates the emergency type of emergencies
     * Checks whether the specified emergency type belongs to EmergencyType.
     */
    private fun validateEmergencyType(emergencyType: String): EmergencyType {
        if (emergencyType != "FIRE" && emergencyType != "CRIME" && emergencyType != "MEDICAL" && emergencyType != "ACCIDENT") {
            validEmergency = false
        }
        return EmergencyType.valueOf(emergencyType)
    }

    /** Validates the handle time of emergencies
     */
    private fun validateHandleTime(handleTime: Int): Int {
        if (handleTime < 1) {
            validEmergency = false
        }
        return handleTime
    }

    /** Validates the duration of events
     */
    private fun validateDuration(duration: Int): Int {
        if (duration < 1) {
            validEvent = false
        }
        return duration
    }

    /** Validates the maximum duration of emergencies, checks whether the specified maximum duration
     * is greater than the handle time.
     */
    private fun validateMaxDuration(maxDuration: Int, handleTime: Int): Int {
        if (maxDuration < handleTime) {
            validEmergency = false
        }
        return maxDuration
    }

    /** Validates the village name of emergencies --> will be changes after Ira is done with Parsing
     */
    private fun validateVillageName(villageName: String): String {
        if (villageName.isBlank()) {
            validEmergency = false
        }
        return villageName
    }

    /** Validates the factor of events
     */
    private fun validateEventFactor(factor: Int): Int {
        if (factor < 1) {
            validEvent = false
        }
        return factor
    }

    /** Validates the road types of events
     * Checks whether the specified road type belongs to PrimaryType.
     */
    private fun validateRoadTypes(roadType: JSONArray): List<PrimaryType> {
        val validRoadTypes = listOf("MAIN_STREET", "SIDE_STREET", "COUNTY_ROAD")
        for (type in roadType) {
            if (type !in validRoadTypes) validEvent = false
        }
        return roadType.map { enumValueOf(it.toString()) }
    }

    /** Validates the source ID of events
     * Will be changed after Ira is done with map
     */
    private fun validateSourceId(sourceId: Int): Int {
        if (sourceId < 0) {
            validEvent = false
        }
        return sourceId
    }

    /** Validates the target ID of events
     * Will be changed after Ira is done with map
     */
    private fun validateTargetId(targetId: Int): Int {
        if (targetId < 0) {
            validEvent = false
        }
        return targetId
    }

    /** Validates the vehicle ID of events
     * Will be changed after Min is done with map
     */
    private fun validateVehicleId(vehicleId: Int): Int {
        if (vehicleId < 0) {
            validEvent = false
        }
        return vehicleId
    }
}
