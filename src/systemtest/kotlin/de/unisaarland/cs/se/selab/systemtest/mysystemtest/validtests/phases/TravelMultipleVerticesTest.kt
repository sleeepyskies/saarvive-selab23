package de.unisaarland.cs.se.selab.systemtest.mysystemtest.validtests.phases

import de.unisaarland.cs.se.selab.systemtest.api.SystemTest

class TravelMultipleVerticesTest : SystemTest() {
    override val name = "TravelMultipleVerticesTest"

    override val map = "mapFiles/travelMultipleVertices_map.dot"
    override val assets = "assetsJsons/travelMultipleVertices_assets.json"
    override val scenario = "scenarioJsons/travelMultipleVertices_scenario.json"
    override val maxTicks = 5

    override suspend fun run() {
        // Parsing + Validation + Construction
        assertNextLine("Initialization Info: travelMultipleVertices_map.dot successfully parsed and validated")
        assertNextLine("Initialization Info: travelMultipleVertices_assets.json successfully parsed and validated")
        assertNextLine("Initialization Info: travelMultipleVertices_scenario.json successfully parsed and validated")

        // Simulation Start
        assertNextLine("Simulation starts")

        // Tick 0
        assertNextLine("Simulation Tick: 0")

        // Tick 1
        assertNextLine("Simulation Tick: 1")
        assertNextLine("Emergency Assignment: 0 assigned to 0")
        assertNextLine("Asset Allocation: 0 allocated to 0; 2 ticks to arrive.")
        assertNextLine("Asset Allocation: 1 allocated to 0; 2 ticks to arrive.")

        // Tick 2
        assertNextLine("Simulation Tick: 2")

        // Tick 3
        assertNextLine("Simulation Tick: 3")
        assertNextLine("Asset Arrival: 0 arrived at 7.")
        assertNextLine("Asset Arrival: 1 arrived at 7.")
        assertNextLine("Emergency Handling Start: 0 handling started.")

        // Tick 4
        assertNextLine("Simulation Tick: 4")
        assertNextLine("Emergency Resolved: 0 resolved.")

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
