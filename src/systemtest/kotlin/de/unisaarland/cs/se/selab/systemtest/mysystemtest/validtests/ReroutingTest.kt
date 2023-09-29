package de.unisaarland.cs.se.selab.systemtest.mysystemtest.validtests

import de.unisaarland.cs.se.selab.systemtest.api.SystemTest

class ReroutingTest : SystemTest() {
    override val name = "ReroutingTest"

    override val map = "mapFiles/reroutingTestMap.dot"
    override val assets = "assetsJsons/reroutingTestAssets.json"
    override val scenario = "scenarioJsons/reroutingTestScenario.json"
    override val maxTicks = 5

    override suspend fun run() {
        // Everything is parsed and validated
        assertNextLine("Initialization Info: reroutingTestMap.dot successfully parsed and validated")
        assertNextLine("Initialization Info: reroutingTestAssets.json successfully parsed and validated")
        assertNextLine("Initialization Info: reroutingTestScenario.json successfully parsed and validated")
        // The Simulation starts with Tick 0
        assertNextLine("Simulation starts")
        // Tick 0
        assertNextLine("Simulation Tick: 0")
        assertNextLine("Emergency Assignment: 0 assigned to 0")
        assertNextLine("Asset Allocation: 0 allocated to 0; 4 ticks to arrive. ")
        assertNextLine("Asset Allocation: 1 allocated to 0; 4 ticks to arrive. ")
        assertNextLine("Event Triggered: 0 triggered.")
        assertNextLine("Assets Rerouted: 2")
        // Tick 1
        assertNextLine("Event Ended: 0 ended.")
        // Simulation End
        assertNextLine("Simulation End")
        // Simulation Statistics
        assertNextLine("Simulation Statistics: 0 assets rerouted.")
        assertNextLine("Simulation Statistics: 2 received emergencies.")
        assertNextLine("Simulation Statistics: 1 ongoing emergencies.")
        assertNextLine("Simulation Statistics: 0 failed emergencies.")
        assertNextLine("Simulation Statistics: 1 resolved emergencies.")
        // File End has been reached
        assertEnd()
    }
}
