package parsertests

import de.unisaarland.cs.se.selab.dataClasses.events.RushHour
import de.unisaarland.cs.se.selab.parser.EventsParser
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.FileNotFoundException

class EventsParserTest {

    @Test
    fun testValidEventsJSON() {
        val parser = EventsParser(
            schemaFile = "event.schema",
            jsonFile = "src/systemtest/resources/scenarioJsons/example_scenario.json",
            vehicles = emptyList() // Provide a list of vehicles here
        )

        val events = parser.parse()

        // Validate the first event
        val event1 = events[0]
        assert(event1.eventID == 0)
        assert(event1 is RushHour)
        assert(event1.startTick == 3)
        assert(event1.duration == 2)
        // if (event1 is RushHour) {assert(event1.roadType == ["SIDE_STREET"])}
        if (event1 is RushHour) { assert(event1.factor == 2) }
    }

    @Test
    fun testInvalidPathToJSON() {
        assertThrows<FileNotFoundException> {
            EventsParser(
                schemaFile = "event.schema",
                jsonFile = "invalid/path/to/json",
                vehicles = emptyList()
            )
        }
    }

    @Test
    fun testInvalidEventsJSON() {
        assertThrows<IllegalArgumentException> {
            EventsParser(
                schemaFile = "event.schema",
                jsonFile = "src/test/resources/parsertests/eventsParser/invalid_events.json",
                vehicles = emptyList()
            ).parse()
        }
    }

    @Test
    fun testInvalidEventType() {
        assertThrows<IllegalArgumentException> {
            EventsParser(
                schemaFile = "event.schema",
                jsonFile = "src/test/resources/parsertests/eventsParser/invalid_event_type.json",
                vehicles = emptyList()
            ).parse()
        }
    }

    @Test
    fun testNegativeEventID() {
        assertThrows<IllegalArgumentException> {
            EventsParser(
                schemaFile = "event.schema",
                jsonFile = "src/test/resources/parsertests/eventsParser/negative_event_id.json",
                vehicles = emptyList()
            ).parse()
        }
    }

    @Test
    fun testDuplicateEventID() {
        assertThrows<IllegalArgumentException> {
            EventsParser(
                schemaFile = "event.schema",
                jsonFile = "src/test/resources/parsertests/eventsParser/duplicate_event_id.json",
                vehicles = emptyList()
            ).parse()
        }
    }

    @Test
    fun testMissingRequiredFields() { // json exception thrown
        assertThrows<IllegalArgumentException> {
            EventsParser(
                schemaFile = "event.schema",
                jsonFile = "src/test/resources/parsertests/eventsParser/missing_required_fields.json",
                vehicles = emptyList()
            ).parse()
        }
    }

    @Test
    fun testRushHourWithNegativeFactor() {
        assertThrows<IllegalArgumentException> {
            EventsParser(
                schemaFile = "event.schema",
                jsonFile = "src/test/resources/parsertests/eventsParser/rush_hour_negative_factor.json",
                vehicles = emptyList()
            ).parse()
        }
    }

    // CONSTRUCTION_SITE Event Tests
    @Test
    fun testConstructionWithNegativeSourceAndTarget() {
        assertThrows<IllegalArgumentException> {
            EventsParser(
                schemaFile = "event.schema",
                jsonFile = "src/test/resources/parsertests/eventsParser/construction_negative_source_target.json",
                vehicles = emptyList()
            ).parse()
        }
    }

    @Test
    fun testConstructionWithMissingSourceOrTarget() { // json exception thrown
        assertThrows<IllegalArgumentException> {
            EventsParser(
                schemaFile = "event.schema",
                jsonFile = "src/test/resources/parsertests/eventsParser/construction_missing_source_or_target.json",
                vehicles = emptyList()
            ).parse()
        }
    }

    // VEHICLE_UNAVAILABLE Event Tests
    @Test
    fun testVehicleUnavailableWithNegativeVehicleID() {
        assertThrows<IllegalArgumentException> {
            EventsParser(
                schemaFile = "event.schema",
                jsonFile = "src/test/resources/parsertests/eventsParser/vehicle_unavailable_negative_id.json",
                vehicles = emptyList()
            ).parse()
        }
    }

    @Test
    fun testVehicleUnavailableWithNonExistentVehicleID() {
        assertThrows<IllegalArgumentException> {
            EventsParser(
                schemaFile = "event.schema",
                jsonFile = "src/test/resources/parsertests/eventsParser/vehicle_unavailable_non_existent_id.json",
                vehicles = emptyList()
            ).parse()
        }
    }

    // Duration Tests
    @Test
    fun testEventWithZeroOrNegativeDuration() {
        assertThrows<IllegalArgumentException> {
            EventsParser(
                schemaFile = "event.schema",
                jsonFile = "src/test/resources/parsertests/eventsParser/event_negative_duration.json",
                vehicles = emptyList()
            ).parse()
        }
    }

    // Tick Tests
    @Test
    fun testEventWithNegativeTick() {
        assertThrows<IllegalArgumentException> {
            EventsParser(
                schemaFile = "event.schema",
                jsonFile = "src/test/resources/parsertests/eventsParser/event_negative_tick.json",
                vehicles = emptyList()
            ).parse()
        }
    }

    // Factor Tests
    @Test
    fun testEventWithZeroOrNegativeFactor() {
        assertThrows<IllegalArgumentException> {
            EventsParser(
                schemaFile = "event.schema",
                jsonFile = "src/test/resources/parsertests/eventsParser/event_negative_factor.json",
                vehicles = emptyList()
            ).parse()
        }
    }

    // Road Types Tests
    @Test
    fun testEventWithInvalidRoadTypes() {
        assertThrows<IllegalArgumentException> {
            EventsParser(
                schemaFile = "event.schema",
                jsonFile = "src/test/resources/parsertests/eventsParser/event_invalid_road_types.json",
                vehicles = emptyList()
            ).parse()
        }
    }

    // Source and Target Tests
    @Test
    fun testEventWithNegativeSourceOrTarget() {
        assertThrows<IllegalArgumentException> {
            EventsParser(
                schemaFile = "event.schema",
                jsonFile = "src/test/resources/parsertests/eventsParser/event_negative_source_or_target.json",
                vehicles = emptyList()
            ).parse()
        }
    }

    // Vehicle ID Tests
//    @Test
//    fun testEventWithValidVehicleID() {
//        val parser = EventsParser(
//            schemaFile = "event.schema",
//            jsonFile = "src/test/resources/parsertests/eventsParser/event_valid_vehicle_id.json",
//            vehicles = listOf() // Provide a list of vehicles here
//        )
//        val events = parser.parse()
//        if (events is VehicleUnavailable) { assert(events.vehicleID == 1) }
//    }

    @Test
    fun testEventWithNegativeVehicleID() {
        assertThrows<IllegalArgumentException> {
            EventsParser(
                schemaFile = "event.schema",
                jsonFile = "src/test/resources/parsertests/eventsParser/event_negative_vehicle_id.json",
                vehicles = emptyList()
            ).parse()
        }
    }
}
