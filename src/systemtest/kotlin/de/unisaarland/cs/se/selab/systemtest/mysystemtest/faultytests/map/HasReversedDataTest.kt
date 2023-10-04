package de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.map

import de.unisaarland.cs.se.selab.systemtest.api.SystemTest

class HasReversedDataTest : SystemTest() {
    override val name = "HasReversedDataTest"

    override val map = "mapFiles/hasReversedData.dot"
    override val assets = "assetsJsons/validBaseFileSmall.json"
    override val scenario = "scenarioJsons/validScenarioFileSmall.json"
    override suspend fun run() {
        assertNextLine("Initialization Info: hasReversedData.dot invalid")
    }

    override val maxTicks = 5
}
