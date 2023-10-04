package de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.map

import de.unisaarland.cs.se.selab.systemtest.api.SystemTest

class NotTheEndTest : SystemTest() {
    override val name = "NotTheEndTest"

    override val map = "mapFiles/notTheEnd.dot"
    override val assets = "assetsJsons/validBaseFileSmall.json"
    override val scenario = "scenarioJsons/validScenarioFileSmall.json"
    override suspend fun run() {
        assertNextLine("Initialization Info: notTheEnd.dot invalid")
    }

    override val maxTicks = 5
}
