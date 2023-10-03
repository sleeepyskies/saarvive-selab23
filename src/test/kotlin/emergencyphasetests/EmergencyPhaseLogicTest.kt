package emergencyphasetests

import de.unisaarland.cs.se.selab.dataClasses.bases.FireStation
import de.unisaarland.cs.se.selab.dataClasses.bases.Hospital
import de.unisaarland.cs.se.selab.dataClasses.bases.PoliceStation
import de.unisaarland.cs.se.selab.dataClasses.emergencies.Emergency
import de.unisaarland.cs.se.selab.dataClasses.emergencies.EmergencyStatus
import de.unisaarland.cs.se.selab.dataClasses.emergencies.EmergencyType
import de.unisaarland.cs.se.selab.graph.Graph
import de.unisaarland.cs.se.selab.graph.PrimaryType
import de.unisaarland.cs.se.selab.graph.Road
import de.unisaarland.cs.se.selab.graph.SecondaryType
import de.unisaarland.cs.se.selab.graph.Vertex
import de.unisaarland.cs.se.selab.phases.EmergencyPhase
import de.unisaarland.cs.se.selab.simulation.DataHolder
import org.junit.jupiter.api.Test

class EmergencyPhaseLogicTest {

    val r1 = Road(
        PrimaryType.SIDE_STREET,
        SecondaryType.NONE,
        "Bikini_Bottom",
        "Krusty_Krab_Street",
        10,
        3
    )

    val r2 = Road(
        PrimaryType.MAIN_STREET,
        SecondaryType.NONE,
        "Bikini_Bottom",
        "Not_Krusty_Krab_Street",
        10,
        5
    )

    val r3 = Road(
        PrimaryType.MAIN_STREET,
        SecondaryType.NONE,
        "Bikini_Bottom",
        "WOWZA",
        10,
        5
    )

    val r4 = Road(
        PrimaryType.MAIN_STREET,
        SecondaryType.NONE,
        "Bikini_Bottom",
        "STUPIDTEST",
        10,
        5
    )

    val r5 = Road(
        PrimaryType.MAIN_STREET,
        SecondaryType.NONE,
        "Bikini_Bottom",
        "IHY",
        10,
        5
    )

    val r6 = Road(
        PrimaryType.SIDE_STREET,
        SecondaryType.NONE,
        "Bikini_Bottom",
        "New_Street",
        15,
        4
    )

    val r7 = Road(
        PrimaryType.SIDE_STREET,
        SecondaryType.NONE,
        "Bikini_Bottom",
        "Sandy_Cheeks_Street",
        12,
        3
    )

    val r8 = Road(
        PrimaryType.SIDE_STREET,
        SecondaryType.NONE,
        "Bikini_Bottom",
        "Jellyfish_Fields",
        8,
        2
    )

    val r9 = Road(
        PrimaryType.SIDE_STREET,
        SecondaryType.NONE,
        "Bikini_Bottom",
        "Chum_Bucket_Lane",
        13,
        4
    )

    val r10 = Road(
        PrimaryType.SIDE_STREET,
        SecondaryType.NONE,
        "Bikini_Bottom",
        "Goo_Lagoon_Road",
        9,
        3
    )

    val r11 = Road(
        PrimaryType.SIDE_STREET,
        SecondaryType.NONE,
        "Bikini_Bottom",
        "Rock_Bottom",
        7,
        2
    )

    val v0 = Vertex(0, mutableMapOf(Pair(1, r1), Pair(3, r2)))
    val v1 = Vertex(1, mutableMapOf(Pair(0, r1), Pair(2, r3), Pair(6, r4)))
    val v2 = Vertex(2, mutableMapOf(Pair(1, r3), Pair(4, r6)))
    val v3 = Vertex(3, mutableMapOf(Pair(0, r2), Pair(1, r3), Pair(5, r7)))
    val v4 = Vertex(4, mutableMapOf(Pair(2, r6), Pair(6, r8)))
    val v5 = Vertex(5, mutableMapOf(Pair(3, r7), Pair(7, r9)))
    val v6 = Vertex(6, mutableMapOf(Pair(1, r4), Pair(4, r8), Pair(8, r10)))
    val v7 = Vertex(7, mutableMapOf(Pair(5, r9), Pair(9, r11)))
    val v8 = Vertex(8, mutableMapOf(Pair(6, r10)))
    val v9 = Vertex(9, mutableMapOf(Pair(7, r11)))

    val vertices = listOf(v0, v1, v2, v3, v4, v5, v6, v7, v8, v9)
    val roads = listOf(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)

    val graph = Graph(vertices, roads)

    val fireStation1 = FireStation(0, 0, 1, mutableListOf())
    val hospital1 = Hospital(1, 2, 2, 3, mutableListOf())
    val policeStation1 = PoliceStation(2, 8, 3, 5, mutableListOf())
    val fireStation2 = FireStation(3, 3, 4, mutableListOf())
    val hospital2 = Hospital(4, 5, 5, 6, mutableListOf())
    val policeStation2 = PoliceStation(5, 7, 6, 8, mutableListOf())

    val bases = listOf(fireStation1, hospital1, policeStation1, fireStation2, hospital2, policeStation2)

    val emergency1 = Emergency(1, EmergencyType.FIRE, 1, 1, 1, 2, "Bikini_Bottom", "Krusty_Krab_Street")
    val emergency2 = Emergency(2, EmergencyType.MEDICAL, 2, 3, 2, 4, "Bikini_Bottom", "WOWZA")
    val emergency3 = Emergency(3, EmergencyType.CRIME, 3, 1, 3, 3, "Bikini_Bottom", "Sandy_Cheeks_Street")
    val emergency4 = Emergency(4, EmergencyType.FIRE, 1, 2, 1, 2, "Bikini_Bottom", "Not_Krusty_Krab_Street")
    val emergency5 = Emergency(5, EmergencyType.MEDICAL, 2, 3, 2, 4, "Bikini_Bottom", "New_Street")
    val emergency6 = Emergency(6, EmergencyType.CRIME, 3, 3, 3, 3, "Bikini_Bottom", "Jellyfish_Fields")

    val emergencies = listOf(emergency1, emergency2, emergency3, emergency4, emergency5, emergency6).toMutableList()

    val dataHolder = DataHolder(graph, bases, mutableListOf(), emergencies)

    val ep = EmergencyPhase(dataHolder)

    @Test
    fun scheduleEmergenciesTest1() {
        ep.currentTick = 3

        val emergencyList = ep.scheduleEmergencies()
        assert(emergencyList == mutableListOf(emergency2, emergency5, emergency6))
    }

    @Test
    fun scheduleEmergenciesTest2() {
        ep.currentTick = 2

        val emergencyList = ep.scheduleEmergencies()
        assert(emergencyList == mutableListOf(emergency4))
    }

    @Test
    fun assignBaseToEmergenciesTest() {
        ep.currentTick = 2
        val emergencyList = ep.scheduleEmergencies()

        ep.assignBasesToEmergencies(emergencyList)
        emergency2.location = Pair(v0, v1)

        assert(emergency4.emergencyStatus == EmergencyStatus.ASSIGNED)
        assert(dataHolder.emergencyToBase[emergency4.id] == fireStation1)
    }

    @Test
    fun sortBySeverityTest1() {
        ep.currentTick = 1
        ep.scheduleEmergencies()
        ep.sortBySeverity(this.dataHolder)
        assert(dataHolder.ongoingEmergencies == mutableListOf(emergency3, emergency1))
    }

    @Test
    fun sortBySeverityTest2() {
        ep.currentTick = 2
        ep.scheduleEmergencies()
        ep.sortBySeverity(this.dataHolder)
        assert(dataHolder.ongoingEmergencies == mutableListOf(emergency4))
    }

    @Test
    fun sortBySeverityTest3() {
        ep.currentTick = 3
        ep.scheduleEmergencies()
        ep.sortBySeverity(this.dataHolder)
        assert(dataHolder.ongoingEmergencies == mutableListOf(emergency6, emergency2, emergency5))
    }
}
