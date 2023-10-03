package de.unisaarland.cs.se.selab.systemtest

import de.unisaarland.cs.se.selab.systemtest.basictests.ExampleTest
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.map.NegativeVertexTest
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.map.NoMainStreetTest
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.map.NoSideStreetTest
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.map.NonExistentVertexTest
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.map.NonUniqueVertexTest
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.map.RoadHas2SameVerticesTest
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.map.SameVertexDifferentVillagesTest
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.map.TwoRoadsSameLocationTest
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.map.TwoRoadsSameNameTest
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.map.VertexWithoutRoadConnectionTest
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.map.VillageAndCountyNameSameTest
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.map.WeightIsZeroTest
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.map.WrongTunnelHeightTest
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
        // Faulty Map Tests
        manager.registerTest(TwoRoadsSameLocationTest())
        manager.registerTest(TwoRoadsSameNameTest())
        manager.registerTest(NonExistentVertexTest())
        manager.registerTest(ZeroHeightTest())
        manager.registerTest(NegativeVertexTest())
        manager.registerTest(NoMainStreetTest())
        manager.registerTest(NonUniqueVertexTest())
        manager.registerTest(NoSideStreetTest())
        manager.registerTest(RoadHas2SameVerticesTest())
        manager.registerTest(SameVertexDifferentVillagesTest())
        manager.registerTest(VertexWithoutRoadConnectionTest())
        manager.registerTest(VillageAndCountyNameSameTest())
        manager.registerTest(WeightIsZeroTest())
        manager.registerTest(WrongTunnelHeightTest())
    }

    fun registerSystemTestsReferenceImpl(manager: SystemTestManager) {
        manager.registerTest(ExampleTest())
        // Valid Tests
        manager.registerTest(TwoRoadsSameLocationTest())
        manager.registerTest(TwoRoadsSameNameTest())
        manager.registerTest(NonExistentVertexTest())
        manager.registerTest(ZeroHeightTest())
        manager.registerTest(NegativeVertexTest())
        manager.registerTest(NoMainStreetTest())
        manager.registerTest(NonUniqueVertexTest())
        manager.registerTest(NoSideStreetTest())
        manager.registerTest(RoadHas2SameVerticesTest())
        manager.registerTest(SameVertexDifferentVillagesTest())
        manager.registerTest(VertexWithoutRoadConnectionTest())
        manager.registerTest(VillageAndCountyNameSameTest())
        manager.registerTest(WeightIsZeroTest())
        manager.registerTest(WrongTunnelHeightTest())
    }

    fun registerSystemTestsMutantValidation(manager: SystemTestManager) {
        manager.registerTest(TwoRoadsSameLocationTest())
        manager.registerTest(TwoRoadsSameNameTest())
        manager.registerTest(NonExistentVertexTest())
        manager.registerTest(ZeroHeightTest())
        manager.registerTest(NegativeVertexTest())
        manager.registerTest(NoMainStreetTest())
        manager.registerTest(NonUniqueVertexTest())
        manager.registerTest(NoSideStreetTest())
        manager.registerTest(RoadHas2SameVerticesTest())
        manager.registerTest(SameVertexDifferentVillagesTest())
        manager.registerTest(VertexWithoutRoadConnectionTest())
        manager.registerTest(VillageAndCountyNameSameTest())
        manager.registerTest(WeightIsZeroTest())
        manager.registerTest(WrongTunnelHeightTest())
    }

    fun registerSystemTestsMutantSimulation(manager: SystemTestManager) {
        manager.registerTest(VertexWithoutRoadConnectionTest())
        manager.registerTest(VillageAndCountyNameSameTest())
    }
}
