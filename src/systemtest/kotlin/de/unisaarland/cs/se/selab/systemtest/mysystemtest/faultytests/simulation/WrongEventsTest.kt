package de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.simulation

import de.unisaarland.cs.se.selab.systemtest.api.SystemTest

class WrongEventsTest : SystemTest() {
    override val name = "WrongEventsTest"

    override val map = "mapFiles/example_map.dot"
    override val assets = "assetsJsons/example_assets.json"
    override val scenario = "scenarioJsons/wrong_events.json"
    override val maxTicks = 5

    override suspend fun run() {
        assertNextLine("Initialization Info: example_map.dot successfully parsed and validated")
        assertNextLine("Initialization Info: example_assets.json successfully parsed and validated")
        assertNextLine("Initialization Info: wrong_events.json invalid")
    }
}
