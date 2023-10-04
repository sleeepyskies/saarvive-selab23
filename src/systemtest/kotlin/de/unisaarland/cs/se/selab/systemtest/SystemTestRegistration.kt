package de.unisaarland.cs.se.selab.systemtest

import de.unisaarland.cs.se.selab.systemtest.basictests.ExampleTest
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.assets.*
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.map.*
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.simulation.EmptySimulationTest
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.simulation.WrongEmergenciesTest
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.simulation.WrongEventsTest
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.validtests.CorrectEdgesToVerticesTest
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.validtests.CorrectSystemTestSmall
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.validtests.EmergencyFailTest
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.validtests.ReallocationTest
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.validtests.RequestTest
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.validtests.ValidSmallMapSpacesKingdomTest
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
        manager.registerTest(FireDoctorsTest())
        manager.registerTest(SameIDAssetsTest())
        manager.registerTest(SameIDBaseTest())
        manager.registerTest(MissingTerminalTest())
        manager.registerTest(NotTheEndTest())
        manager.registerTest(ValidSmallMapSpacesKingdomTest())
        manager.registerTest(InvalidStringIDTest())
        manager.registerTest(HasOnlyTwoVerticesTest())
        manager.registerTest(HasTwoVerticesAndARoadTest())
        manager.registerTest(HasOnlyRoadsTest())
        manager.registerTest(HasReversedDataTest())
        manager.registerTest(VillagesWithSameNameTest())
        manager.registerTest(CountySameRoadNameTest())
        manager.registerTest(CorrectEdgesToVerticesTest())
        manager.registerTest(NotEnoughWaterTest())
        manager.registerTest(WrongWaterTest())
        manager.registerTest(BackseatDriversTest())

        // simulation tests
        manager.registerTest(EmptySimulationTest())
        manager.registerTest(WrongEmergenciesTest())
        manager.registerTest(WrongEventsTest())
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
        manager.registerTest(SplittedNameTest())
        manager.registerTest(MissingTerminalTest())
        manager.registerTest(NotTheEndTest())
        manager.registerTest(ValidSmallMapSpacesKingdomTest())
        manager.registerTest(InvalidStringIDTest())
        manager.registerTest(HasOnlyTwoVerticesTest())
        manager.registerTest(HasTwoVerticesAndARoadTest())
        manager.registerTest(HasOnlyRoadsTest())
        manager.registerTest(HasReversedDataTest())
        manager.registerTest(VillagesWithSameNameTest())
        manager.registerTest(CountySameRoadNameTest())
        manager.registerTest(CorrectEdgesToVerticesTest())

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
        manager.registerTest(FireDoctorsTest())
        manager.registerTest(SameIDAssetsTest())
        manager.registerTest(SameIDBaseTest())
        manager.registerTest(NotEnoughWaterTest())
        manager.registerTest(WrongWaterTest())
        manager.registerTest(BackseatDriversTest())

        // simulation tests
        manager.registerTest(EmptySimulationTest())
        manager.registerTest(WrongEmergenciesTest())
        manager.registerTest(WrongEventsTest())
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
        manager.registerTest(SplittedNameTest())
        manager.registerTest(MissingTerminalTest())
        manager.registerTest(NotTheEndTest())
        manager.registerTest(ValidSmallMapSpacesKingdomTest())
        manager.registerTest(InvalidStringIDTest())
        manager.registerTest(HasOnlyTwoVerticesTest())
        manager.registerTest(HasTwoVerticesAndARoadTest())
        manager.registerTest(HasOnlyRoadsTest())
        manager.registerTest(HasReversedDataTest())
        manager.registerTest(VillagesWithSameNameTest())
        manager.registerTest(CountySameRoadNameTest())
        manager.registerTest(CorrectEdgesToVerticesTest())

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
        manager.registerTest(FireDoctorsTest())
        manager.registerTest(SameIDAssetsTest())
        manager.registerTest(SameIDBaseTest())
        manager.registerTest(NotEnoughWaterTest())
        manager.registerTest(WrongWaterTest())
        manager.registerTest(BackseatDriversTest())

        // simulation tests
        manager.registerTest(EmptySimulationTest())
        manager.registerTest(WrongEmergenciesTest())
        manager.registerTest(WrongEventsTest())
    }

    fun registerSystemTestsMutantSimulation(manager: SystemTestManager) {
        manager.registerTest(VertexWithoutRoadConnectionTest())
        manager.registerTest(VillageAndCountyNameSameTest())
    }
}
