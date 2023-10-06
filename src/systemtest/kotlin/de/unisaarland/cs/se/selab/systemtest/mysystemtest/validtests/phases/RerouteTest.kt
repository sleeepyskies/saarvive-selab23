package de.unisaarland.cs.se.selab.systemtest.mysystemtest.validtests.phases

import de.unisaarland.cs.se.selab.systemtest.api.SystemTest

class RerouteTest : SystemTest() {
    override val name = "RerouteTest"

    override val map = "mapFiles/decentGraphSimTests.dot"
    override val assets = "assetsJsons/simpleAssets_detailedGraph.json"
    override val scenario = "scenarioJsons/rerouteTest_scenario.json"
    override val maxTicks = 20

    override suspend fun run() {
        // Parsing + Validation + Construction
        assertNextLine("Initialization Info: decentGraphSimTests.dot successfully parsed and validated")
        assertNextLine("Initialization Info: simpleAssets_detailedGraph.json successfully parsed and validated")
        assertNextLine("Initialization Info: oneFireEmergency_scenario.json successfully parsed and validated")

        // Simulation Start
        assertNextLine("Simulation starts")

        // Tick 0
        assertNextLine("Simulation Tick: 0")

        // Tick 1
        assertNextLine("Simulation Tick: 1")
        assertNextLine("Emergency Assignment: 0 assigned to 3")
        assertNextLine("Asset Allocation: 5 allocated to 0; 3 ticks to arrive.")

        // Tick 2
        assertNextLine("Simulation Tick: 2")
        assertNextLine("Event Triggered: 0 triggered.")
        assertNextLine("Assets Rerouted: 1")

        // Tick 3
        assertNextLine("Simulation Tick: 3")

        // Tick 4
        assertNextLine("Simulation Tick: 4")

        // Tick 5
        assertNextLine("Simulation Tick: 5")

        // Tick 6
        assertNextLine("Simulation Tick: 6")

        // Tick 7
        assertNextLine("Simulation Tick: 7")
        assertNextLine("Asset Arrival: 5 arrived at 9.")
        assertNextLine("Emergency Handling Start: 0 handling started.")

        // Tick 8
        assertNextLine("Simulation Tick: 8")
        assertNextLine("Emergency Resolved: 0 resolved.")

        // Simulation End
        assertNextLine("Simulation End")

        assertNextLine("Simulation Statistics: 0 assets rerouted.")
        assertNextLine("Simulation Statistics: 1 received emergencies.")
        assertNextLine("Simulation Statistics: 0 ongoing emergencies.")
        assertNextLine("Simulation Statistics: 0 failed emergencies.")
        assertNextLine("Simulation Statistics: 0 resolved emergencies.")
        // end of file is reached
        assertEnd()
    }
}
