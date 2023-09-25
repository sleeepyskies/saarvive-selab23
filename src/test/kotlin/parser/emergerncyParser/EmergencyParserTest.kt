package parser.emergerncyParser

import de.unisaarland.cs.se.selab.dataClasses.emergencies.EmergencyType
import de.unisaarland.cs.se.selab.parser.EmergencyParser
import org.junit.jupiter.api.Test

class EmergencyParserTest {

    @Test
    fun testValid1() {
        var parser = EmergencyParser(
            schemaFile = "src/main/resources/schema/emergency.schema",
            jsonFile = "parser/emergerncyParser/valid_emergency.json"
        )

        var emergencies = parser.parse()
        var emergency = emergencies[0]
        assert(emergency.id == 1)
        assert(emergency.startTick == 5)
        assert(emergency.severity == 2)
        assert(emergency.emergencyType == EmergencyType.FIRE)
        assert(emergency.handleTime == 3)
        assert(emergency.maxDuration == 10)
        assert(emergency.villageName == "Bobski")
        assert(emergency.roadName == "Bobski Street")
    }
}
