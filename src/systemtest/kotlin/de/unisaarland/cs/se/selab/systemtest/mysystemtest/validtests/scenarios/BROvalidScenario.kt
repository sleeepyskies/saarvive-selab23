package de.unisaarland.cs.se.selab.systemtest.mysystemtest.validtests.scenarios

import de.unisaarland.cs.se.selab.systemtest.api.SystemTest

class BROvalidScenario : SystemTest() { // for push 2.0
    override val name = "BROvalidScenario"

    override val map = "mapFiles/BROvalidScenario_map.dot"
    override val assets = "assetsJsons/BROvalidScenario_bases.json"
    override val scenario = "scenarioJsons/BROvalidScenario_simulation.json"
    override val maxTicks = 20

    override suspend fun run() {
        assertNextLine("Initialization Info: BROvalidScenario_map.dot successfully parsed and validated")
        assertNextLine("Initialization Info: BROvalidScenario_bases.json successfully parsed and validated")
        assertNextLine("Initialization Info: BROvalidScenario_simulation.json successfully parsed and validated")
        // start of simulation
        assertNextLine("Simulation starts")
        assertNextLine("Simulation Tick: 0")
        assertNextLine("Event Triggered: 0 triggered.")

        assertNextLine("Simulation Tick: 1")
        assertNextLine("Emergency Assignment: 0 assigned to 1")
        assertNextLine("Asset Allocation: 4 allocated to 0; 2 ticks to arrive.")

        assertNextLine("Simulation Tick: 2")

        assertNextLine("Simulation Tick: 3")
        assertNextLine("Emergency Assignment: 1 assigned to 2") // medical assigned to hospital
        assertNextLine("Asset Allocation: 7 allocated to 1; 1 ticks to arrive.") // Ambulance 1
        assertNextLine("Asset Allocation: 8 allocated to 1; 1 ticks to arrive.") // Ambulance 2
        assertNextLine("Asset Allocation: 9 allocated to 1; 1 ticks to arrive.") // Doctor car
        assertNextLine("Asset Arrival: 4 arrived at 3.") // policeC id reach vertex 3 crime
        assertNextLine("Emergency Handling Start: 0 handling started.") // crime handling starts (needs 2 ticks)

        assertNextLine("Simulation Tick: 4")
        assertNextLine("Asset Arrival: 7 arrived at 2.") // Ambulance 1 arrival
        assertNextLine("Asset Arrival: 8 arrived at 2.") // Ambulance 2 arrival
        assertNextLine("Asset Arrival: 9 arrived at 2.") // Doctor car arrival
        assertNextLine("Emergency Handling Start: 1 handling started.") // medical handling starts (needs 3 ticks)
        assertNextLine("Event Ended: 0 ended.") // Doctor car arrival

        assertNextLine("Simulation Tick: 5")
        assertNextLine("Emergency Resolved: 0 resolved.") // crime resolved after 2 ticks from tick 3

        assertNextLine("Simulation Tick: 6")

        assertNextLine("Simulation Tick: 7")
        assertNextLine("Emergency Resolved: 1 resolved.") // medical resolved after 3 ticks from tick 4

        // end of simulation
        assertNextLine("Simulation End")
        assertNextLine("Simulation Statistics: 0 assets rerouted.")
        assertNextLine("Simulation Statistics: 2 received emergencies.")
        assertNextLine("Simulation Statistics: 0 ongoing emergencies.")
        assertNextLine("Simulation Statistics: 0 failed emergencies.")
        assertNextLine("Simulation Statistics: 2 resolved emergencies.")
        // end of file is reached
        assertEnd()
    }
}
