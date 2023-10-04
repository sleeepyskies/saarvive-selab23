package de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.assets

import de.unisaarland.cs.se.selab.systemtest.api.SystemTest
class TooManyCriminals : SystemTest() {
    override val name = "TooManyCriminals"

    override val map = "mapFiles/example_map.dot"
    override val assets = "assetsJsons/tooManyCriminals_assets.json"
    override val scenario = "scenarioJsons/example_scenario.json"
    override val maxTicks = 5
    override suspend fun run() {
        assertNextLine("Initialization Info: example_map.dot successfully parsed and validated")
        assertNextLine("Initialization Info: tooManyCriminals_assets.json invalid")
    }
}
