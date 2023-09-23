package de.unisaarland.cs.se.selab.systemtest

import de.unisaarland.cs.se.selab.systemtest.basictests.ExampleTest
import de.unisaarland.cs.se.selab.systemtest.runner.SystemTestManager

object SystemTestRegistration {
    fun registerSystemTests(manager: SystemTestManager) {
        manager.registerTest(ExampleTest())
    }
}
