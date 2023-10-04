package de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.map

import de.unisaarland.cs.se.selab.systemtest.api.SystemTest
class NoVerticesTest : SystemTest() {
    override val name = "NoVerticesTest"

    override val map = "mapFiles/noVerticesMap.dot"
    override val assets = "assetsJsons/validBaseFileSmall.json"
    override val scenario = "scenarioJsons/validScenarioFileSmall.json"
    override val maxTicks = 5
    override suspend fun run() {
        assertNextLine("Initialization Info: noVerticesMap.dot invalid")
    }
}
