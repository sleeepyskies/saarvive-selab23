package de.unisaarland.cs.se.selab.systemtest

import de.unisaarland.cs.se.selab.systemtest.basictests.CorrectSystemTestSmall
import de.unisaarland.cs.se.selab.systemtest.basictests.EmergencyFailTest
import de.unisaarland.cs.se.selab.systemtest.basictests.ExampleTest
import de.unisaarland.cs.se.selab.systemtest.runner.SystemTestManager

object SystemTestRegistration {
    fun registerSystemTests(manager: SystemTestManager) {
        manager.registerTest(ExampleTest())
        manager.registerTest(CorrectSystemTestSmall())
        manager.registerTest(EmergencyFailTest())
    }
}
