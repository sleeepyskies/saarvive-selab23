package de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.map

import de.unisaarland.cs.se.selab.systemtest.api.SystemTest

class HasOnlyRoadsTest : SystemTest() {
    override val name = "HasOnlyRoads"

    override val map = "mapFiles/hasOnlyRoads.dot"
    override val assets = "assetsJsons/validBaseFileSmall.json"
    override val scenario = "scenarioJsons/validScenarioFileSmall.json"
    override suspend fun run() {
        assertNextLine("Initialization Info: hasOnlyRoads.dot invalid")
    }

    override val maxTicks = 5
}
