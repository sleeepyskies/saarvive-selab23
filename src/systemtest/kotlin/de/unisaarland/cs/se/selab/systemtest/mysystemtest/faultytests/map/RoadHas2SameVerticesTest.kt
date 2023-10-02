package de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.map

import de.unisaarland.cs.se.selab.systemtest.api.SystemTest

class RoadHas2SameVerticesTest : SystemTest() {
    override val name = "RoadHas2SameVerticesTest"

    override val map = "mapFiles/roadHas2sameVertex.dot"
    override val assets = "assetsJsons/validBaseFileSmall.json"
    override val scenario = "scenarioJsons/validScenarioFileSmall.json"
    override suspend fun run() {
        assertNextLine("Initialization Info: roadHas2sameVertex.dot invalid")
    }

    override val maxTicks = 5
}
