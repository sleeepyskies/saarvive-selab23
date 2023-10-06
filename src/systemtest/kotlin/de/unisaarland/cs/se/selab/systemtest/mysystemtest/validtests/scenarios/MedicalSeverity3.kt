package de.unisaarland.cs.se.selab.systemtest.mysystemtest.validtests.scenarios

import de.unisaarland.cs.se.selab.systemtest.api.SystemTest

class MedicalSeverity3 : SystemTest() {
    override val name = "MedicalSeverity3"

    override val map = "mapFiles/complexScenario1.dot"
    override val assets = "assetsJsons/complexScenario1.json"
    override val scenario = "scenarioJsons/complexScenario1_simulation.json"
    override val maxTicks = 10

    override suspend fun run() {
        assertNextLine("Initialization Info: complexScenario1.dot successfully parsed and validated")
        assertNextLine("Initialization Info: medicalSeverity3_assets.json successfully parsed and validated")
        assertNextLine("Initialization Info: medicalSeverity3_simulation.json successfully parsed and validated")
        assertNextLine("Simulation starts")
        assertNextLine("Simulation Tick: 0")
        assertNextLine("Simulation Tick: 1")
        assertNextLine("Emergency Assignment: 0 assigned to 0")
        assertNextLine("Asset Allocation: 0 allocated to 0; 2 ticks to arrive.")
        assertNextLine("Asset Allocation: 1 allocated to 0; 2 ticks to arrive.")
        assertNextLine("Asset Allocation: 2 allocated to 0; 2 ticks to arrive.")
        assertNextLine("Asset Allocation: 3 allocated to 0; 2 ticks to arrive.")
        assertNextLine("Asset Allocation: 4 allocated to 0; 2 ticks to arrive.")
        assertNextLine("Asset Allocation: 5 allocated to 0; 2 ticks to arrive.")
        assertNextLine("Asset Allocation: 6 allocated to 0; 2 ticks to arrive.")
        assertNextLine("Asset Request: 1 sent to 1 for 0.")
        assertNextLine("Asset Allocation: 10 allocated to 0; 1 ticks to arrive.")
        assertNextLine("Asset Allocation: 11 allocated to 0; 1 ticks to arrive.")

        assertNextLine("Simulation Tick: 2")
        assertNextLine("Asset Arrival: 10 arrived at 13.")
        assertNextLine("Asset Arrival: 11 arrived at 13.")

        assertNextLine("Simulation Tick: 3")
        assertNextLine("Asset Arrival: 0 arrived at 13.")
        assertNextLine("Asset Arrival: 1 arrived at 13.")
        assertNextLine("Asset Arrival: 2 arrived at 13.")
        assertNextLine("Asset Arrival: 3 arrived at 13.")
        assertNextLine("Asset Arrival: 4 arrived at 13.")
        assertNextLine("Asset Arrival: 5 arrived at 13.")
        assertNextLine("Asset Arrival: 6 arrived at 13.")
        assertNextLine("Emergency Handling Start: 0 handling started.")

        assertNextLine("Simulation Tick: 4")
        assertNextLine("Simulation Tick: 5")
        assertNextLine("Simulation Tick: 6")
        assertNextLine("Emergency Resolved: 0 resolved.")

        assertNextLine("Simulation Tick: 7")
        assertNextLine("Asset Arrival: 10 arrived at 12.")
        assertNextLine("Asset Arrival: 11 arrived at 12.")

        assertNextLine("Simulation Tick: 8")
        assertNextLine("Asset Arrival: 0 arrived at 15.")
        assertNextLine("Asset Arrival: 1 arrived at 15.")
        assertNextLine("Asset Arrival: 2 arrived at 15.")
        assertNextLine("Asset Arrival: 3 arrived at 15.")
        assertNextLine("Asset Arrival: 4 arrived at 15.")
        assertNextLine("Asset Arrival: 5 arrived at 15.")
        assertNextLine("Asset Arrival: 6 arrived at 15.")

        assertNextLine("Simulation Tick: 9")
        // end of simulation
        assertNextLine("Simulation End")
        // Statistics
        assertNextLine("Simulation Statistics: 0 assets rerouted.")
        assertNextLine("Simulation Statistics: 1 received emergencies.")
        assertNextLine("Simulation Statistics: 0 ongoing emergencies.")
        assertNextLine("Simulation Statistics: 0 failed emergencies.")
        assertNextLine("Simulation Statistics: 1 resolved emergencies.")
        // end of logging
        assertEnd()
    }
}
