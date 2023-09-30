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

    // a set of all emergency IDs to make sure they are unique
    private val emergencyIDSet = mutableSetOf<Int>()
    private val eventIDSet = mutableSetOf<Int>()

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
    fun parseEmergencyCalls(): List<Emergency> {
        val emergencyCallsArray = json.getJSONArray("emergencyCalls")
        val parsedEmergencies = mutableListOf<Emergency>()
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
        return parsedEmergencies
    }

    /** Parses the JSON data and returns a list of events
     */
    fun parseEvents(): List<Event> {
        val eventsArray = json.getJSONArray("events")
        val parsedEvents = mutableListOf<Event>()
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
        return parsedEvents
    }

    /** Validates the ID of emergencies, check if it is unique.
     */
    private fun validateEmergencyId(id: Int): Int {
        require(id >= 0) { "ID must be positive" }
        require(!emergencyIDSet.contains(id)) { "ID must be unique" }
        return id
    }

    private fun validateEventId(id: Int): Int {
        require(id >= 0) { "ID must be positive" }
        require(!eventIDSet.contains(id)) { "ID must be unique" }
        return id
    }

    /** Validates the severity of emergencies
     * Checks whether the specified severity value belongs to the range of valid values.
     */
    private fun validateSeverity(severity: Int): Int {
        require(severity in 1..3) { "Severity must be between 1 and 3" }
        return severity
    }

    /** Validates the tick of emergencies
     */
    private fun validateEmergencyTick(tick: Int): Int {
        require(tick >= 1) { "Tick must be greater than or equal to 1" }
        return tick
    }

    /** Validates the tick of events
     * Checks whether the specified tick value belongs to the range of valid values.
     */
    private fun validateEventTick(tick: Int): Int {
        if (tick <= 0) {
            System.err.println("Tick must be greater than 0")
        }
        return tick
    }

    /** Validates the emergency type of emergencies
     * Checks whether the specified emergency type belongs to EmergencyType.
     */
    private fun validateEmergencyType(emergencyType: String): EmergencyType {
        require(EmergencyType.values().any { it.name == emergencyType }) {
            "EmergencyType must be one of the following: ${EmergencyType.values().joinToString(", ") { it.name }}"
        }
        return EmergencyType.valueOf(emergencyType)
    }

    /** Validates the handle time of emergencies
     */
    private fun validateHandleTime(handleTime: Int): Int {
        require(handleTime >= 1) { "Minimum handle time is 1" }
        return handleTime
    }

    /** Validates the duration of events
     */
    private fun validateDuration(duration: Int): Int {
        require(duration >= 1) { "Minimum duration of an event should be at least 1" }
        return duration
    }

    /** Validates the maximum duration of emergencies, checks whether the specified maximum duration
     * is greater than the handle time.
     */
    private fun validateMaxDuration(maxDuration: Int, handleTime: Int): Int {
        require(maxDuration > handleTime) { "Maximum duration must be greater than handle time" }
        return maxDuration
    }

    /** Validates the village name of emergencies --> will be changes after Ira is done with Parsing
     */
    private fun validateVillageName(villageName: String): String {
        require(villageName.isNotBlank()) { "Village name must not be blank" }
        return villageName
    }

    /** Validates the factor of events
     */
    private fun validateEventFactor(factor: Int): Int {
        require(factor >= 1) { "Factor must be at least 1" }
        return factor
    }

    /** Validates the road types of events
     * Checks whether the specified road type belongs to PrimaryType.
     */
    private fun validateRoadTypes(roadType: JSONArray): List<PrimaryType> {
        val validRoadTypes = listOf("MAIN_STREET", "SIDE_STREET", "COUNTY_ROAD")
        for (type in roadType) {
            require(type in validRoadTypes) { "RoadType must be one of the following: $validRoadTypes" }
        }
        return roadType.map { enumValueOf(it.toString()) }
    }

    /** Validates the source ID of events
     * Will be changed after Ira is done with map
     */
    private fun validateSourceId(sourceId: Int): Int {
        require(sourceId >= 0) { "Source ID must be positive" }
        return sourceId
    }

    /** Validates the target ID of events
     * Will be changed after Ira is done with map
     */
    private fun validateTargetId(targetId: Int): Int {
        require(targetId >= 0) { "Target ID must be positive" }
        return targetId
    }

    /** Validates the vehicle ID of events
     * Will be changed after Min is done with map
     */
    private fun validateVehicleId(vehicleId: Int): Int {
        require(vehicleId >= 0) { "Vehicle ID must be positive" }
        return vehicleId
    }
}
