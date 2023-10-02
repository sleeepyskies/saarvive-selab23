package de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests

import de.unisaarland.cs.se.selab.systemtest.api.SystemTest

class VertexWithoutRoadConnectionTest : SystemTest() {
    override val name = "VertexWithoutRoadConnectionTest"

    override val map = "mapFiles/vertexWithoutRoadConnection.dot"
    override val assets = "assetsJsons/validBaseFileSmall.json"
    override val scenario = "scenarioJsons/validScenarioFileSmall.json"
    override suspend fun run() {
        assertNextLine("Initialization Info: vertexWithoutRoadConnection.dot invalid")
    }

    override val maxTicks = 5
}
