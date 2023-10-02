package de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests

import de.unisaarland.cs.se.selab.systemtest.api.SystemTest

class NoSideStreetTest : SystemTest() {
    override val name = "NoSideStreetTest"

    override val map = "mapFiles/noSideStreet.dot"
    override val assets = "assetsJsons/validBaseFileSmall.json"
    override val scenario = "scenarioJsons/validScenarioFileSmall.json"
    override suspend fun run() {
        assertNextLine("Initialization Info: noSideStreet.dot invalid")
    }

    override val maxTicks = 5
}
