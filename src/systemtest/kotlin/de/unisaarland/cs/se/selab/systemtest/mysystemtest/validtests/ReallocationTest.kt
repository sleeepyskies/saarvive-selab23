package de.unisaarland.cs.se.selab.systemtest.mysystemtest.validtests

import de.unisaarland.cs.se.selab.systemtest.api.SystemTest

class ReallocationTest : SystemTest() {
    override val name = "ReallocationTest"

    override val map = "mapFiles/rerouteTestMap.dot"
    override val assets = "assetsJsons/rerouteTestAssets.json"
    override val scenario = "scenarioJsons/rerouteTestScenario.json"
    override val maxTicks = 5

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
        assertNextLine("Event Ended: 0 ended.")
        // Tick 2
        assertNextLine("Simulation Tick: 2")
        assertNextLine("Emergency Assignment: 0 assigned to 0")
        assertNextLine("Asset Allocation: 0 allocated to 0; 3 ticks to arrive.")
        assertNextLine("Asset Allocation: 1 allocated to 0; 3 ticks to arrive.")
        // Tick 3
        assertNextLine("Simulation Tick: 3")
        assertNextLine("Emergency Assignment: 1 assigned to 0")
        assertNextLine("Asset Allocation: 2 allocated to 1; 0 ticks to arrive.")
        assertNextLine("Asset Allocation: 3 allocated to 1; 0 ticks to arrive.")
        assertNextLine("Asset Allocation: 4 allocated to 1; 0 ticks to arrive.")
        assertNextLine("Asset Allocation: 5 allocated to 1; 0 ticks to arrive.")
        assertNextLine("Asset Reallocation: 0 reallocated to 1.")
        assertNextLine("Asset Reallocation: 1 reallocated to 1.")
        assertNextLine("Asset Request: 0 sent to 1 for 1")
        assertNextLine("Asset Allocation: 6 allocated to 1; 0 ticks to arrive.")

        assertNextLine("Asset Arrival: 0 arrived at 1.")
        assertNextLine("Asset Arrival: 1 arrived at 1.")
        assertNextLine("Asset Arrival: 2 arrived at 0.")
        assertNextLine("Asset Arrival: 3 arrived at 0.")
        assertNextLine("Asset Arrival: 4 arrived at 0.")
        assertNextLine("Asset Arrival: 5 arrived at 0.")
        assertNextLine("Asset Arrival: 6 arrived at 0.")
        assertNextLine("Emergency Handling Start: 0 handling started.")
        // Tick 4
        assertNextLine("Simulation Tick: 4")
        assertNextLine("Emergency Resolved: 0 resolved.")
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
