package de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.map

import de.unisaarland.cs.se.selab.systemtest.api.SystemTest

class HasOnlyTwoVerticesTest : SystemTest() {
    override val name = "HasOnlyTwoVerticesTest"

    override val map = "mapFiles/hasOnlyTwoVertices.dot"
    override val assets = "assetsJsons/validBaseFileSmall.json"
    override val scenario = "scenarioJsons/validScenarioFileSmall.json"
    override suspend fun run() {
        assertNextLine("Initialization Info: hasOnlyTwoVertices.dot invalid")
    }

    override val maxTicks = 5
}
