package graphtests

import de.unisaarland.cs.se.selab.graph.Graph
import de.unisaarland.cs.se.selab.graph.PrimaryType
import de.unisaarland.cs.se.selab.graph.Road
import de.unisaarland.cs.se.selab.graph.SecondaryType
import de.unisaarland.cs.se.selab.graph.Vertex
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

class GraphComplexClassTests {

    private lateinit var graph: Graph

    /**
     * Creates a graph for testing purposes
     */
    @BeforeEach
    fun buildMap() {
        val vertex2 = Vertex(2, mutableMapOf())
        val vertex4 = Vertex(4, mutableMapOf())
        val vertex1 = Vertex(1, mutableMapOf())
        val vertex3 = Vertex(3, mutableMapOf())
        val vertex6 = Vertex(6, mutableMapOf())
        val vertex7 = Vertex(7, mutableMapOf())

        val road24 = Road(PrimaryType.COUNTY_ROAD, SecondaryType.NONE, "Coffee_land", "Kamuccino", 30, 10)
        val road41 = Road(PrimaryType.SIDE_STREET, SecondaryType.NONE, "Coffee_shop", "Min_Tea", 15, 10)
        val road16 = Road(PrimaryType.SIDE_STREET, SecondaryType.NONE, "Coffee_shop", "Sky_presser", 5, 10)
        val road67 = Road(PrimaryType.SIDE_STREET, SecondaryType.NONE, "Coffee_shop", "Irish_coffee", 10, 10)
        val road43 = Road(PrimaryType.SIDE_STREET, SecondaryType.NONE, "Coffee_shop", "Umamacchiato", 15, 10)
        val road37 = Road(PrimaryType.SIDE_STREET, SecondaryType.NONE, "Coffee_shop", "Caramel_kotlin", 15, 10)
        val road47 = Road(PrimaryType.MAIN_STREET, SecondaryType.NONE, "Coffee_shop", "Road_to_life", 40, 10)

        vertex2.connectingRoads[4] = road24
        vertex4.connectingRoads[2] = road24
        vertex4.connectingRoads[1] = road41
        vertex4.connectingRoads[3] = road43
        vertex4.connectingRoads[7] = road47
        vertex1.connectingRoads[4] = road41
        vertex1.connectingRoads[6] = road16
        vertex6.connectingRoads[1] = road16
        vertex6.connectingRoads[7] = road67
        vertex3.connectingRoads[4] = road43
        vertex3.connectingRoads[7] = road37
        vertex7.connectingRoads[3] = road37
        vertex7.connectingRoads[6] = road67
        vertex7.connectingRoads[4] = road47

        val roads = listOf(road24, road41, road16, road67, road43, road37, road47)
        val vertices = listOf(vertex2, vertex4, vertex1, vertex3, vertex6, vertex7)
        this.graph = Graph(vertices, roads)
    }

    @Test
    fun shortestPathWithSameNumTicks() {
        val path = graph.calculateShortestPath(graph.graph[0], graph.graph[5], 0) // vertex 2 to vertex 7
        assert(path == 6)
    }

    @Test
    fun calculateShortestRouteTwoPossibilities() {
        val route = graph.calculateShortestRoute(graph.graph[0], graph.graph[5], 0) // vertex 2 to vertex 7
        val expectedRoute = listOf(graph.graph[1], graph.graph[2], graph.graph[4], graph.graph[5])
        assert(route == expectedRoute)
    }
}
