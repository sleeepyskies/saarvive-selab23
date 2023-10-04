package de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.assets

import de.unisaarland.cs.se.selab.systemtest.api.SystemTest
class PuppyHeavenTest : SystemTest() {
    override val name = "PuppyHeavenTest"

    override val map = "mapFiles/example_map.dot"
    override val assets = "assetsJsons/puppyHeaven_assets.json"
    override val scenario = "scenarioJsons/example_scenario.json"
    override val maxTicks = 5
    override suspend fun run() {
        assertNextLine("Initialization Info: example_map.dot successfully parsed and validated")
        assertNextLine("Initialization Info: puppyHeaven_assets.json invalid")
    }
}
