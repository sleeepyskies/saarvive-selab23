package de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.map

import de.unisaarland.cs.se.selab.systemtest.api.SystemTest

class VillagesWithSameNameTest : SystemTest() {
    override val name = "VillagesWithSameNameTest"

    override val map = "mapFiles/villagesWithSameName.dot"
    override val assets = "assetsJsons/validBaseFileSmall.json"
    override val scenario = "scenarioJsons/validScenarioFileSmall.json"
    override suspend fun run() {
        assertNextLine("Initialization Info: villagesWithSameName.dot invalid")
    }

    override val maxTicks = 5
}
