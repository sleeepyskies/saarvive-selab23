package de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.map

import de.unisaarland.cs.se.selab.systemtest.api.SystemTest

class InvalidStringIDTest : SystemTest() {
    override val name = "InvalidStringIDTest"

    override val map = "mapFiles/invalidStringID.dot"
    override val assets = "assetsJsons/validBaseFileSmall.json"
    override val scenario = "scenarioJsons/validScenarioFileSmall.json"
    override suspend fun run() {
        assertNextLine("Initialization Info: invalidStringID.dot invalid")
    }

    override val maxTicks = 5
}
