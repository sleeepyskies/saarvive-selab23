package de.unisaarland.cs.se.selab.systemtest.mysystemtest.validtests.scenarios

import de.unisaarland.cs.se.selab.systemtest.api.SystemTest

class ValidScenario3 : SystemTest() {
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
        assertNextLine("Emergency Assignment: 1 assigned to 3")
        assertNextLine("Asset Allocation: 14 allocated to 1; 2 ticks to arrive.")
        assertNextLine("Simulation Tick: 5")
        assertNextLine("Simulation Tick: 6")
        assertNextLine("Asset Arrival: 14 arrived at 1.")
        assertNextLine("Emergency Handling Start: 1 handling started.")
        assertNextLine("Simulation Tick: 7")
        assertNextLine("Simulation Tick: 8")
        assertNextLine("Simulation Tick: 9")
        assertNextLine("Simulation Tick: 10")
        assertNextLine("Simulation Tick: 11")
        assertNextLine("Simulation Tick: 12")
        assertNextLine("Simulation Tick: 13")
        assertNextLine("Emergency Resolved: 1 resolved.")
        assertNextLine("Simulation End")
        assertNextLine("Simulation Statistics: 0 assets rerouted.")
        assertNextLine("Simulation Statistics: 1 received emergencies.")
        assertNextLine("Simulation Statistics: 0 ongoing emergencies.")
        assertNextLine("Simulation Statistics: 0 failed emergencies.")
        assertNextLine("Simulation Statistics: 1 resolved emergencies.")
    }
}
