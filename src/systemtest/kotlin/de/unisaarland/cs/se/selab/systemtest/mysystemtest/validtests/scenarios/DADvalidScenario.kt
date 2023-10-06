package de.unisaarland.cs.se.selab.systemtest.mysystemtest.validtests.scenarios

import de.unisaarland.cs.se.selab.systemtest.api.SystemTest

class DADvalidScenario : SystemTest() {
    override val name = "DADvalidScenario"

    override val map = "mapFiles/DADvalidScenario_map.dot"
    override val assets = "assetsJsons/DADvalidScenario_bases.json"
    override val scenario = "scenarioJsons/DADvalidScenario_simulation.json"
    override val maxTicks = 30

    // this test tests on requests
    override suspend fun run() {
        assertNextLine("Initialization Info: DADvalidScenario_map.dot successfully parsed and validated")
        assertNextLine("Initialization Info: DADvalidScenario_bases.json successfully parsed and validated")
        assertNextLine("Initialization Info: DADvalidScenario_simulation.json successfully parsed and validated")

        assertNextLine("Simulation starts")
        assertNextLine("Simulation Tick: 0")
        assertNextLine("Event Triggered: 0 triggered.") // traffic jam happens

        assertNextLine("Simulation Tick: 1")
        assertNextLine("Event Ended: 0 ended.") // traffic jam ends

        assertNextLine("Simulation Tick: 2")

        assertNextLine("Simulation Tick: 3")
        assertNextLine("Emergency Assignment: 0 assigned to 1") // crime sev2 assigned to PC id1
        assertNextLine("Asset Allocation: 4 allocated to 0; 6 ticks to arrive.") // PC id 4
        assertNextLine("Asset Allocation: 5 allocated to 0; 6 ticks to arrive.") // PC id 5
        assertNextLine("Asset Allocation: 6 allocated to 0; 6 ticks to arrive.") // PC id 6
        assertNextLine("Asset Allocation: 7 allocated to 0; 6 ticks to arrive.") // PC id 7
        assertNextLine("Asset Allocation: 8 allocated to 0; 6 ticks to arrive.") // K9PC id 8 all allocated to crime
        assertNextLine("Asset Request: 1 sent to 2 for 0.") // ayiyayiya
        assertNextLine("Asset Allocation: 9 allocated to 0; 5 ticks to arrive.")

        assertNextLine("Simulation Tick: 4")
        assertNextLine("Simulation Tick: 5")
        assertNextLine("Simulation Tick: 6")
        assertNextLine("Simulation Tick: 7")

        assertNextLine("Simulation Tick: 8")
        assertNextLine("Asset Arrival: 9 arrived at 4.")

        assertNextLine("Simulation Tick: 9")
        assertNextLine("Asset Arrival: 4 arrived at 4.")
        assertNextLine("Asset Arrival: 5 arrived at 4.")
        assertNextLine("Asset Arrival: 6 arrived at 4.")
        assertNextLine("Asset Arrival: 7 arrived at 4.")
        assertNextLine("Asset Arrival: 8 arrived at 4.")
        assertNextLine("Emergency Handling Start: 0 handling started.") // crime handle time is 2

        assertNextLine("Simulation Tick: 10")

        assertNextLine("Simulation Tick: 11")
        assertNextLine("Emergency Resolved: 0 resolved.")

        assertNextLine("Simulation End")
        assertNextLine("Simulation Statistics: 0 assets rerouted.")
        assertNextLine("Simulation Statistics: 1 received emergencies.")
        assertNextLine("Simulation Statistics: 0 ongoing emergencies.")
        assertNextLine("Simulation Statistics: 0 failed emergencies.")
        assertNextLine("Simulation Statistics: 1 resolved emergencies.")
        assertEnd()
    }
}
