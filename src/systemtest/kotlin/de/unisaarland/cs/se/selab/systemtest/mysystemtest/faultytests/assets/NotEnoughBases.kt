package de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.assets

import de.unisaarland.cs.se.selab.systemtest.api.SystemTest
class NotEnoughBases : SystemTest() {
    override val name = "NotEnoughBases"

    override val map = "mapFiles/example_map.dot"
    override val assets = "assetsJsons/notEnoughBases_assets.json"
    override val scenario = "scenarioJsons/example_scenario.json"
    override val maxTicks = 5
    override suspend fun run() {
        assertNextLine("Initialization Info: example_map.dot successfully parsed and validated")
        assertNextLine("Initialization Info: notEnoughBases_assets.json invalid")
    }
}
