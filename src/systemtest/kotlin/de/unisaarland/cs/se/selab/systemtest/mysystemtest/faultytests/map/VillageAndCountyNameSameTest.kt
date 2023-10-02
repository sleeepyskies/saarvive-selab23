package de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.map

import de.unisaarland.cs.se.selab.systemtest.api.SystemTest

class VillageAndCountyNameSameTest : SystemTest() {
    override val name = "VillageAndCountyNameSameTest"

    override val map = "mapFiles/villageAndCountyNameSame.dot"
    override val assets = "assetsJsons/validBaseFileSmall.json"
    override val scenario = "scenarioJsons/validScenarioFileSmall.json"
    override suspend fun run() {
        assertNextLine("Initialization Info: villageAndCountyNameSame.dot invalid")
    }

    override val maxTicks = 5
}
