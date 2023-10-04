package de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.map

import de.unisaarland.cs.se.selab.systemtest.api.SystemTest

class NoDigraphNameTest : SystemTest() {
    override val name = "NoDigraphNameTest"

    override val map = "mapFiles/noDigraphName.dot"
    override val assets = "assetsJsons/validBaseFileSmall.json"
    override val scenario = "scenarioJsons/validScenarioFileSmall.json"
    override suspend fun run() {
        assertNextLine("Initialization Info: noDigraphName.dot invalid")
    }

    override val maxTicks = 5
}
