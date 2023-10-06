package de.unisaarland.cs.se.selab.systemtest

import de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.assets.*
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.invalidScenarios.InvalidScenario1
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.map.*
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.simulation.EmptySimulationTest
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.simulation.WhyThisEmergencySoWrong
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.simulation.WrongEmergenciesTest
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.faultytests.simulation.WrongEventsTest
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.validtests.CorrectEdgesToVerticesTest
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.validtests.ValidSmallMapSpacesKingdomTest
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.validtests.phases.AllocateFireEmergencyOneTest1
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.validtests.phases.AllocateFireEmergencyOneTest2
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.validtests.phases.AllocateFireEmergencyOneTest3
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.validtests.phases.TravelMultipleVerticesTest
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.validtests.scenarios.BROvalidScenario
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.validtests.scenarios.ComplexScenario1Test
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.validtests.scenarios.DADvalidScenario
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.validtests.scenarios.FATHERvalidScenario
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.validtests.scenarios.FireTrucksRefillTest
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.validtests.scenarios.MOMvalidScenario
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.validtests.scenarios.MedicalSeverity3
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.validtests.scenarios.SISvalidScenario
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.validtests.scenarios.ValidScenario1
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.validtests.scenarios.ValidScenario2
import de.unisaarland.cs.se.selab.systemtest.mysystemtest.validtests.scenarios.ValidScenario3
import de.unisaarland.cs.se.selab.systemtest.runner.SystemTestManager

object SystemTestRegistration {
    fun registerSystemTests(manager: SystemTestManager) {
        // phase tests
        // manager.registerTest(AllocateFireEmergencyOneTest1())
        manager.registerTest(AllocateFireEmergencyOneTest2())
        manager.registerTest(AllocateFireEmergencyOneTest3())
        // Valid scenarios
        manager.registerTest(ValidScenario1())
        manager.registerTest(ValidScenario2())
        manager.registerTest(ValidScenario3())
        manager.registerTest(ComplexScenario1Test())
        manager.registerTest(BROvalidScenario())
        manager.registerTest(SISvalidScenario())
        manager.registerTest(DADvalidScenario())
        manager.registerTest(FATHERvalidScenario())
        manager.registerTest(InvalidScenario1())
        manager.registerTest(MOMvalidScenario())
    }

    fun registerSystemTestsReferenceImpl(manager: SystemTestManager) {
        manager.registerTest(ComplexScenario1Test())
        manager.registerTest(SISvalidScenario())
        manager.registerTest(FATHERvalidScenario())
        manager.registerTest(InvalidScenario1())
        manager.registerTest(FireTrucksRefillTest())
        manager.registerTest(AllocateFireEmergencyOneTest1())
        manager.registerTest(AllocateFireEmergencyOneTest2())
        manager.registerTest(AllocateFireEmergencyOneTest3())
        manager.registerTest(TravelMultipleVerticesTest())
        manager.registerTest(ValidScenario1())
        manager.registerTest(ValidScenario2())
        manager.registerTest(ValidScenario3())
        manager.registerTest(BROvalidScenario())
        manager.registerTest(DADvalidScenario())
        manager.registerTest(SISvalidScenario())
        manager.registerTest(InvalidScenario1())
        manager.registerTest(MedicalSeverity3())
        manager.registerTest(InvalidScenario1())
        manager.registerTest(MOMvalidScenario())
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
        manager.registerTest(NoRoadBetween())
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
        manager.registerTest(LongLadderTest())
        manager.registerTest(TallCarTest())
        manager.registerTest(LadderBikeTest())
        manager.registerTest(NoCriminalsTest())
        manager.registerTest(TooManyCriminals())
        manager.registerTest(WrongCarWrongStuffTest())
        manager.registerTest(WhyThisEmergencySoWrong())
        // manager.registerTest(TwoBasesTest())

        // simulation tests
        manager.registerTest(EmptySimulationTest())
        manager.registerTest(WrongEmergenciesTest())
        manager.registerTest(WrongEventsTest())
        // manager.registerTest(BROvalidScenario())
        manager.registerTest(TravelMultipleVerticesTest())
    }

    fun registerSystemTestsMutantSimulation(manager: SystemTestManager) {
        // Valid scenarios
        manager.registerTest(AllocateFireEmergencyOneTest1())
        manager.registerTest(AllocateFireEmergencyOneTest2())
        manager.registerTest(AllocateFireEmergencyOneTest3())
        manager.registerTest(TravelMultipleVerticesTest())
        manager.registerTest(ValidScenario1())
        manager.registerTest(ValidScenario2())
        manager.registerTest(ValidScenario3())
        manager.registerTest(BROvalidScenario())
        manager.registerTest(DADvalidScenario())
        manager.registerTest(AllocateFireEmergencyOneTest3())
        manager.registerTest(MedicalSeverity3())
        manager.registerTest(FATHERvalidScenario())
        manager.registerTest(SISvalidScenario())
        manager.registerTest(MedicalSeverity3())
    }
}
