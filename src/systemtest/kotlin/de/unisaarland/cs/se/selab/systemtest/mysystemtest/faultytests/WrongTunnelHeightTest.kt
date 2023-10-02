package de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests

import de.unisaarland.cs.se.selab.systemtest.api.SystemTest

class WrongTunnelHeightTest : SystemTest() {
    override val name = "WrongTunnelHeightTest"

    override val map = "mapFiles/wrongTunnelHeight.dot"
    override val assets = "assetsJsons/validBaseFileSmall.json"
    override val scenario = "scenarioJsons/validScenarioFileSmall.json"
    override suspend fun run() {
        assertNextLine("Initialization Info: wrongTunnelHeight.dot invalid")
    }

    override val maxTicks = 5
}
