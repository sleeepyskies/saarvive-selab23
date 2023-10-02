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

        // For testing with height restriction
        val vertex9 = Vertex(9, mutableMapOf())
        val vertex5 = Vertex(5, mutableMapOf())
        val vertex0 = Vertex(0, mutableMapOf())

        val road79 = Road(PrimaryType.MAIN_STREET, SecondaryType.NONE, "Coffee_land", "Parser_not_parsing", 10, 4)
        val road75 = Road(PrimaryType.MAIN_STREET, SecondaryType.NONE, "Coffee_land", "Cinnamon_Rolls", 15, 10)
        val road50 = Road(PrimaryType.MAIN_STREET, SecondaryType.NONE, "Coffee_land", "Napoleon_attacking_UdS", 15, 10)
        val road90 = Road(PrimaryType.SIDE_STREET, SecondaryType.NONE, "Coffee_land", "Not_us_dying", 10, 10)

        vertex0.connectingRoads[9] = road90
        vertex0.connectingRoads[5] = road50
        vertex9.connectingRoads[0] = road90
        vertex9.connectingRoads[7] = road79
        vertex5.connectingRoads[0] = road50
        vertex5.connectingRoads[7] = road75
        vertex7.connectingRoads[9] = road79
        vertex7.connectingRoads[5] = road75

        // For testing with OneWayRoads and tunnels
        val vertex8 = Vertex(8, mutableMapOf())
        val vertex10 = Vertex(10, mutableMapOf())
        val vertex11 = Vertex(11, mutableMapOf())
        val vertex12 = Vertex(12, mutableMapOf())

        val road410 = Road(PrimaryType.SIDE_STREET, SecondaryType.ONE_WAY_STREET, "Coffee_shop", "Going_nowhere", 5, 10)
        val road1011 = Road(PrimaryType.SIDE_STREET, SecondaryType.ONE_WAY_STREET, "Coffee_shop", "Going_nowhere2", 15, 10)
        val road118 = Road(PrimaryType.SIDE_STREET, SecondaryType.ONE_WAY_STREET, "Coffee_shop", "Going_nowhere2", 15, 10)

        val roads = listOf(road24, road41, road16, road67, road43, road37, road47, road79, road75, road50, road90)
        val vertices = listOf(vertex2, vertex4, vertex1, vertex3, vertex6, vertex7, vertex9, vertex5, vertex0)
        this.graph = Graph(vertices, roads)
    }

    @Test
    fun calculateShortestPathWithSameNumTicks() {
        val path = graph.calculateShortestPath(graph.graph[0], graph.graph[5], 0) // vertex 2 to vertex 7
        assert(path == 6)
    }

    @Test
    fun calculateShortestRouteTwoPossibilities() {
        val route = graph.calculateShortestRoute(graph.graph[0], graph.graph[5], 0) // vertex 2 to vertex 7
        val expectedRoute = listOf(graph.graph[1], graph.graph[2], graph.graph[4], graph.graph[5])
        assert(route == expectedRoute)
    }

    @Test
    fun calculateShortestPathWithHeightRestriction() {
        val path1 = graph.calculateShortestPath(graph.graph[5], graph.graph[8], 5) // vertex 7 to vertex 0
        assert(path1 == 9)

        val path2 = graph.calculateShortestPath(graph.graph[5], graph.graph[8], 3) // vertex 7 to vertex 0
        assert(path2 == 8)
    }

    @Test
    fun calculateShortestRouteTwoPossibilitiesWithHeightRestriction() {
        val route1 = graph.calculateShortestRoute(graph.graph[5], graph.graph[8], 5) // vertex 7 to vertex 0
        val expectedRoute1 = listOf(graph.graph[7], graph.graph[8])
        assert(route1 == expectedRoute1)

        val route2 = graph.calculateShortestRoute(graph.graph[5], graph.graph[8], 3) // vertex 7 to vertex 0
        val expectedRoute2 = listOf(graph.graph[6], graph.graph[8])
        assert(route2 == expectedRoute2)
    }
}
