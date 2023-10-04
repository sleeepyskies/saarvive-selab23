package de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.map

import de.unisaarland.cs.se.selab.systemtest.api.SystemTest

class WrongNumericIDTest : SystemTest() {
    override val name = "NumericIDWrong"

    override val map = "mapFiles/wrongNumericID.dot"
    override val assets = "assetsJsons/validBaseFileSmall.json"
    override val scenario = "scenarioJsons/validScenarioFileSmall.json"
    override suspend fun run() {
        assertNextLine("Initialization Info: wrongNumericID.dot invalid")
    }

    override val maxTicks = 5
}
