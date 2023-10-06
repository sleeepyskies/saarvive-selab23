package de.unisaarland.cs.se.selab.systemtest.mysystemtest.validtests.scenarios

import de.unisaarland.cs.se.selab.systemtest.api.SystemTest

class FireTrucksRefillTest : SystemTest() {
    override val name = "FireTrucksRefillTest"

    override val map = "mapFiles/complexScenario1.dot"
    override val assets = "assetsJsons/fireTruckRefill_assets.json"
    override val scenario = "scenarioJsons/fireTruckRefill_simulation.json"
    override val maxTicks = 20
    override suspend fun run() {
        assertNextLine("Initialization Info: complexScenario1.dot successfully parsed and validated")
        assertNextLine("Initialization Info: fireTruckRefill_assets.json successfully parsed and validated")
        assertNextLine("Initialization Info: fireTruckRefill_simulation.json successfully parsed and validated")
        // start of simulation
        assertNextLine("Simulation starts")
        assertNextLine("Simulation Tick: 0")
        assertNextLine("Simulation Tick: 1")
        assertNextLine("Emergency Assignment: 0 assigned to 1")
        assertNextLine("Asset Allocation: 0 allocated to 0; 1 ticks to arrive.")
        assertNextLine("Asset Allocation: 1 allocated to 0; 1 ticks to arrive.")

        assertNextLine("Simulation Tick: 2")
        assertNextLine("Asset Arrival: 0 arrived at 3.")
        assertNextLine("Asset Arrival: 1 arrived at 3.")
        assertNextLine("Emergency Handling Start: 0 handling started.") // crime handling starts (needs 2 ticks)

        assertNextLine("Simulation Tick: 3")
        assertNextLine("Emergency Resolved: 0 resolved.") // crime resolved after 2 ticks from tick 3

        assertNextLine("Simulation Tick: 4")
        assertNextLine("Emergency Assignment: 1 assigned to 1")
        assertNextLine("Asset Arrival: 0 arrived at 1.")
        assertNextLine("Asset Arrival: 1 arrived at 1.")
        assertNextLine("Simulation Tick: 5")
        assertNextLine("Simulation Tick: 6")
        assertNextLine("Simulation Tick: 7")
        assertNextLine("Asset Allocation: 0 allocated to 1; 1 ticks to arrive.")
        assertNextLine("Asset Allocation: 1 allocated to 1; 1 ticks to arrive.")

        assertNextLine("Simulation Tick: 8")
        assertNextLine("Asset Arrival: 0 arrived at 3.")
        assertNextLine("Asset Arrival: 1 arrived at 3.")
        assertNextLine("Emergency Handling Start: 1 handling started.")

        assertNextLine("Simulation Tick: 9")
        assertNextLine("Emergency Resolved: 1 resolved.")

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
