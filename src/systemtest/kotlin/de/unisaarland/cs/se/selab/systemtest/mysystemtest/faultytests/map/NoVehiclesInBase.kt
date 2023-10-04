package de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.map

import de.unisaarland.cs.se.selab.systemtest.api.SystemTest
class NoVehiclesInBase : SystemTest() {
    override val name = "NoVehiclesInBase"

    override val map = "mapFiles/example_map.dot"
    override val assets = "assetsJsons/noVehicles_assets.json"
    override val scenario = "scenarioJsons/example_scenario.json"
    override val maxTicks = 5
    override suspend fun run() {
        assertNextLine("Initialization Info: example_map.dot successfully parsed and validated")
        assertNextLine("Initialization Info: noVehicles_assets.json invalid")
    }
}
