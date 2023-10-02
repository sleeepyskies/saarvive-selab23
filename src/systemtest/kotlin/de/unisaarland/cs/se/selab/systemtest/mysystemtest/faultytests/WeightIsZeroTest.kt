package de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests

import de.unisaarland.cs.se.selab.systemtest.api.SystemTest

class WeightIsZeroTest : SystemTest() {
    override val name = "WeightIsZeroTest"

    override val map = "mapFiles/weightIsZero.dot"
    override val assets = "assetsJsons/validBaseFileSmall.json"
    override val scenario = "scenarioJsons/validScenarioFileSmall.json"
    override suspend fun run() {
        assertNextLine("Initialization Info: weightIsZero.dot invalid")
    }

    override val maxTicks = 5
}
