package de.unisaarland.cs.se.selab.systemtest.mysystemtest.validtests

import de.unisaarland.cs.se.selab.systemtest.api.SystemTest

class RerouteTest : SystemTest() {
    override val name = "RerouteTest"

    override val map = "mapFiles/rerouteTestMap.dot"
    override val assets = "assetsJsons/rerouteTestAssets.json"
    override val scenario = "scenarioJsons/rerouteTestScenario.json"
    override val maxTicks = 2

    override suspend fun run() {
        // Everything is parsed and validated
        assertNextLine("Initialization Info: emergencyFailTestMap.dot successfully parsed and validated")
        assertNextLine("Initialization Info: emergencyFailTestAssets.json successfully parsed and validated")
        assertNextLine("Initialization Info: emergencyFailTestScenario.json successfully parsed and validated")
        // The Simulation starts with Tick 0
        assertNextLine("Simulation starts")
        // Tick 0
        assertNextLine("Simulation Tick: 0")
        assertNextLine("Event Triggered: 0 triggered.")
        // Tick 1
        assertNextLine("Simulation Tick: 1")
        assertNextLine("Emergency Assignment: 0 assigned to 0")
        assertNextLine("Asset Allocation: 0 allocated to 0; 3 ticks to arrive.")

        // Simulation End
        assertNextLine("Simulation End")
        // Simulation Statistics
        assertNextLine("Simulation Statistics: 0 assets rerouted.")
        assertNextLine("Simulation Statistics: 1 received emergencies.")
        assertNextLine("Simulation Statistics: 1 ongoing emergencies.")
        assertNextLine("Simulation Statistics: 0 failed emergencies.")
        assertNextLine("Simulation Statistics: 0 resolved emergencies.")
        // File End has been reached
        assertEnd()
    }
}
