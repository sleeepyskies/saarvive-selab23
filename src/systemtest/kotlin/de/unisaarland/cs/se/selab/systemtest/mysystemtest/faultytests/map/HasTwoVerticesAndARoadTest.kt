package de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.map

import de.unisaarland.cs.se.selab.systemtest.api.SystemTest

class HasTwoVerticesAndARoadTest : SystemTest() {
    override val name = "HasTwoVerticesAndARoadTest"

    override val map = "mapFiles/hasTwoVerticesAndARoad.dot"
    override val assets = "assetsJsons/validBaseFileSmall.json"
    override val scenario = "scenarioJsons/validScenarioFileSmall.json"
    override suspend fun run() {
        assertNextLine("Initialization Info: hasTwoVerticesAndARoad.dot invalid")
    }

    override val maxTicks = 5
}
