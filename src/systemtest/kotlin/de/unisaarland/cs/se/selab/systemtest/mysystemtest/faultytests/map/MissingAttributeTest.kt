package de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.map

import de.unisaarland.cs.se.selab.systemtest.api.SystemTest

class MissingAttributeTest : SystemTest() {
    override val name = "MissingAttributeTest"

    override val map = "mapFiles/missingAttribute.dot"
    override val assets = "assetsJsons/validBaseFileSmall.json"
    override val scenario = "scenarioJsons/validScenarioFileSmall.json"
    override suspend fun run() {
        assertNextLine("Initialization Info: missingAttribute.dot invalid")
    }

    override val maxTicks = 5
}
