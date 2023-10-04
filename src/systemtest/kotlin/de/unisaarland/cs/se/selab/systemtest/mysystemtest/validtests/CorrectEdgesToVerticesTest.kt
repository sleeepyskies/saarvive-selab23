package de.unisaarland.cs.se.selab.systemtest.mysystemtest.validtests

import de.unisaarland.cs.se.selab.systemtest.api.SystemTest

class CorrectEdgesToVerticesTest : SystemTest() {
    override val name = "CorrectEdgesToVerticesTest"

    override val map = "mapFiles/correctEdgesToVertices.dot"
    override val assets = "assetsJsons/validBaseFileSmall.json"
    override val scenario = "scenarioJsons/validScenarioFileSmall.json"
    override suspend fun run() {
        assertNextLine("Initialization Info: correctEdgesToVertices.dot successfully parsed and validated")
    }

    override val maxTicks = 5
}
