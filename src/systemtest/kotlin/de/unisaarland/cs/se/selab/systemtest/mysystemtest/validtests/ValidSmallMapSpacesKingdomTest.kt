package de.unisaarland.cs.se.selab.systemtest.mysystemtest.validtests

import de.unisaarland.cs.se.selab.systemtest.api.SystemTest

class ValidSmallMapSpacesKingdomTest : SystemTest() {
    override val name = "ValidSmallMapSpacesKingdomTest"

    override val map = "mapFiles/validSmallMapSpacesKingdom.dot"
    override val assets = "assetsJsons/validBaseFileSmall.json"
    override val scenario = "scenarioJsons/validScenarioFileSmall.json"
    override suspend fun run() {
        assertNextLine("Initialization Info: validSmallMapSpacesKingdom.dot successfully parsed and validated")
    }

    override val maxTicks = 5
}
