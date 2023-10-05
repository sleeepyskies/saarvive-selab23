package de.unisaarland.cs.se.selab.systemtest.mysystemtest.validtests.scenarios

import de.unisaarland.cs.se.selab.systemtest.api.SystemTest

class ComplexScenario1Test : SystemTest() {
    override val name = "ComplexScenario1Test"

    override val map = "mapFiles/complexScenario1.dot"
    override val assets = "assetsJsons/complexScenario1.json"
    override val scenario = "scenarioJsons/complexScenario1_simulation.json"
    override val maxTicks = 50

    override suspend fun run() {
        assertNextLine("Initialization Info: complexScenario1.dot successfully parsed and validated")
        assertNextLine("Initialization Info: complexScenario1.json successfully parsed and validated")
        assertNextLine("Initialization Info: complexScenario1_simulation.json successfully parsed and validated")
        // start of simulation
        assertNextLine("Simulation starts")
        assertNextLine("Simulation Tick: 0")

        // end of simulation
        assertNextLine("Simulation End")
        // Statistics
        assertNextLine("Simulation Statistics:  assets rerouted.")
        assertNextLine("Simulation Statistics:  received emergencies.")
        assertNextLine("Simulation Statistics:  ongoing emergencies.")
        assertNextLine("Simulation Statistics:  failed emergencies.")
        assertNextLine("Simulation Statistics:  resolved emergencies.")
        // end of file is reached
        assertEnd()
    }
}
