package graphtests

import de.unisaarland.cs.se.selab.graph.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GraphTest {

    private lateinit var graph: Graph

    @BeforeEach
    public fun setUp() {
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
            3)
        val roadNotKrabStreet = Road(
            PrimaryType.MAIN_STREET,
            SecondaryType.NONE,
            "Bikini_Bottom",
            "Not_Krusty_Krab_Street",
            10,
            5)

        vertex1.connectingRoads[1] = roadKrabStreet
        vertex2.connectingRoads[0] = roadKrabStreet
        vertex2.connectingRoads[2] = roadNotKrabStreet
        vertex3.connectingRoads[1] = roadNotKrabStreet

        val roads = listOf(roadKrabStreet, roadNotKrabStreet)
        val vertices = listOf(vertex1, vertex2, vertex3)
        this.graph = Graph(vertices, roads)
    }

    @Test
    public fun shortestPathTest1() {
        var ticks = graph.calculateShortestPath(graph.graph[0], graph.graph[1], 2)
        assert(ticks == 1)
        ticks = graph.calculateShortestPath(graph.graph[0], graph.graph[1], 2)
        assert(ticks == 1)
    }
}