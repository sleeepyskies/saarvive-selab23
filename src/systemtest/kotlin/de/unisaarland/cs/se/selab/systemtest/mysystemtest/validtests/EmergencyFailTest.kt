package de.unisaarland.cs.se.selab.systemtest.mysystemtest.validtests

import de.unisaarland.cs.se.selab.systemtest.api.SystemTest

class EmergencyFailTest : SystemTest() {
    override val name = "EmergencyFailTest"

    override val map = "mapFiles/emergencyFailTestMap.dot"
    override val assets = "assetsJsons/emergencyFailTestAssets.json"
    override val scenario = "scenarioJsons/emergencyFailTestScenario.json"
    override val maxTicks = 6

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
        // Tick 2
        assertNextLine("Simulation Tick: 2")
        assertNextLine("Event Ended: 0 ended.")
        // Tick 3
        assertNextLine("Simulation Tick: 3")
        assertNextLine("Asset Arrival: 0 arrived at 3.")
        // Tick 4
        assertNextLine("Simulation Tick: 4")
        // Tick 5
        assertNextLine("Simulation Tick: 5")
        assertNextLine("Emergency Failed: 0 failed.")

        // Simulation End
        assertNextLine("Simulation End")
        // Simulation Statistics
        assertNextLine("Simulation Statistics: 0 assets rerouted.")
        assertNextLine("Simulation Statistics: 1 received emergencies.")
        assertNextLine("Simulation Statistics: 0 ongoing emergencies.")
        assertNextLine("Simulation Statistics: 1 failed emergencies.")
        assertNextLine("Simulation Statistics: 0 resolved emergencies.")
        // File End has been reached
        assertEnd()
    }
}
