package de.unisaarland.cs.se.selab.systemtest.mysystemtest.validtests.scenarios

import de.unisaarland.cs.se.selab.systemtest.api.SystemTest

class ValidScenario3 : SystemTest(){
    override val name = "ValidScenario3"
    override val map = "mapFiles/validScenario3_map.dot"
    override val assets = "assetsJsons/validScenario3_assets.json"
    override val scenario = "scenarioJsons/validScenario3_simulation.json"
    override val maxTicks = 50

    override suspend fun run() {
        assertNextLine("Initialization Info: validScenario3_map.dot successfully parsed and validated")
        assertNextLine("Initialization Info: validScenario3_assets.json successfully parsed and validated")
        assertNextLine("Initialization Info: validScenario3_simulation.json successfully parsed and validated")

        assertNextLine("Simulation starts")
        assertNextLine("Simulation Tick: 0")
        assertNextLine("Simulation Tick: 1")
        assertNextLine("Simulation Tick: 2")
        assertNextLine("Simulation Tick: 3")
        assertNextLine("Simulation Tick: 4")
    }
}