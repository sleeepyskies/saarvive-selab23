package de.unisaarland.cs.se.selab.systemtest.mysystemtest.validtests.phases

import de.unisaarland.cs.se.selab.systemtest.api.SystemTest

class AllocateFireEmergencyOneTest2 : SystemTest() {
    override val name = "AllocateFireEmergencyOneTest2"

    override val map = "mapFiles/decentGraphSimTests.dot"
    override val assets = "assetsJsons/simpleAssets_detailedGraph.json"
    override val scenario = "scenarioJsons/oneFireEmergency_scenario.json"
    override val maxTicks = 3

    override suspend fun run() {
        // Parsing + Validation + Construction
        assertNextLine("Initialization Info: decentGraphSimTests.dot successfully parsed and validated")
        assertNextLine("Initialization Info: simpleAssets_detailedGraph.json successfully parsed and validated")
        assertNextLine("Initialization Info: oneFireEmergency_scenario.json successfully parsed and validated")

        // Simulation Start
        assertNextLine("Simulation starts")

        // Tick 0
        assertNextLine("Simulation Tick: 0")
        assertNextLine("Event Triggered: 0 triggered.")

        // Tick 1
        assertNextLine("Simulation Tick: 1")
        assertNextLine("Emergency Assignment: 0 assigned to 0")
        assertNextLine("Asset Allocation: 0 allocated to 0; 1 ticks to arrive")
        assertNextLine("Asset Allocation: 1 allocated to 0; 1 ticks to arrive")
        // can assets arrive at an emergency the same tick they are allocated? for 1 tick arrival time
        // or do they move, then arrive next tick
        assertNextLine("Asset Arrival: 0 arrived at 1.")
        assertNextLine("Asset Arrival: 1 arrived at 1.")
        assertNextLine("Emergency Handling Start: 0 handling started.")

        // Tick 2
        assertNextLine("Simulation Tick: 2")
        assertNextLine("Emergency Resolved: 0 resolved.")
        assertNextLine("Event Ended: 0 ended.")

        // Simulation End
        assertNextLine("Simulation End")

        assertNextLine("Simulation Statistics: 0 assets rerouted.")
        assertNextLine("Simulation Statistics: 1 received emergencies.")
        assertNextLine("Simulation Statistics: 0 ongoing emergencies.")
        assertNextLine("Simulation Statistics: 0 failed emergencies.")
        assertNextLine("Simulation Statistics: 1 resolved emergencies.")
        // end of file is reached
        assertEnd()
    }
}
