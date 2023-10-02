package de.unisaarland.cs.se.selab.systemtest

import de.unisaarland.cs.se.selab.systemtest.basictests.ExampleTest
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.map.NegativeVertexTest
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.map.NonExistentVertexTest
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.map.TwoRoadsSameLocationTest
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.map.TwoRoadsSameNameTest
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.map.ZeroHeightTest
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.validtests.CorrectSystemTestSmall
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.validtests.EmergencyFailTest
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.validtests.ReallocationTest
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.validtests.RequestTest
import de.unisaarland.cs.se.selab.systemtest.runner.SystemTestManager

object SystemTestRegistration {
    fun registerSystemTests(manager: SystemTestManager) {
        // Example Test
        manager.registerTest(ExampleTest())
        // Valid Tests
        manager.registerTest(CorrectSystemTestSmall())
        manager.registerTest(EmergencyFailTest())
        manager.registerTest(RequestTest())
        manager.registerTest(ReallocationTest())
        // Faulty Tests
        manager.registerTest(TwoRoadsSameLocationTest())
        manager.registerTest(TwoRoadsSameNameTest())
        manager.registerTest(NonExistentVertexTest())
        manager.registerTest(ZeroHeightTest())
        manager.registerTest(NegativeVertexTest())
    }
}
