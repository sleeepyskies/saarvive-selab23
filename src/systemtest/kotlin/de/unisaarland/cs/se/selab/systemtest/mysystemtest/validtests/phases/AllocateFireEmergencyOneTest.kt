package de.unisaarland.cs.se.selab.systemtest.mysystemtest.validtests

import de.unisaarland.cs.se.selab.systemtest.api.SystemTest

class AllocateFireEmergencyOneTest : SystemTest() {
    override val name = "AllocateFireEmergencyOneTest"

    override val map = "mapFiles/correctEdgesToVertices.dot"
    override val assets = "assetsJsons/validBaseFileSmall.json"
    override val scenario = "scenarioJsons/validScenarioFileSmall.json"
    override val maxTicks = 5

    override suspend fun run() {
        assertNextLine("Initialization Info: correctEdgesToVertices.dot successfully parsed and validated")
    }
}
