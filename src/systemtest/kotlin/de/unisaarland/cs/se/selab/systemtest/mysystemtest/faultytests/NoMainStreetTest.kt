package de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests
import de.unisaarland.cs.se.selab.systemtest.api.SystemTest

class NoMainStreetTest : SystemTest() {
    override val name = "NoMainStreetTest"

    override val map = "mapFiles/noMainStreet.dot"
    override val assets = "assetsJsons/validBaseFileSmall.json"
    override val scenario = "scenarioJsons/validScenarioFileSmall.json"
    override val maxTicks = 3
    override suspend fun run() {
        assertNextLine("Initialization Info: noMainStreet.dot invalid")
    }
}
