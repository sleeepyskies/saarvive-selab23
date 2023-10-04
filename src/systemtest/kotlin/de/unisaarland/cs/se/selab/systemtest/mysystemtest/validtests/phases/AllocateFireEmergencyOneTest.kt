package de.unisaarland.cs.se.selab.systemtest.mysystemtest.validtests.phases

import de.unisaarland.cs.se.selab.systemtest.api.SystemTest

class AllocateFireEmergencyOneTest : SystemTest() {
    override val name = "AllocateFireEmergencyOneTest"

    override val map = "mapFiles/decentGraphSimTests.dot"
    override val assets = "assetsJsons/simpleAssets_detailedGraph.json"
    override val scenario = "scenarioJsons/oneFireEmergency_scenario.json"
    override val maxTicks = 5

    override suspend fun run() {
        // Parsing + Validation + Construction
        assertNextLine("Initialization Info: correctEdgesToVertices.dot successfully parsed and validated")
        assertNextLine("Initialization Info: simpleAssets_detailedGraph.json successfully parsed and validated")
        assertNextLine("Initialization Info: oneFireEmergency_scenario.json successfully parsed and validated")

        // Simulation Start
        assertNextLine("Simulation starts")

        // Tick 1
        assertNextLine("Simulation Tick: 0")
    }
}
