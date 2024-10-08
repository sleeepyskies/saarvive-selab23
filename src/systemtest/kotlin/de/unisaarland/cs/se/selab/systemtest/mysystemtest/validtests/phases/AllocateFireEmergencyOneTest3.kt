package de.unisaarland.cs.se.selab.systemtest.mysystemtest.validtests.phases

import de.unisaarland.cs.se.selab.systemtest.api.SystemTest

class AllocateFireEmergencyOneTest3 : SystemTest() {
    override val name = "AllocateFireEmergencyOneTest3"

    override val map = "mapFiles/decentGraphSimTests.dot"
    override val assets = "assetsJsons/simpleAssets_detailedGraph2.json"
    override val scenario = "scenarioJsons/oneFireEmergency_scenario.json"
    override val maxTicks = 5
    private val requestFail = "Request Failed: 0 failed."

    override suspend fun run() {
        // Parsing + Validation + Construction
        assertNextLine("Initialization Info: decentGraphSimTests.dot successfully parsed and validated")
        assertNextLine("Initialization Info: simpleAssets_detailedGraph2.json successfully parsed and validated")
        assertNextLine("Initialization Info: oneFireEmergency_scenario.json successfully parsed and validated")

        // Simulation Start
        assertNextLine("Simulation starts")

        // Tick 0
        assertNextLine("Simulation Tick: 0")

        // Tick 1
        assertNextLine("Simulation Tick: 1")
        assertNextLine("Emergency Assignment: 0 assigned to 0")
        assertNextLine("Asset Allocation: 0 allocated to 0; 1 ticks to arrive.")
        assertNextLine("Asset Request: 1 sent to 1 for 0.")
        assertNextLine(requestFail)

        // Tick 2
        assertNextLine("Simulation Tick: 2")
        assertNextLine("Asset Request: 2 sent to 1 for 0.")
        assertNextLine(requestFail)
        assertNextLine("Asset Arrival: 0 arrived at 1.")
        assertNextLine("Emergency Failed: 0 failed.")

        // Simulation End
        assertNextLine("Simulation End")

        assertNextLine("Simulation Statistics: 0 assets rerouted.")
        assertNextLine("Simulation Statistics: 1 received emergencies.")
        assertNextLine("Simulation Statistics: 0 ongoing emergencies.")
        assertNextLine("Simulation Statistics: 1 failed emergencies.")
        assertNextLine("Simulation Statistics: 0 resolved emergencies.")
        // end of file is reached
        assertEnd()
    }
}
