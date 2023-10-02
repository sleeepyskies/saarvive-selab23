package de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.map

import de.unisaarland.cs.se.selab.systemtest.api.SystemTest

class NonExistentVertexTest : SystemTest() {
    override val name = "NonExistentVertexTest"

    override val map = "mapFiles/connectsNon-ExistentVertex.dot"
    override val assets = "assetsJsons/validBaseFileSmall.json"
    override val scenario = "scenarioJsons/validScenarioFileSmall.json"
    override suspend fun run() {
        assertNextLine("Initialization Info: connectsNon-ExistentVertex.dot invalid")
    }

    override val maxTicks = 5
}
