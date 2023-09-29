package de.unisaarland.cs.se.selab.systemtest.mysystemtest.validtests

import de.unisaarland.cs.se.selab.systemtest.api.SystemTest

class RequestTest : SystemTest() {
    override val name = "NegativeVertexTest"

    override val map = "mapFiles/requestTestMap.dot"
    override val assets = "assetsJsons/requestTestAssets.json"
    override val scenario = "scenarioJsons/requestTestScenario.json"
    override val maxTicks = 5
    override suspend fun run() {
        // Everything is parsed and validated
        assertNextLine("Initialization Info: requestTestMap.dot successfully parsed and validated")
        assertNextLine("Initialization Info: requestTestAssets.json successfully parsed and validated")
        assertNextLine("Initialization Info: requestTestScenario.json successfully parsed and validated")
        // The Simulation starts with Tick 0
        assertNextLine("Simulation starts")
        // Tick 0
        assertNextLine("Simulation Tick: 0")
        assertNextLine("Event Triggered: 0 triggered.")
        // Tick 1
        assertNextLine("Simulation Tick: 1")
        assertNextLine("Event Ended: 0 ended.")
        // Tick 2
        assertNextLine("Simulation Tick: 2")
        assertNextLine("Emergency Assignment: 0 assigned to 1")
        assertNextLine("Asset Allocation: 1 allocated to 0; 2 ticks to arrive.")
        assertNextLine("Asset Request: 0 sent to 0 for 0.")
        assertNextLine("Asset Allocation: 0 allocated to 0; 3 ticks to arrive.")
        // Tick 3
        assertNextLine("Simulation Tick: 3")
        // Tick 4
        assertNextLine("Simulation Tick: 4")
        assertNextLine("Asset Arrival: 1 arrived at 4.")
        // Tick 5
        assertNextLine("Simulation Tick: 5")
        assertNextLine("Asset Arrival: 0 arrived at 4.")
        assertNextLine("Emergency Handling Start: 0 handling started.")
        // Tick 6
        assertNextLine("Simulation Tick: 6")
        // Tick 7
        assertNextLine("Simulation Tick: 7")
        assertNextLine("Emergency Resolved: 0 resolved.")
        // Simulation End
        assertNextLine("Simulation End")
        // Simulation Statistics
        assertNextLine("Simulation Statistics: 0 assets rerouted.")
        assertNextLine("Simulation Statistics: 1 received emergencies.")
        assertNextLine("Simulation Statistics: 0 ongoing emergencies.")
        assertNextLine("Simulation Statistics: 0 failed emergencies.")
        assertNextLine("Simulation Statistics: 1 resolved emergencies.")
        // File End has been reached
        assertEnd()
    }
}
