package de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.map

import de.unisaarland.cs.se.selab.systemtest.api.SystemTest

class NoRoadBetween : SystemTest() {
    override val name = "NoRoadBetween"

    override val map = "mapFiles/noRoadBetween_map.dot"
    override val assets = "assetsJsons/validScenario3_assets.json"
    override val scenario = "scenarioJsons/validScenario3_simulation.json"
    override val maxTicks = 5
    override suspend fun run() {
        assertNextLine("Initialization Info: noRoadBetween_map.dot invalid")
    }
}
