package parsertests

import de.unisaarland.cs.se.selab.dataClasses.emergencies.EmergencyStatus
import de.unisaarland.cs.se.selab.dataClasses.emergencies.EmergencyType
import de.unisaarland.cs.se.selab.dataClasses.vehicles.VehicleType
import de.unisaarland.cs.se.selab.parser.CountyParser
import de.unisaarland.cs.se.selab.parser.SimulationParser
import org.json.JSONException
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.PrintStream

class SimulationParserTest {
    private lateinit var parser: SimulationParser
    private val graph =
        CountyParser("src/systemtest/resources/mapFiles/example_map.dot").parse()

    // Emergency Parsing Tests
    @Test
    fun testValid1() {
        parser = SimulationParser(
            schemaFile = "emergency.schema",
            jsonFile = "src/test/resources/parsertests/emergencyParser/valid_emergency.json",
            graph
        )

        parser.parseEmergencyCalls()
        val emergency = parser.parsedEmergencies[0]
        assert(emergency.id == 1)
        assert(emergency.startTick == 5)
        assert(emergency.severity == 2)
        assert(emergency.emergencyType == EmergencyType.FIRE)
        assert(emergency.handleTime == 3)
        assert(emergency.maxDuration == 10)
        assert(emergency.villageName == "Saarbruecken")
        assert(emergency.roadName == "Bobski Street")
        assert(emergency.getEmergencyStatus() == EmergencyStatus.UNASSIGNED)
        assert(
            emergency.requiredVehicles == mutableMapOf(
                VehicleType.FIRE_TRUCK_WATER to 4,
                VehicleType.FIRE_TRUCK_LADDER to 1,
                VehicleType.FIREFIGHTER_TRANSPORTER to 1,
                VehicleType.AMBULANCE to 1
            )
        )
    }

    @Test
    fun testValid2() {
        parser = SimulationParser(
            schemaFile = "emergency.schema",
            jsonFile = "src/test/resources/parsertests/emergencyParser/valid_aswell_emergency.json",
            graph
        )

        parser.parseEmergencyCalls()
        val emergency = parser.parsedEmergencies[0]
        assert(emergency.id == 2)
        assert(emergency.startTick == 8)
        assert(emergency.severity == 1)
        assert(emergency.emergencyType == EmergencyType.ACCIDENT)
        assert(emergency.handleTime == 2)
        assert(emergency.maxDuration == 5)
        assert(emergency.villageName == "Saarbruecken")
        assert(emergency.roadName == "Street Street")
        assert(emergency.getEmergencyStatus() == EmergencyStatus.UNASSIGNED)
        assert(emergency.requiredVehicles == mutableMapOf(VehicleType.FIRE_TRUCK_TECHNICAL to 1))
    }

    @Test
    fun testValid3() {
        parser = SimulationParser(
            schemaFile = "emergency.schema",
            jsonFile = "src/test/resources/parsertests/emergencyParser/multiple_valid.json",
            graph
        )

        parser.parseEmergencyCalls()
        val emergency1 = parser.parsedEmergencies[0]
        val emergency2 = parser.parsedEmergencies[1]

        assert(emergency1.id == 1)
        assert(emergency1.startTick == 1000)
        assert(emergency1.severity == 2)
        assert(emergency1.emergencyType == EmergencyType.MEDICAL)
        assert(emergency1.handleTime == 5)
        assert(emergency1.maxDuration == 10)
        assert(emergency1.villageName == "Saarbruecken")
        assert(emergency1.roadName == "Road")
        assert(emergency1.getEmergencyStatus() == EmergencyStatus.UNASSIGNED)
        assert(
            emergency1.requiredVehicles == mutableMapOf(
                VehicleType.AMBULANCE to 2,
                VehicleType.EMERGENCY_DOCTOR_CAR to 1
            )
        )

        assert(emergency2.id == 2)
        assert(emergency2.startTick == 2)
        assert(emergency2.severity == 3)
        assert(emergency2.emergencyType == EmergencyType.ACCIDENT)
        assert(emergency2.handleTime == 4)
        assert(emergency2.maxDuration == 8)
        assert(emergency2.villageName == "Saarbruecken")
        assert(emergency2.roadName == "BB")
        assert(emergency2.getEmergencyStatus() == EmergencyStatus.UNASSIGNED)
        assert(
            emergency2.requiredVehicles == mutableMapOf(
                VehicleType.FIRE_TRUCK_TECHNICAL to 4,
                VehicleType.POLICE_MOTORCYCLE to 2,
                VehicleType.POLICE_CAR to 4,
                VehicleType.AMBULANCE to 3,
                VehicleType.EMERGENCY_DOCTOR_CAR to 1
            )
        )
    }

    @Test // checking output instead of checking exception
    fun testInvalid1() {
        parser = SimulationParser(
            schemaFile = "emergency.schema",
            jsonFile = "src/test/resources/parsertests/emergencyParser/out_of_range_emergency.json",
            graph
        )

        val errContent = ByteArrayOutputStream()
        System.setErr(PrintStream(errContent))

        parser.parseEmergencyCalls()

        val expectedOutput = "Emergency ID must be positive"
        assertTrue(errContent.toString().contains(expectedOutput))
    }

    @Test
    fun testInvalid2() {
        parser = SimulationParser(
            schemaFile = "emergency.schema",
            jsonFile = "src/test/resources/parsertests/emergencyParser/missing_attributes_emergency.json",
            graph
        )

        assertThrows<JSONException> {
            parser.parseEmergencyCalls()
        }
    }

    // null pointer error
    @Test
    fun testInvalidSchemaFilePath() {
        assertThrows<FileNotFoundException> {
            SimulationParser(
                schemaFile = "emergency.schema",
                jsonFile = "wha?t",
                graph
            ).parseEmergencyCalls()
        }
    }

    @Test
    fun testInvalid4() {
        assertThrows<FileNotFoundException> {
            SimulationParser(
                schemaFile = "emergency.schema",
                jsonFile = "wha?t",
                graph
            ).parseEmergencyCalls()
        }
    }

    @Test
    fun testValidInitialization() {
        val schemaFile = "emergency.schema"
        val jsonFile = "src/test/resources/parsertests/emergencyParser/valid_emergency.json"
        parser = SimulationParser(schemaFile, jsonFile, graph)
        // Ensure the parser is initialized without errors
        assertDoesNotThrow { parser.parse() }
    }

    @Test
    fun testParse() {
        // Ensure that parsing returns non-empty lists of emergencies and events
        val schemaFile = "emergency.schema"
        val jsonFile = "src/test/resources/parsertests/emergencyParser/multiple_valid.json"
        parser = SimulationParser(schemaFile, jsonFile, graph)
        parser.parse()
        assertTrue(parser.parsedEmergencies.isNotEmpty())
        assertTrue(parser.parsedEvents.isNotEmpty())
    }
}
