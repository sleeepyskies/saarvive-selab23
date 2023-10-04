package de.unisaarland.cs.se.selab.systemtest

import de.unisaarland.cs.se.selab.systemtest.basictests.ExampleTest
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.assets.BaseOnNothing
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.assets.NoVehiclesInBase
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.assets.NotEnoughBases
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.assets.SameBaseLocation
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.map.*
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
        manager.registerTest(WrongNumericIDTest())
        manager.registerTest(MissingAttributeTest())
        manager.registerTest(NoDigraphNameTest())
    }

    fun registerSystemTestsReferenceImpl(manager: SystemTestManager) {
        // County Tests
        manager.registerTest(ExampleTest())
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
        manager.registerTest(WrongNumericIDTest())
        manager.registerTest(MissingAttributeTest())
        manager.registerTest(NoDigraphNameTest())
        manager.registerTest(NoVerticesTest())

        // assets
        manager.registerTest(NoVehiclesInBase())
        manager.registerTest(NotEnoughBases())
        manager.registerTest(SameBaseLocation())
        manager.registerTest(BaseOnNothing())
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
        manager.registerTest(WrongNumericIDTest())
        manager.registerTest(MissingAttributeTest())
        manager.registerTest(NoDigraphNameTest())

        manager.registerTest(NoVehiclesInBase())
        manager.registerTest(NotEnoughBases())
        manager.registerTest(SameBaseLocation())
        manager.registerTest(BaseOnNothing())
    }

    fun registerSystemTestsMutantSimulation(manager: SystemTestManager) {
        manager.registerTest(VertexWithoutRoadConnectionTest())
        manager.registerTest(VillageAndCountyNameSameTest())

        manager.registerTest(NoVerticesTest())

        // assets
        manager.registerTest(NoVehiclesInBase())
        manager.registerTest(NotEnoughBases())
        manager.registerTest(SameBaseLocation())
        manager.registerTest(BaseOnNothing())
    }
}
