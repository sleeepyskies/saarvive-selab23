package de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.map

import de.unisaarland.cs.se.selab.systemtest.api.SystemTest

class MissingTerminalTest : SystemTest() {
    override val name = "MissingTerminalTest"

    override val map = "mapFiles/missingTerminal.dot"
    override val assets = "assetsJsons/validBaseFileSmall.json"
    override val scenario = "scenarioJsons/validScenarioFileSmall.json"
    override suspend fun run() {
        assertNextLine("Initialization Info: missingTerminal.dot invalid")
    }

    override val maxTicks = 5
}
