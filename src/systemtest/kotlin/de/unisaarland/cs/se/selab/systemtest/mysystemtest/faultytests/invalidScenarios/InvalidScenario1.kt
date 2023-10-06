package de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.invalidScenarios

import de.unisaarland.cs.se.selab.systemtest.api.SystemTest

class InvalidScenario1 : SystemTest() {
    override val name = "ValidScenario1"
    override val map = "mapFiles/invalidScenario1_map.dot"
    override val assets = "assetsJsons/invalidScenario1_assets.json"
    override val scenario = "scenarioJsons/invalidScenario1_simulation.json"
    override val maxTicks = 50

    override suspend fun run() {
        assertNextLine("Initialization Info: invalidScenario1_map.dot successfully parsed and validated")
        assertNextLine("Initialization Info: invalidScenario1_assets.json successfully parsed and validated")
        assertNextLine("Initialization Info: invalidScenario1_simulation.json successfully parsed and validated")
        assertNextLine("Simulation starts")
        assertNextLine("Simulation Tick: 0")
        assertNextLine("Simulation Tick: 1")
        assertNextLine("Simulation Tick: 2")
        assertNextLine("Event Triggered: 99 triggered.")
        assertNextLine("Simulation Tick: 3")
        assertNextLine("Simulation Tick: 4")
        assertNextLine("Simulation Tick: 5")
        assertNextLine("Simulation Tick: 6")
        assertNextLine("Event Ended: 99 ended.")
        assertNextLine("Simulation Tick: 7")
        assertNextLine("Simulation Tick: 8")
        assertNextLine("Simulation Tick: 9")
        assertNextLine("Emergency Assignment: 1 assigned to 1")
        assertNextLine("Asset Allocation: 666 allocated to 1; 2 ticks to arrive.")
        assertNextLine("Simulation Tick: 10")
        assertNextLine("Simulation Tick: 11")
        assertNextLine("Asset arrival: 666 arrived at 3.")
        assertNextLine("Emergency Handling Start: 1 handling started.")
        assertNextLine("Simulation Tick: 12")
        assertNextLine("Simulation Tick: 13")
        assertNextLine("Simulation Tick: 14")
        assertNextLine("Simulation Tick: 15")
        assertNextLine("Simulation Tick: 16")
        assertNextLine("Simulation Tick: 17")
        assertNextLine("Simulation Tick: 18")
        assertNextLine("Emergency Resolved: 1 resolved.")
        assertNextLine("Simulation End")
        assertNextLine("Simulation Statistics: 0 assets rerouted.")
        assertNextLine("Simulation Statistics: 1 received emergencies.")
        assertNextLine("Simulation Statistics: 0 ongoing emergencies.")
        assertNextLine("Simulation Statistics: 0 failed emergencies.")
        assertNextLine("Simulation Statistics: 1 resolved emergencies.")
    }
}
