package de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.map

import de.unisaarland.cs.se.selab.systemtest.api.SystemTest

class SplittedNameTest : SystemTest() {
    override val name = "SplittedNameTest"

    override val map = "mapFiles/splittedName.dot"
    override val assets = "assetsJsons/validBaseFileSmall.json"
    override val scenario = "scenarioJsons/validScenarioFileSmall.json"
    override suspend fun run() {
        assertNextLine("Initialization Info: splittedName.dot invalid")
    }

    override val maxTicks = 5
}
