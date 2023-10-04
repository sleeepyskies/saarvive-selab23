package de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.map

import de.unisaarland.cs.se.selab.systemtest.api.SystemTest

class CountySameRoadNameTest : SystemTest() {
    override val name = "CountySameRoadName"

    override val map = "mapFiles/countySameRoadName.dot"
    override val assets = "assetsJsons/validBaseFileSmall.json"
    override val scenario = "scenarioJsons/validScenarioFileSmall.json"
    override suspend fun run() {
        assertNextLine("Initialization Info: countySameRoadName.dot invalid")
    }

    override val maxTicks = 5
}
