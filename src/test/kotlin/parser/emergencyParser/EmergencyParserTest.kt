package parser.emergencyParser

import de.unisaarland.cs.se.selab.dataClasses.VehicleType
import de.unisaarland.cs.se.selab.dataClasses.emergencies.EmergencyStatus
import de.unisaarland.cs.se.selab.dataClasses.emergencies.EmergencyType
import de.unisaarland.cs.se.selab.parser.EmergencyParser
import de.unisaarland.cs.se.selab.parser.ValidationException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class EmergencyParserTest {

    @Test
    fun testValid1() {
        val parser = EmergencyParser(
            schemaFile = "src/main/resources/schema/emergency.schema",
            jsonFile = "src/test/resources/parser/emergencyParser/valid_emergency.json"
        )

        val emergencies = parser.parse()
        val emergency = emergencies[0]
        assert(emergency.id == 1)
        assert(emergency.startTick == 5)
        assert(emergency.severity == 2)
        assert(emergency.emergencyType == EmergencyType.FIRE)
        assert(emergency.handleTime == 3)
        assert(emergency.maxDuration == 10)
        assert(emergency.villageName == "Bobski")
        assert(emergency.roadName == "Bobski Street")
        assert(emergency.getEmergencyStatus() == EmergencyStatus.UNASSIGNED)
        assert(
            emergency.getRequiredVehicles() == mutableMapOf(
                VehicleType.FIRE_TRUCK_WATER to 4,
                VehicleType.FIRE_TRUCK_LADDER to 1,
                VehicleType.FIREFIGHTER_TRANSPORTER to 1,
                VehicleType.AMBULANCE to 1
            )
        )
        TODO("Add requiredCapacity check to all tests")
    }

    @Test
    fun testValid2() {
        val parser = EmergencyParser(
            schemaFile = "src/main/resources/schema/emergency.schema",
            jsonFile = "src/test/resources/parser/emergencyParser/valid_aswell_emergency.json"
        )

        val emergencies = parser.parse()
        val emergency = emergencies[0]
        assert(emergency.id == 2)
        assert(emergency.startTick == 8)
        assert(emergency.severity == 1)
        assert(emergency.emergencyType == EmergencyType.ACCIDENT)
        assert(emergency.handleTime == 2)
        assert(emergency.maxDuration == 5)
        assert(emergency.villageName == "SPONGEBOOOOOOB")
        assert(emergency.roadName == "Street Street")
        assert(emergency.getEmergencyStatus() == EmergencyStatus.UNASSIGNED)
        assert(emergency.getRequiredVehicles() == mutableMapOf(VehicleType.FIRE_TRUCK_TECHNICAL to 1))
    }

    @Test
    fun testValid3() {
        val parser = EmergencyParser(
            schemaFile = "src/main/resources/schema/emergency.schema",
            jsonFile = "src/test/resources/parser/emergencyParser/multiple_valid.json"
        )

        val emergencies = parser.parse()
        val emergency1 = emergencies[0]
        val emergency2 = emergencies[1]

        assert(emergency1.id == 1)
        assert(emergency1.startTick == 1000)
        assert(emergency1.severity == 2)
        assert(emergency1.emergencyType == EmergencyType.MEDICAL)
        assert(emergency1.handleTime == 5)
        assert(emergency1.maxDuration == 10)
        assert(emergency1.villageName == "Village")
        assert(emergency1.roadName == "Road")
        assert(emergency1.getEmergencyStatus() == EmergencyStatus.UNASSIGNED)
        assert(
            emergency1.getRequiredVehicles() == mutableMapOf(
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
        assert(emergency2.villageName == "B")
        assert(emergency2.roadName == "BB")
        assert(emergency2.getEmergencyStatus() == EmergencyStatus.UNASSIGNED)
        assert(
            emergency2.getRequiredVehicles() == mutableMapOf(
                VehicleType.FIRE_TRUCK_TECHNICAL to 4,
                VehicleType.POLICE_MOTORCYCLE to 2,
                VehicleType.POLICE_CAR to 4,
                VehicleType.AMBULANCE to 3,
                VehicleType.EMERGENCY_DOCTOR_CAR to 1
            )
        )
    }

    @Test
    fun testInvalid1() {
        val parser = EmergencyParser(
            schemaFile = "src/main/resources/schema/emergency.schema",
            jsonFile = "src/test/resources/parser/emergencyParser/out_of_range_emergency.json"
        )

        assertThrows<ValidationException> {
            parser.parse()
        }
    }

    @Test
    fun testInvalid2() {
        val parser = EmergencyParser(
            schemaFile = "src/main/resources/schema/emergency.schema",
            jsonFile = "src/test/resources/parser/emergencyParser/missing_attributes_emergency.json"
        )

        assertThrows<ValidationException> {
            parser.parse()
        }
    }

    @Test
    fun testInvalid3() {
        val parser = EmergencyParser(
            schemaFile = "what",
            jsonFile = "wha?t"
        )

        assertThrows<ValidationException> {
            parser.parse()
        }
    }

    @Test
    fun testInvalid4() {
        val parser = EmergencyParser(
            schemaFile = "src/main/resources/schema/emergency.schema",
            jsonFile = "wha?t"
        )

        assertThrows<ValidationException> {
            parser.parse()
        }
    }

    @Test
    fun testInvalid5() {
        val parser = EmergencyParser(
            schemaFile = "EXCUSE ME?",
            jsonFile = "src/test/resources/parser/emergencyParser/missing_attributes_emergency.json"
        )

        assertThrows<ValidationException> {
            parser.parse()
        }
    }
}
