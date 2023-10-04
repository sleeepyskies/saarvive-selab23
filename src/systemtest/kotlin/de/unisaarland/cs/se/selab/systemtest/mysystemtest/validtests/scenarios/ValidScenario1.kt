package de.unisaarland.cs.se.selab.systemtest.mysystemtest.validtests.scenarios

import de.unisaarland.cs.se.selab.systemtest.api.SystemTest

class ValidScenario1 : SystemTest() {
    override val name = "ValidScenario1"

    override val map = "mapFiles/validScenario1_map.dot"
    override val assets = "assetsJsons/validScenario1_bases.json"
    override val scenario = "scenarioJsons/validScenario_simulation.json"
    override val maxTicks = 10

    override suspend fun run() {
        assertNextLine("Initialization Info: validScenario1_map.dot successfully parsed and validated")
        assertNextLine("Initialization Info: validScenario1_bases.json successfully parsed and validated")
        assertNextLine("Initialization Info: validScenario_simulation.json successfully parsed and validated")
        // start of simulation
        assertNextLine("Simulation started")
        assertNextLine("Simulation Tick: 0")
        assertNextLine("Event Triggered: 0 triggered.") // should affect the road calculations
        assertNextLine("Simulation Tick: 1")
        // emergency CRIME, severity 1 starts, id -> 0
        assertNextLine("Emergency Assignment: 0 assigned to 3")
        // needed assets: 1 Police Car, 1 Criminal
        assertNextLine("Asset Allocation: 9 allocated to 0; 0 ticks to arrive.")
        assertNextLine("Asset Arrival: 9 arrived at 3.")
        assertNextLine("Emergency Handling Start: 0 handling started")
        assertNextLine("Simulation Tick: 2")
        assertNextLine("Simulation Tick: 3")
        assertNextLine("Emergency Assignment: 1 assigned to 2")
        assertNextLine("Asset Allocation: 7 allocated to 1; 1 ticks to arrive.")
        assertNextLine("Emergency Resolved: 0 resolved.")
        assertNextLine("Simulation Tick: 4")
        assertNextLine("Asset Arrival: 7 arrived at 4.")
        assertNextLine("Emergency Handling Start: 1 handling started")
        assertNextLine("Simulation Tick: 5")
        assertNextLine("Event Ended: 0 ended.")
        assertNextLine("Simulation Tick: 6")
        assertNextLine("Emergency Resolved: 1 resolved.")
        assertNextLine("Simulation Tick: 7")
        assertNextLine("Simulation Tick: 8")
        assertNextLine("Simulation Tick: 9")
        assertNextLine("Simulation Tick: 10")
        // end of simulation
        assertNextLine("Simulation End")
        // Statistics
        assertNextLine("Simulation Statistics: 0 assets rerouted.")
        assertNextLine("Simulation Statistics: 2 received emergencies.")
        assertNextLine("Simulation Statistics: 0 ongoing emergencies.")
        assertNextLine("Simulation Statistics: 0 failed emergencies.")
        assertNextLine("Simulation Statistics: 2 resolved emergencies.")
        // end of file is reached
        assertEnd()
    }
}
