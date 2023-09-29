package de.unisaarland.cs.se.selab.systemtest.basictests

import de.unisaarland.cs.se.selab.systemtest.api.SystemTest

class CorrectSystemTestSmall : SystemTest() {
    override val name = "CorrectSystemTestSmall"

    override val map = "mapFiles/validMapSmall.dot"
    override val assets = "assetsJsons/validBaseFileSmall.json"
    override val scenario = "scenarioJsons/validScenarioFileSmall.json"
    override val maxTicks = 4

    override suspend fun run() {
        // Everything is parsed and validated
        assertNextLine("Initialization Info: validMapSmall.dot successfully parsed and validated")
        assertNextLine("Initialization Info: validBaseFileSmall.json successfully parsed and validated")
        assertNextLine("Initialization Info: validScenarioFileSmall.json successfully parsed and validated")
        // The Simulation starts with Tick 0
        assertNextLine("Simulation starts")
        // Tick 0
        assertNextLine("Simulation Tick: 0")
        assertNextLine("Event Triggered: 0 triggered.")
        // Tick 1
        assertNextLine("Simulation Tick: 1")
        assertNextLine("Emergency Assignment: 0 assigned to 0")
        assertNextLine("Asset Allocation: 0 allocated to 0; 1 ticks to arrive.")
        assertNextLine("Asset Allocation: 1 allocated to 0; 1 ticks to arrive.")
        assertNextLine("Asset Arrival: 0 arrived at 1.")
        assertNextLine("Asset Arrival: 1 arrived at 1.")
        assertNextLine("Emergency Handling Start: 0 handling started.")
        assertNextLine("Event Ended: 0 ended.")
        // Tick 2
        assertNextLine("Simulation Tick: 2")
        // Tick 3
        assertNextLine("Simulation Tick: 3")
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
