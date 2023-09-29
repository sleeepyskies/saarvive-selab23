package de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests

import de.unisaarland.cs.se.selab.systemtest.api.SystemTest

class ZeroHeightTest : SystemTest() {
    override val name = "ZeroHeightTest"

    override val map = "mapFiles/heightIsZero.dot"
    override val assets = "assetsJsons/validBaseFileSmall.json"
    override val scenario = "scenarioJsons/validScenarioFileSmall.json"
    override suspend fun run() {
        assertNextLine("Initialization Info: heightIsZero.dot invalid")
    }

    override val maxTicks = 5
}
