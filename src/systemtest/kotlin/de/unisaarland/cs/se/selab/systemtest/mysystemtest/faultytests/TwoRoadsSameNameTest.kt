package de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests

import de.unisaarland.cs.se.selab.systemtest.api.SystemTest

class TwoRoadsSameNameTest : SystemTest() {
    override val name = "TwoRoadsSameNameTest"

    override val map = "mapFiles/2roadsSameName.dot"
    override val assets = "assetsJsons/validBaseFileSmall.json"
    override val scenario = "scenarioJsons/validScenarioFileSmall.json"
    override suspend fun run() {
        assertNextLine("Initialization Info: 2roadsSameName.dot invalid")
    }

    override val maxTicks = 5
}
