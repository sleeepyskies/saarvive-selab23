package de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.map

import de.unisaarland.cs.se.selab.systemtest.api.SystemTest

class TwoRoadsSameLocationTest : SystemTest() {
    override val name = "TwoRoadsSameLocationTest"

    override val map = "mapFiles/2roadsSameLocation.dot"
    override val assets = "assetsJsons/validBaseFileSmall.json"
    override val scenario = "scenarioJsons/validScenarioFileSmall.json"
    override suspend fun run() {
        assertNextLine("Initialization Info: 2roadsSameLocation.dot invalid")
    }

    override val maxTicks = 5
}
