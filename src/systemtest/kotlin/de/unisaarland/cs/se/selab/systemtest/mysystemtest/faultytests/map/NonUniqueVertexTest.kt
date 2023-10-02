package de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.map

import de.unisaarland.cs.se.selab.systemtest.api.SystemTest

class NonUniqueVertexTest : SystemTest() {
    override val name = "NonUniqueVertexTest"
    override val map = "mapFiles/non-UniqueVertex.dot"
    override val assets = "assetsJsons/validBaseFileSmall.json"
    override val scenario = "scenarioJsons/validScenarioFileSmall.json"
    override val maxTicks = 3

    override suspend fun run() {
        assertNextLine("Initialization Info: non-UniqueVertex.dot invalid")
    }
}
