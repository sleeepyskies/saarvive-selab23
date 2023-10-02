package graphtests

import de.unisaarland.cs.se.selab.graph.Graph
import de.unisaarland.cs.se.selab.graph.PrimaryType
import de.unisaarland.cs.se.selab.graph.Road
import de.unisaarland.cs.se.selab.graph.SecondaryType
import de.unisaarland.cs.se.selab.graph.Vertex
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CalculateShortestRouteTest {
    private lateinit var graph: Graph

    // Vertices
    val vertex1 = Vertex(0, mutableMapOf())
    val vertex2 = Vertex(1, mutableMapOf())
    val vertex3 = Vertex(2, mutableMapOf())

    @BeforeEach
    fun setup() {
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
    fun simplethreeVertexRoute() {
        var route = graph.calculateShortestRoute(vertex1, vertex3, 2)
        assert(route == listOf(vertex1, vertex2, vertex3))
    }
}
