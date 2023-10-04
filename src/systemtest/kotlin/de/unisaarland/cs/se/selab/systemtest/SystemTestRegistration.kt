package de.unisaarland.cs.se.selab.systemtest

import de.unisaarland.cs.se.selab.systemtest.basictests.ExampleTest
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.assets.*
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

        // asset parser tests
        manager.registerTest(NoVehiclesInBase())
        manager.registerTest(NotEnoughBases())
        manager.registerTest(SameBaseLocation())
        manager.registerTest(BaseOnNothing())
        manager.registerTest(WhatBaseTest())
        manager.registerTest(BaseWrongAssetsTest())
        manager.registerTest(PuppyHeavenTest())
        manager.registerTest(AnAppleADayTest())
        manager.registerTest(DoctorDawgTest())
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

        // asset parser tests
        manager.registerTest(NoVehiclesInBase())
        manager.registerTest(NotEnoughBases())
        manager.registerTest(SameBaseLocation())
        manager.registerTest(BaseOnNothing())
        manager.registerTest(WhatBaseTest())
        manager.registerTest(BaseWrongAssetsTest())
        manager.registerTest(PuppyHeavenTest())
        manager.registerTest(AnAppleADayTest())
        manager.registerTest(DoctorDawgTest())
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

        // asset parser tests
        manager.registerTest(NoVehiclesInBase())
        manager.registerTest(NotEnoughBases())
        manager.registerTest(SameBaseLocation())
        manager.registerTest(BaseOnNothing())
        manager.registerTest(WhatBaseTest())
        manager.registerTest(BaseWrongAssetsTest())
        manager.registerTest(PuppyHeavenTest())
        manager.registerTest(AnAppleADayTest())
        manager.registerTest(DoctorDawgTest())
    }

    fun registerSystemTestsMutantSimulation(manager: SystemTestManager) {
        manager.registerTest(VertexWithoutRoadConnectionTest())
        manager.registerTest(VillageAndCountyNameSameTest())
    }
}
