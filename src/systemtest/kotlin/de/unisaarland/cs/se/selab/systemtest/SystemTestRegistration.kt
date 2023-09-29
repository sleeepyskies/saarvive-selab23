package de.unisaarland.cs.se.selab.systemtest

import de.unisaarland.cs.se.selab.systemtest.basictests.ExampleTest
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.NonExistentVertexTest
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.TwoRoadsSameLocationTest
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.TwoRoadsSameNameTest
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.ZeroHeightTest
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.validtests.CorrectSystemTestSmall
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.validtests.EmergencyFailTest
import de.unisaarland.cs.se.selab.systemtest.runner.SystemTestManager

object SystemTestRegistration {
    fun registerSystemTests(manager: SystemTestManager) {
        manager.registerTest(ExampleTest())
        manager.registerTest(CorrectSystemTestSmall())
        manager.registerTest(EmergencyFailTest())
        manager.registerTest(TwoRoadsSameLocationTest())
        manager.registerTest(TwoRoadsSameNameTest())
        manager.registerTest(NonExistentVertexTest())
        manager.registerTest(ZeroHeightTest())
    }
}
