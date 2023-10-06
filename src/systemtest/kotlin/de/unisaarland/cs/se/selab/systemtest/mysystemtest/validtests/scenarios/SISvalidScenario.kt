package de.unisaarland.cs.se.selab.systemtest.mysystemtest.validtests.scenarios

import de.unisaarland.cs.se.selab.systemtest.api.SystemTest

class SISvalidScenario : SystemTest() {
    override val name = "SISvalidScenario"

    override val map = "mapFiles/SISvalidScenario_map.dot"
    override val assets = "assetsJsons/SISvalidScenario_bases.json"
    override val scenario = "scenarioJsons/SISvalidScenario_simulation.json"
    override val maxTicks = 30

    // this test tests on reallocation when a higher severity emergency occurs
    override suspend fun run() {
        assertNextLine("Initialization Info: SISvalidScenario_map.dot successfully parsed and validated")
        assertNextLine("Initialization Info: SISvalidScenario_bases.json successfully parsed and validated")
        assertNextLine("Initialization Info: SISvalidScenario_simulation.json successfully parsed and validated")

        assertNextLine("Simulation starts")
        assertNextLine("Simulation Tick: 0")
        assertNextLine("Event Triggered: 0 triggered.") // traffic jam happens

        assertNextLine("Simulation Tick: 1")
        assertNextLine("Event Ended: 0 ended.")

        assertNextLine("Simulation Tick: 2")

        assertNextLine("Simulation Tick: 3")
        assertNextLine("Emergency Assignment: 0 assigned to 2")
        assertNextLine("Asset Allocation: 7 allocated to 0; 4 ticks to arrive.")

        assertNextLine("Simulation Tick: 4")

        assertNextLine("Simulation Tick: 5")
        assertNextLine("Emergency Assignment: 1 assigned to 2")
        assertNextLine("Asset Allocation: 8 allocated to 1; 5 ticks to arrive.")
        assertNextLine("Asset Allocation: 9 allocated to 1; 5 ticks to arrive.")
        assertNextLine("Asset Reallocation: 7 reallocated to 1.") // 3 ticks

        assertNextLine("Simulation Tick: 6")
        assertNextLine("Simulation Tick: 7")

        assertNextLine("Simulation Tick: 8")
        assertNextLine("Asset Arrival: 7 arrived at 4.")

        assertNextLine("Simulation Tick: 9")

        assertNextLine("Simulation Tick: 10")
        assertNextLine("Asset Arrival: 8 arrived at 4.")
        assertNextLine("Asset Arrival: 9 arrived at 4.")
        assertNextLine("Emergency Handling Start: 1 handling started.")

        assertNextLine("Simulation Tick: 11")
        assertNextLine("Simulation Tick: 12")

        assertNextLine("Simulation Tick: 13")
        assertNextLine("Emergency Resolved: 1 resolved.") // medical sev2 resolved

        assertNextLine("Simulation Tick: 14") // vehicles go back hospital id2, this will take 5 ticks
        assertNextLine("Simulation Tick: 15")
        assertNextLine("Simulation Tick: 16")
        assertNextLine("Simulation Tick: 17")

        assertNextLine("Simulation Tick: 18")
        assertNextLine("Asset Arrival: 7 arrived at 2.") // Am1 back to base
        assertNextLine("Asset Arrival: 8 arrived at 2.") // Am2 back to base
        assertNextLine("Asset Arrival: 9 arrived at 2.") // EDC back to base

        assertNextLine("Simulation Tick: 19") // Am1 has to wait 1 tick to discharge patient
        assertNextLine("Asset Allocation: 7 allocated to 0; 4 ticks to arrive.")

        assertNextLine("Simulation Tick: 20")
        assertNextLine("Simulation Tick: 21")
        assertNextLine("Simulation Tick: 22")
        assertNextLine("Simulation Tick: 23")
        assertNextLine("Asset Arrival: 7 arrived at 3.")
        assertNextLine("Emergency Handling Start: 0 handling started.") // med sev1 handling starts, takes 2Ticks

        assertNextLine("Simulation Tick: 24")
        assertNextLine("Simulation Tick: 25")
        assertNextLine("Emergency Resolved: 0 resolved.") // medical sev1 resolved
        assertEndOfSimulation() // to fix detekt too long
    }

    private suspend fun assertEndOfSimulation() {
        assertNextLine("Simulation End")
        assertNextLine("Simulation Statistics: 0 assets rerouted.")
        assertNextLine("Simulation Statistics: 2 received emergencies.")
        assertNextLine("Simulation Statistics: 0 ongoing emergencies.")
        assertNextLine("Simulation Statistics: 0 failed emergencies.")
        assertNextLine("Simulation Statistics: 2 resolved emergencies.")
        assertEnd()
    }
}
