package de.unisaarland.cs.se.selab.systemtest.basictests

import de.unisaarland.cs.se.selab.systemtest.api.SystemTest

class EmergencyFailTest : SystemTest() {
    override val name = "EmergencyFailTest"

    override val map = "mapFiles/emergencyFailTestMap.dot"
    override val assets = "assetsJsons/emergencyFailTestAssets.json"
    override val scenario = "scenarioJsons/emergencyFailTestScenario.json"
    override val maxTicks = 8

    override suspend fun run() {
        // Everything is parsed and validated
        assertNextLine("Initialization Info: example_map.dot successfully parsed and validated")
        assertNextLine("Initialization Info: example_assets.json successfully parsed and validated")
        assertNextLine("Initialization Info: example_scenario.json successfully parsed and validated")
        // The Simulation starts with Tick 0
        assertNextLine("Simulation starts")
        // Tick 0
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
