package de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.simulation

import de.unisaarland.cs.se.selab.systemtest.api.SystemTest

class WhyThisEmergencySoWrong : SystemTest() {
    override val name = "WhyThisEmergencySoWrong"
    override val map = "mapFiles/invalidScenario1_map.dot"
    override val assets = "assetsJsons/invalidScenario1_assets.json"
    override val scenario = "scenarioJsons/whyThisEmergencySoWrong_simulation.json"
    override val maxTicks = 14

    override suspend fun run() {
        assertNextLine("Initialization Info: invalidScenario1_map.dot successfully parsed and validated")
        assertNextLine("Initialization Info: invalidScenario1_assets.json successfully parsed and validated")
        assertNextLine("Initialization Info: whyThisEmergencySoWrong_simulation.json invalid")
    }
}
