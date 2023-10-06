package de.unisaarland.cs.se.selab.systemtest.mysystemtest.validtests.scenarios

import de.unisaarland.cs.se.selab.systemtest.api.SystemTest

class FATHERvalidScenario : SystemTest() {
    override val name = "FATHERvalidScenario"

    override val map = "mapFiles/FATHERvalidScenario_map.dot"
    override val assets = "assetsJsons/FATHERvalidScenario_bases.json"
    override val scenario = "scenarioJsons/FATHERvalidScenario_simulation.json"
    override val maxTicks = 30

    // this test tests on requests with water capacity
    override suspend fun run() {
        assertNextLine("Initialization Info: FATHERvalidScenario_map.dot successfully parsed and validated")
        assertNextLine("Initialization Info: FATHERvalidScenario_bases.json successfully parsed and validated")
        assertNextLine("Initialization Info: FATHERvalidScenario_simulation.json successfully parsed and validated")

        assertNextLine("Simulation starts")
        assertNextLine("Simulation Tick: 0")
        assertNextLine("Event Triggered: 0 triggered.")

        assertNextLine("Simulation Tick: 1")
        assertNextLine("Event Ended: 0 ended.")

        assertNextLine("Simulation Tick: 2")

        assertNextLine("Simulation Tick: 3")
        assertNextLine("Emergency Assignment: 0 assigned to 20")
        assertNextLine("Asset Allocation: 21 allocated to 0; 1 ticks to arrive.")
        assertNextLine("Asset Request: 1 sent to 0 for 0.")
        assertNextLine("Asset Allocation: 3 allocated to 0; 8 ticks to arrive.")

        assertNextLine("Simulation Tick: 4")
        assertNextLine("Asset Arrival: 21 arrived at 5.")

        assertNextLine("Simulation Tick: 5")
        assertNextLine("Simulation Tick: 6")
        assertNextLine("Simulation Tick: 7")
        assertNextLine("Simulation Tick: 8")
        assertNextLine("Simulation Tick: 9")
        assertNextLine("Simulation Tick: 10")

        assertNextLine("Simulation Tick: 11")
        assertNextLine("Asset Arrival: 3 arrived at 4.")
        assertNextLine("Emergency Handling Start: 0 handling started.")

        assertNextLine("Simulation Tick: 12")

        assertNextLine("Simulation Tick: 13")
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
