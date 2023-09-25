package parser.emergencyParser

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
            jsonFile = "parser/emergencyParser/valid_emergency.json"
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
    }

    fun testValid2() {
        val parser = EmergencyParser(
            schemaFile = "src/main/resources/schema/emergency.schema",
            jsonFile = "parser/emergencyParser/valid_aswell_emergency.json"
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
    }

    fun testValid3() {
        val parser = EmergencyParser(
            schemaFile = "src/main/resources/schema/emergency.schema",
            jsonFile = "parser/emergencyParser/multiple_valid.json"
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

        assert(emergency2.id == 2)
        assert(emergency2.startTick == 2)
        assert(emergency2.severity == 3)
        assert(emergency2.emergencyType == EmergencyType.ACCIDENT)
        assert(emergency2.handleTime == 4)
        assert(emergency2.maxDuration == 8)
        assert(emergency2.villageName == "B")
        assert(emergency2.roadName == "BB")
    }

    fun testInvalid1() {
        val parser = EmergencyParser(
            schemaFile = "src/main/resources/schema/emergency.schema",
            jsonFile = "parser/emergencyParser/out_of_range_emergency.json"
        )

        val emergencies = parser.parse()
        var emergency = emergencies[0]
        assertThrows<ValidationException> {
            parser.parse()
        }
    }

    fun testInvalid2() {
        val parser = EmergencyParser(
            schemaFile = "src/main/resources/schema/emergency.schema",
            jsonFile = "parser/emergencyParser/missing_attributes_emergency.json"
        )

        assertThrows<ValidationException> {
            parser.parse()
        }
    }

    fun testInvalid3() {
        val parser = EmergencyParser(
            schemaFile = "what",
            jsonFile = "wha?t"
        )

        assertThrows<ValidationException> {
            parser.parse()
        }
    }

    fun testInvalid4() {
        val parser = EmergencyParser(
            schemaFile = "src/main/resources/schema/emergency.schema",
            jsonFile = "wha?t"
        )

        assertThrows<ValidationException> {
            parser.parse()
        }
    }

    fun testInvalid5() {
        val parser = EmergencyParser(
            schemaFile = "EXCUSE ME????????????????????",
            jsonFile = "parser/emergencyParser/missing_attributes_emergency.json"
        )

        assertThrows<ValidationException> {
            parser.parse()
        }
    }
}
