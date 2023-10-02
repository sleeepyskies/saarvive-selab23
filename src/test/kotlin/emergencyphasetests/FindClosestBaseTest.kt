package emergencyphasetests

import de.unisaarland.cs.se.selab.dataClasses.bases.FireStation
import de.unisaarland.cs.se.selab.dataClasses.emergencies.Emergency
import de.unisaarland.cs.se.selab.dataClasses.emergencies.EmergencyType
import de.unisaarland.cs.se.selab.graph.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class FindClosestBaseTest {

    private lateinit var graph: Graph

    @BeforeEach
    private fun setUp() {
        // Graph setup
        val vertex1 = Vertex(0, mutableMapOf())
        val vertex2 = Vertex(1, mutableMapOf())
        val vertex3 = Vertex(2, mutableMapOf())

        val roadKrabStreet = Road(
            PrimaryType.SIDE_STREET,
            SecondaryType.NONE,
            "Bikini_Bottom",
            "Krusty_Krab_Street",
            10,
            3
        )
        val roadNotKrabStreet = Road(
            PrimaryType.MAIN_STREET,
            SecondaryType.NONE,
            "Bikini_Bottom",
            "Not_Krusty_Krab_Street",
            10,
            5
        )

        vertex1.connectingRoads[1] = roadKrabStreet
        vertex2.connectingRoads[0] = roadKrabStreet
        vertex2.connectingRoads[2] = roadNotKrabStreet
        vertex3.connectingRoads[1] = roadNotKrabStreet

        val roads = listOf(roadKrabStreet, roadNotKrabStreet)
        val vertices = listOf(vertex1, vertex2, vertex3)
        this.graph = Graph(vertices, roads)
    }

    @Test
    public fun findClosestBaseTest1() {
        val base1 = FireStation(0,0,1, mutableListOf())
        val base2 = FireStation(0,0,1, mutableListOf())
        val emergency = Emergency(
            0,
            EmergencyType.FIRE,
            1,
            0,
            0,
            0,
            "village",
            "road"
        )
    }
}