package de.unisaarland.cs.se.selab.systemtest.mysystemtest.validtests.scenarios

import de.unisaarland.cs.se.selab.systemtest.api.SystemTest

class ValidScenario2 : SystemTest() {
    override val name = "ValidScenario2"
    override val map = "mapFiles/decentGraphSimTests.dot"
    override val assets = "assetsJsons/validScenario2_assets.json"
    override val scenario = "scenarioJsons/validScenario2_simulation.json"
    override val maxTicks = 50

    override suspend fun run() {
        assertNextLine("Initialization Info: decentGraphSimTests.dot successfully parsed and validated")
        assertNextLine("Initialization Info: validScenario2_assets.json successfully parsed and validated")
        assertNextLine("Initialization Info: validScenario2_simulation.json successfully parsed and validated")
        // start of simulation
        assertNextLine("Simulation starts")
        assertNextLine("Simulation Tick: 0")
        assertNextLine("Simulation Tick: 1")
        // start emergency 1 -> MEDICAL severity 1
        assertNextLine("Emergency Assignment: 1 assigned to 0") // lasts for 15 ticks, handling time 5
        // needed assets: 1 Ambulance
        assertNextLine("Asset Allocation: 0 allocated to 1; 3 ticks to arrive.") // vehicles send, travel for 3 ticks
        assertNextLine("Simulation Tick: 2")
        assertNextLine("Simulation Tick: 3")
        // Start emergency 2 -> FIRE severity 3
        assertNextLine("Emergency Assignment: 0 assigned to 2") // lasts for 25 ticks, handling time 10
        assertNextLine("Asset Allocation: 6 allocated to 0; 3 ticks to arrive.")
        assertNextLine("Asset Allocation: 7 allocated to 0; 3 ticks to arrive.")
        assertNextLine("Asset Allocation: 8 allocated to 0; 3 ticks to arrive.")
        assertNextLine("Asset Allocation: 9 allocated to 0; 3 ticks to arrive.")
        assertNextLine("Asset Allocation: 10 allocated to 0; 3 ticks to arrive.")
        assertNextLine("Asset Allocation: 11 allocated to 0; 3 ticks to arrive.") // 6 firetruck with water -> 6k total
        assertNextLine("Asset Allocation: 12 allocated to 0; 3 ticks to arrive.")
        assertNextLine("Asset Allocation: 13 allocated to 0; 3 ticks to arrive.") // 2 fire truck ladder -> 40m each
        assertNextLine("Asset Allocation: 15 allocated to 0; 3 ticks to arrive.")
        assertNextLine("Asset Allocation: 16 allocated to 0; 3 ticks to arrive.") // 2 firefighter transporter
        assertNextLine("Asset Request: 0 sent to 1 for 0.")
        assertNextLine("Asset Allocation: 2 allocated to 0; 6 ticks to arrive.")
        assertNextLine("Asset Request: 1 sent to 0 for 0.")
        assertNextLine("Asset Allocation: 1 allocated to 0; 1 ticks to arrive.")
        assertNextLine("Asset Allocation: 18 allocated to 0; 1 ticks to arrive.")
        assertNextLine("Event Triggered: 0 triggered.") // rush hour affects the ambulance 0 for event 1
        assertNextLine("Assets Rerouted: 1") //
        assertNextLine("Simulation Tick: 4")
        assertNextLine("Asset Arrival: 0 arrived at 9.")
        assertNextLine("Asset Arrival: 1 arrived at 8.")
        assertNextLine("Asset Arrival: 18 arrived at 8.")
        assertNextLine("Emergency Handling Start: 1 handling started.")
        assertNextLine("Event Triggered: 1 triggered.")
        assertNextLine("Assets Rerouted: 10") // all the fire, ambulance and 1 extra ambulance
        assertNextLine("Simulation Tick: 5")
        assertNextLine("Simulation Tick: 6")
        assertNextLine("Asset Arrival: 6 arrived at 6.")
        assertNextLine("Asset Arrival: 7 arrived at 6.")
        assertNextLine("Asset Arrival: 8 arrived at 6.")
        assertNextLine("Asset Arrival: 9 arrived at 6.")
        assertNextLine("Asset Arrival: 10 arrived at 6.")
        assertNextLine("Asset Arrival: 11 arrived at 6.")
        assertNextLine("Asset Arrival: 12 arrived at 6.")
        assertNextLine("Asset Arrival: 13 arrived at 6.")
        assertNextLine("Asset Arrival: 15 arrived at 6.")
        assertNextLine("Asset Arrival: 16 arrived at 6.")
        assertNextLine("Event Ended: 1 ended.") // road closure event
        // vehicles arrive at tick 6
        assertNextLine("Simulation Tick: 7")
        assertNextLine("Simulation Tick: 8")
        assertNextLine("Event Ended: 0 ended.") // rush hour event
        assertNextLine("Assets Rerouted: 12") // more are rerouted
        assertNextLine("Simulation Tick: 9")
        assertNextLine("Asset Arrival: 2 arrived at 6.")
        assertNextLine("Emergency Resolved: 1 resolved.")
        assertNextLine("Emergency Handling Start: 0 handling started.")
        assertNextLine("Simulation Tick: 10")
        assertNextLine("Simulation Tick: 11")
        assertNextLine("Simulation Tick: 12")
        assertNextLine("Asset Arrival: 0 arrived at 5.")
        assertNextLine("Simulation Tick: 13")
        assertNextLine("Simulation Tick: 14")
        assertNextLine("Simulation Tick: 15")
        assertNextLine("Simulation Tick: 16")
        assertNextLine("Simulation Tick: 17")
        assertNextLine("Simulation Tick: 18")
        assertNextLine("Simulation Tick: 19")
        assertNextLine("Emergency Resolved: 1 resolved.")
        assertNextLine("Simulation End")
        // Statistics
        assertNextLine("Simulation Statistics: 0 assets rerouted.")
        assertNextLine("Simulation Statistics: 2 received emergencies.")
        assertNextLine("Simulation Statistics: 0 ongoing emergencies.")
        assertNextLine("Simulation Statistics: 0 failed emergencies.")
        assertNextLine("Simulation Statistics: 2 resolved emergencies.")
        // end of file is reached
        assertEnd()
        // emergency 1 ends
    }
}
