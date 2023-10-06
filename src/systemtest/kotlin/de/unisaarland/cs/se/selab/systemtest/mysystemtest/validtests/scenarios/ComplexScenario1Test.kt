package de.unisaarland.cs.se.selab.systemtest.mysystemtest.validtests.scenarios

import de.unisaarland.cs.se.selab.systemtest.api.SystemTest

class ComplexScenario1Test : SystemTest() {
    override val name = "ComplexScenario1Test"

    override val map = "mapFiles/complexScenario1.dot"
    override val assets = "assetsJsons/complexScenario1.json"
    override val scenario = "scenarioJsons/complexScenario1_simulation.json"
    override val maxTicks = 50

    override suspend fun run() {
        assertNextLine("Initialization Info: complexScenario1.dot successfully parsed and validated")
        assertNextLine("Initialization Info: complexScenario1.json successfully parsed and validated")
        assertNextLine("Initialization Info: complexScenario1_simulation.json successfully parsed and validated")
        // start of simulation
        assertNextLine("Simulation starts")
        assertNextLine("Simulation Tick: 0")
        // 1 tick
        assertNextLine("Simulation Tick: 1")
        assertNextLine("Emergency Assignment: 0 assigned to 7")
        assertNextLine("Emergency Assignment: 1 assigned to 5")
        assertNextLine("Asset Allocation: 7 allocated to 0; 1 ticks to arrive.")
        assertNextLine("Asset Allocation: 23 allocated to 1; 1 ticks to arrive.")
        assertNextLine("Event Triggered: 2 triggered.")
        assertNextLine("Assets Rerouted: 1")
        // 2 tick
        assertNextLine("Simulation Tick: 2")
        assertNextLine("Emergency Assignment: 2 assigned to 1")
        assertNextLine("Asset Allocation: 8 allocated to 2; 1 ticks to arrive.")
        assertNextLine("Asset Allocation: 9 allocated to 2; 1 ticks to arrive.")
        assertNextLine("Asset Allocation: 16 allocated to 2; 1 ticks to arrive.")
        assertNextLine("Asset Request: 1 sent to 0 for 2.")
        assertNextLine("Asset Request: 2 sent to 3 for 2.")
        assertNextLine("Asset Request: 3 sent to 4 for 2.")
        assertNextLine("Asset Allocation: 0 allocated to 2; 2 ticks to arrive.")
        assertNextLine("Asset Allocation: 1 allocated to 2; 2 ticks to arrive.")
        assertNextLine("Asset Allocation: 39 allocated to 2; 3 ticks to arrive.")
        assertNextLine("Asset Allocation: 17 allocated to 2; 3 ticks to arrive.")
        assertNextLine("Asset Allocation: 18 allocated to 2; 3 ticks to arrive.")
        assertNextLine("Asset Allocation: 19 allocated to 2; 3 ticks to arrive.")
        assertNextLine("Asset Allocation: 21 allocated to 2; 3 ticks to arrive.")
        assertNextLine("Asset Request: 4 sent to 5 for 2.")
        assertNextLine("Asset Allocation: 24 allocated to 2; 6 ticks to arrive.")
        assertNextLine("Asset Allocation: 25 allocated to 2; 6 ticks to arrive.")
        assertNextLine("Asset Allocation: 100 allocated to 2; 6 ticks to arrive.")
        assertNextLine("Asset Arrival: 7 arrived at 3.")
        assertNextLine("Emergency Handling Start: 0 handling started.")
        assertNextLine("Event Ended: 2 ended.")
        assertNextLine("Event Triggered: 0 triggered.") // should affect the road calculations
        assertNextLine("Event Triggered: 1 triggered.") // should affect the road calculations
        assertNextLine("Assets Rerouted: 14")
        // 3 tick
        assertNextLine("Simulation Tick: 3")
        assertNextLine("Asset Arrival: 8 arrived at 13.")
        assertNextLine("Asset Arrival: 9 arrived at 13.")
        assertNextLine("Asset Arrival: 16 arrived at 13.")
        assertNextLine("Asset Arrival: 23 arrived at 3.")
        assertNextLine("Emergency Handling Start: 1 handling started.")
        assertNextLine("Emergency Resolved: 0 resolved.")
        assertNextLine("Event Ended: 1 ended.")
        assertNextLine("Event Triggered: 3 triggered.") // should affect the road calculations
        assertNextLine("Assets Rerouted: 7")
        // 4 tick
        assertNextLine("Simulation Tick: 4")
        assertNextLine("Asset Arrival: 7 arrived at 7.")

        assertNextLine("Emergency Resolved: 1 resolved.")
        assertNextLine("Event Ended: 3 ended.")

        // 5 tick
        assertNextLine("Simulation Tick: 5")
        assertNextLine("Asset Arrival: 0 arrived at 15.")
        assertNextLine("Asset Arrival: 1 arrived at 15.")
        assertNextLine("Asset Arrival: 17 arrived at 13.")
        assertNextLine("Asset Arrival: 18 arrived at 13.")
        assertNextLine("Asset Arrival: 19 arrived at 13.")
        assertNextLine("Asset Arrival: 21 arrived at 13.")
        assertNextLine("Asset Arrival: 39 arrived at 13.")
        assertNextLine("Event Ended: 0 ended.")
        assertNextLine("Assets Rerouted: 1")

        // 6 tick
        assertNextLine("Simulation Tick: 6")
        assertNextLine("Emergency Assignment: 3 assigned to 2")
        assertNextLine("Asset Allocation: 35 allocated to 3; 1 ticks to arrive.")
        assertNextLine("Asset Request: 5 sent to 3 for 3.")
        assertNextLine("Asset Request: 6 sent to 8 for 3.")
        assertNextLine("Request Failed: 6 failed.")
        assertNextLine("Asset Arrival: 23 arrived at 6.")

        // 7 tick
        assertNextLine("Simulation Tick: 7")
        assertNextLine("Asset Request: 7 sent to 3 for 3.")
        assertNextLine("Asset Request: 8 sent to 8 for 3.")
        assertNextLine("Request Failed: 8 failed.")
        assertNextLine("Asset Arrival: 35 arrived at 19.")

        // 8 tick
        assertNextLine("Simulation Tick: 8")
        assertNextLine("Asset Request: 9 sent to 3 for 3.")
        assertNextLine("Asset Request: 10 sent to 8 for 3.")
        assertNextLine("Request Failed: 10 failed.")

        // 9 tick
        assertNextLine("Simulation Tick: 9")
        assertNextLine("Asset Request: 11 sent to 3 for 3.")
        assertNextLine("Asset Request: 12 sent to 8 for 3.")
        assertNextLine("Request Failed: 12 failed.")
        assertNextLine("Asset Arrival: 24 arrived at 13.")
        assertNextLine("Asset Arrival: 25 arrived at 13.")
        assertNextLine("Asset Arrival: 100 arrived at 13.")
        assertNextLine("Emergency Handling Start: 2 handling started.")
        assertNextLine("Emergency Failed: 3 failed.")

        // 10 tick
        assertNextLine("Simulation Tick: 10")

        // 11 tick
        assertNextLine("Simulation Tick: 11")
        assertNextLine("Asset Arrival: 35 arrived at 20.")
        assertNextLine("Event Triggered: 4 triggered.")

        // 12 tick
        assertNextLine("Simulation Tick: 12")
        assertNextLine("Event Ended: 4 ended.")

        // 13 tick
        assertNextLine("Simulation Tick: 13")

        // 14 tick
        assertNextLine("Simulation Tick: 14")

        // 15 tick
        assertNextLine("Simulation Tick: 15")

        // 16 tick
        assertNextLine("Simulation Tick: 16")
        assertNextLine("Emergency Resolved: 2 resolved.")

        // end of simulation
        assertNextLine("Simulation End")
        // Statistics
        assertNextLine("Simulation Statistics: 23 assets rerouted.")
        assertNextLine("Simulation Statistics: 4 received emergencies.")
        assertNextLine("Simulation Statistics: 0 ongoing emergencies.")
        assertNextLine("Simulation Statistics: 1 failed emergencies.")
        assertNextLine("Simulation Statistics: 3 resolved emergencies.")
        // end of file is reached
        assertEnd()
    }
}
