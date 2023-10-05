package parsertests

import de.unisaarland.cs.se.selab.dataClasses.emergencies.EmergencyStatus
import de.unisaarland.cs.se.selab.dataClasses.emergencies.EmergencyType
import de.unisaarland.cs.se.selab.dataClasses.vehicles.VehicleType
import de.unisaarland.cs.se.selab.parser.CountyParser
import de.unisaarland.cs.se.selab.parser.SimulationParser
import org.json.JSONObject
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import java.util.Random
import kotlin.test.Test

class SimulationParserTest {
    private lateinit var emergencyParser: SimulationParser
    private val schema = "emergency.schema"
    private val graph =
        CountyParser("src/systemtest/resources/mapFiles/example_map.dot").parse()

    // Emergency Parsing Tests
    @Test
    fun testValid1() {
        emergencyParser = SimulationParser(
            schemaFile = schema,
            jsonFile = "src/test/resources/parsertests/emergencyParser/valid_emergency.json",
            graph
        )

        emergencyParser.parseEmergencyCalls()
        val emergency = emergencyParser.parsedEmergencies[0]
        assert(emergency.id == 1)
        assert(emergency.startTick == 5)
        assert(emergency.severity == 2)
        assert(emergency.emergencyType == EmergencyType.FIRE)
        assert(emergency.handleTime == 3)
        assert(emergency.maxDuration == 10)
        assert(emergency.villageName == "Saarbruecken")
        assert(emergency.roadName == "Flughafenstrasse")
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
    fun `test parse method with empty JSON`() {
        val emptyfile = "src/test/resources/parsertests/emergencyParser/empty_file.json"
        val emptyParser = SimulationParser(schema, emptyfile, graph)
        assertThrows<IllegalArgumentException> {
            emptyParser.parse()
        }
    }

    @Test
    fun `test parse method with JSON containing invalid emergencies`() {
        val invalidJsonFile = "src/test/resources/parsertests/emergencyParser/invalid_emergency.json"
        val invalidParser = SimulationParser(schema, invalidJsonFile, graph)
        assertThrows<IllegalArgumentException> { invalidParser.parse() }
    }

    @Test
    fun `test parse empty calls`() {
        val emptyJsonFile = "src/test/resources/parsertests/emergencyParser/empty_calls.json"
        val emptyParser = SimulationParser(schema, emptyJsonFile, graph)
        assertThrows<IllegalArgumentException> {
            emptyParser.parse()
        }
    }

    @Test
    fun `test parse multiple valid`() {
        val validJsonFile = "src/test/resources/parsertests/emergencyParser/multiple_valid.json"
        val validParser = SimulationParser(schema, validJsonFile, graph)
        validParser.parse()
        val emergencies = validParser.parsedEmergencies
        assert(emergencies.size == 2)
        assert(emergencies[0].id == 1)
        assert(emergencies[1].id == 2)
    }

    @Test
    fun `test multiple invalid emergencies`() {
        val invalidJsonFile = "src/test/resources/parsertests/emergencyParser/multiple_invalid.json"
        val invalidParser = SimulationParser(schema, invalidJsonFile, graph)
        assertThrows<IllegalArgumentException> { invalidParser.parse() }
    }

    @BeforeEach
    fun setUp() {
        val jsonFile = "src/test/resources/parsertests/emergencyParser/valid_emergency.json"
        emergencyParser = SimulationParser(schema, jsonFile, graph)
    }

    @Test
    fun `test emergency id`() {
        emergencyParser.parseEmergencyCalls()
        val emergency = emergencyParser.parsedEmergencies[0]
        assert(emergency.id == 1)
    }

    @Test
    fun `test negative id`() {
        val isValid = emergencyParser.validateEmergencyId(Random().nextInt(-100, -1))
        assert(!isValid)
    }

    @Test
    fun `test existent id`() {
        emergencyParser.parseEmergencyCalls()
        val isValid = emergencyParser.validateEmergencyId(1)
        assert(!isValid)
    }

    @Test
    fun `test outputInvalidAndFinish`() {
        assertThrows<IllegalArgumentException> { emergencyParser.outputInvalidAndFinish() }
    }

    @Test
    fun `test validateEmergency with valid emergency`() {
        val validJson = JSONObject(
            """{
                "id": 2,
                "emergencyType": "FIRE",
                "severity": 2,
                "tick": 1,
                "handleTime": 2,
                "maxDuration": 3,
                "village": "Saarbruecken",
                "roadName": "Flughafenstrasse"
            }"""
        )
        val isValid = emergencyParser.validateEmergency(validJson)
        assert(isValid)
    }

    @Test
    fun `test negative tick`() {
        val isValid = emergencyParser.validateEmergencyTick(Random().nextInt(-100, -1))
        val isValid2 = emergencyParser.validateEmergencyTick(0)
        val isValid3 = emergencyParser.validateEmergencyTick(Random().nextInt(1, 100000))
        assert(!isValid)
        assert(!isValid2)
        assert(isValid3)
    }

    @Test
    fun `test severity validation`() {
        val isValid = emergencyParser.validateSeverity(1)
        val isValid2 = emergencyParser.validateSeverity(2)
        val isValid3 = emergencyParser.validateSeverity(3)
        val isValid4 = emergencyParser.validateSeverity(Random().nextInt(-100, -1))
        val isValid5 = emergencyParser.validateSeverity(Random().nextInt(4, 1000))
        assert(isValid)
        assert(isValid2)
        assert(isValid3)
        assert(!isValid4)
        assert(!isValid5)
    }

    @Test
    fun `test village name validation`() {
        // valid villages:
        val isValid = emergencyParser.validateVillageName("Saarbruecken")
        val isValid4 = emergencyParser.validateVillageName("Saarland")
        val isValid5 = emergencyParser.validateVillageName("Homburg")
        val isValid6 = emergencyParser.validateVillageName("Saarlouis")
        // invalid villages:
        val isValid2 = emergencyParser.validateVillageName("Saarbrücken")
        val isValid3 = emergencyParser.validateVillageName("Saarbruecken1")
        val isValid7 = emergencyParser.validateVillageName("Saarbruecken ")
        val isValid8 = emergencyParser.validateVillageName("WHAT")
        val isValid9 = emergencyParser.validateVillageName("BroYouAreInWrongVillage")
        val isValid10 = emergencyParser.validateVillageName(" ")
        val isValid11 = emergencyParser.validateVillageName("811 what's your emergency")
        val isValid12 = emergencyParser.validateVillageName("     ")
        val isValid13 = emergencyParser.validateVillageName("Saarbru ecken")
        val isValid14 = emergencyParser.validateVillageName(" Saarbruecken")
        assert(isValid)
        assert(!isValid2)
        assert(!isValid3)
        assert(isValid4)
        assert(isValid5)
        assert(isValid6)
        assert(isValid7)
        assert(!isValid8)
        assert(!isValid9)
        assert(!isValid10)
        assert(!isValid11)
        assert(!isValid12)
        assert(!isValid13)
        assert(isValid14)
    }

    @Test
    fun `test road name validation`() {
        // valid road names:
        val isValid = emergencyParser.validateRoadName("Flughafenstrasse")
        val isValid7 = emergencyParser.validateRoadName("Beethovenstrasse")
        val isValid9 = emergencyParser.validateRoadName("Countryroad")
        val isValid8 = emergencyParser.validateRoadName("Alleestrasse")
        // invalid road names:
        val isValid2 = emergencyParser.validateRoadName("Flughafenstrasse 1")
        val isValid3 = emergencyParser.validateRoadName("Flughafenstrasse ")
        val isValid4 = emergencyParser.validateRoadName("Flughafenstrasse1")
        val isValid5 = emergencyParser.validateRoadName(" ")
        val isValid6 = emergencyParser.validateRoadName("......")
        assert(isValid)
        assert(!isValid2)
        assert(!isValid3)
        assert(!isValid4)
        assert(!isValid5)
        assert(!isValid6)
        assert(isValid7)
        assert(isValid8)
        assert(isValid9)
    }

    @Test
    fun `test emergency type validation`() {
        val isValid = emergencyParser.validateEmergencyType("FIRE")
        val isValid2 = emergencyParser.validateEmergencyType("ACCIDENT")
        val isValid3 = emergencyParser.validateEmergencyType("CRIME")
        val isValid4 = emergencyParser.validateEmergencyType("MEDICAL")
        val isValid5 = emergencyParser.validateEmergencyType("(!!)")
        val isValid6 = emergencyParser.validateEmergencyType(" ")
        val isValid7 = emergencyParser.validateEmergencyType("WE ARE TOO HOT")
        val isValid8 = emergencyParser.validateEmergencyType("fire")
        val isValid9 = emergencyParser.validateEmergencyType("accident")
        val isValid10 = emergencyParser.validateEmergencyType("crime")
        val isValid11 = emergencyParser.validateEmergencyType("medical")
        val isValid12 = emergencyParser.validateEmergencyType("FIRE MEDICAL")
        val isValid13 = emergencyParser.validateEmergencyType("FIRE ACCIDENT")
        val isValid14 = emergencyParser.validateEmergencyType("FIRE CRIME")
        val isValid15 = emergencyParser.validateEmergencyType("ACCIDENT MEDICAL")
        val isValid16 = emergencyParser.validateEmergencyType("ACCIDENT CRIME")
        val isValid17 = emergencyParser.validateEmergencyType("MEDICAL, CRIME")
        val isValid18 = emergencyParser.validateEmergencyType("FIRE" + "ACCIDENT")
        assert(isValid)
        assert(isValid2)
        assert(isValid3)
        assert(isValid4)
        assert(!isValid5)
        assert(!isValid6)
        assert(!isValid7)
        assert(!isValid8)
        assert(!isValid9)
        assert(!isValid10)
        assert(!isValid11)
        assert(!isValid12)
        assert(!isValid13)
        assert(!isValid14)
        assert(!isValid15)
        assert(!isValid16)
        assert(!isValid17)
        assert(!isValid18)
    }

    @Test
    fun `test handle time validation`() {
        val isValid = emergencyParser.validateHandleTime(1)
        val isValid2 = emergencyParser.validateHandleTime(2)
        val isValid3 = emergencyParser.validateHandleTime(3)
        val isValid4 = emergencyParser.validateHandleTime(Random().nextInt(-100, -1))
        val isValid5 = emergencyParser.validateHandleTime(0)
        val isValid6 = emergencyParser.validateHandleTime(Random().nextInt(4, 1000))
        assert(isValid)
        assert(isValid2)
        assert(isValid3)
        assert(!isValid4)
        assert(!isValid5)
        assert(isValid6)
    }

    @Test
    fun `test maxDuration validation`() {
        val isValid = emergencyParser.validateMaxDuration(222, 34)
        val isValid2 = emergencyParser.validateMaxDuration(2, 1)
        val isValid3 = emergencyParser.validateMaxDuration(3, 1)
        val isValid4 = emergencyParser.validateMaxDuration(Random().nextInt(-100, -1), 1)
        val isValid5 = emergencyParser.validateMaxDuration(0, 1)
        val isValid6 = emergencyParser.validateMaxDuration(Random().nextInt(2, 1000), 1)
        val isValid7 = emergencyParser.validateMaxDuration(0, 0)
        assert(isValid)
        assert(isValid2)
        assert(isValid3)
        assert(!isValid4)
        assert(!isValid5)
        assert(isValid6)
        assert(!isValid7)
    }
}
