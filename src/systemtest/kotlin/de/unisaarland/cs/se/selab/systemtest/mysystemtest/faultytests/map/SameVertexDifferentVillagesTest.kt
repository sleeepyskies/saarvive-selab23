package de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.map

import de.unisaarland.cs.se.selab.systemtest.api.SystemTest

class SameVertexDifferentVillagesTest : SystemTest() {
    override val name = "SameVertexDifferentVillagesTest"

    override val map = "mapFiles/sameVertexDifferentVillages.dot"
    override val assets = "assetsJsons/validBaseFileSmall.json"
    override val scenario = "scenarioJsons/validScenarioFileSmall.json"
    override suspend fun run() {
        assertNextLine("Initialization Info: sameVertexDifferentVillages.dot invalid")
    }

    override val maxTicks = 5
}
