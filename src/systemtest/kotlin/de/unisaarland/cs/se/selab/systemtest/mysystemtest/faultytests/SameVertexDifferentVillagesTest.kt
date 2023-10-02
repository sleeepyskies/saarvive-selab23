package de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests

import de.unisaarland.cs.se.selab.systemtest.api.SystemTest

class SameVertexDifferentVillagesTest : SystemTest() {
    override val name = "SameVertexDifferentVillagesTest"

    override val map = "mapFiles/sameVertexDifferentVillagesTest.dot"
    override val assets = "assetsJsons/validBaseFileSmall.json"
    override val scenario = "scenarioJsons/validScenarioFileSmall.json"
    override suspend fun run() {
        assertNextLine("Initialization Info: sameVertexDifferentVillagesTest.dot invalid")
    }

    override val maxTicks = 5
}
