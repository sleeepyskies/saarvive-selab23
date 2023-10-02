package de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.map

import de.unisaarland.cs.se.selab.systemtest.api.SystemTest

class NegativeVertexTest : SystemTest() {
    override val name = "NegativeVertexTest"

    override val map = "mapFiles/negativeVertexID.dot"
    override val assets = "assetsJsons/validBaseFileSmall.json"
    override val scenario = "scenarioJsons/validScenarioFileSmall.json"
    override suspend fun run() {
        assertNextLine("Initialization Info: negativeVertexID.dot invalid")
    }

    override val maxTicks = 5
}
