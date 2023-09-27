package graphtests

import de.unisaarland.cs.se.selab.graph.Graph
import de.unisaarland.cs.se.selab.graph.PrimaryType
import de.unisaarland.cs.se.selab.graph.Road
import de.unisaarland.cs.se.selab.graph.SecondaryType
import de.unisaarland.cs.se.selab.graph.Vertex
import kotlin.test.Test
import kotlin.test.assertEquals

class GraphTest {
    private fun createMockGraph(): Graph {
        val vertexA = Vertex(0, mutableMapOf())
        val vertexB = Vertex(1, mutableMapOf())
        val vertexC = Vertex(2, mutableMapOf())
        val vertexD = Vertex(3, mutableMapOf())
        val vertexE = Vertex(4, mutableMapOf())

        val roadAB = Road(PrimaryType.MAIN_STREET, SecondaryType.NONE, "VillageA", "RoadAB", 5, 2)
        val roadBC = Road(PrimaryType.SIDE_STREET, SecondaryType.TUNNEL, "VillageB", "RoadBC", 3, 1)
        val roadCD = Road(PrimaryType.SIDE_STREET, SecondaryType.ONE_WAY_STREET, "VillageC", "RoadCD", 2, 1)
        val roadAE = Road(PrimaryType.SIDE_STREET, SecondaryType.NONE, "VillageD", "RoadDE", 1, 1)
        val roadDE = Road(PrimaryType.SIDE_STREET, SecondaryType.NONE, "VillageD", "RoadDE", 4, 1)
        // Connect vertices with roads
        vertexA.connectingRoads[vertexB] = roadAB
        vertexB.connectingRoads[vertexA] = roadAB

        vertexB.connectingRoads[vertexC] = roadBC
        vertexC.connectingRoads[vertexB] = roadBC

        vertexC.connectingRoads[vertexD] = roadCD

        vertexA.connectingRoads[vertexE] = roadAE
        vertexE.connectingRoads[vertexA] = roadAE

        vertexD.connectingRoads[vertexE] = roadDE
        vertexE.connectingRoads[vertexD] = roadDE


        // Create a list of vertices and roads for the Graph class
        val vertices = listOf(vertexA, vertexB, vertexC, vertexD, vertexE)
        val roads = listOf(roadAB, roadBC, roadCD, roadAE, roadDE)
        return Graph(vertices, roads)
    }

    @Test
    fun testCalculateShortestPath_SameVertex() {
        val mockGraph = createMockGraph()
        val vertexA = mockGraph.graph[0] // Get the first vertex

        // Calculate the shortest path from a vertex to itself
        val shortestPath = mockGraph.calculateShortestPath(vertexA, vertexA, 0)

        // Expect that the shortest path is 0 since it's the same vertex
        assertEquals(0, shortestPath)
    }

    @Test
    fun testCalculateShortestPath_SimplePath() {
        val mockGraph = createMockGraph()
        val vertexA = mockGraph.graph[0] // Get the first vertex
        val vertexB = mockGraph.graph[1] // Get the second vertex

        // Calculate the shortest path from vertex A to vertex B
        val shortestPath = mockGraph.calculateShortestPath(vertexA, vertexB, 0)

        // Expect that the shortest path is 5 since it's the only path
        assertEquals(5, shortestPath)
    }

    @Test
    fun testCalculateShortestPath_ComplexPath() {
        val mockGraph = createMockGraph()
        val vertexA = mockGraph.graph[0] // Get the first vertex
        val vertexC = mockGraph.graph[2] // Get the third vertex

        // Calculate the shortest path from vertex A to vertex C
        val shortestPath = mockGraph.calculateShortestPath(vertexA, vertexC, 0)

        // Expect that the shortest path is 8 since it's the only path
        assertEquals(8, shortestPath)
    }

    @Test
    fun tesCalculateShortestPath_NoPath() {
        val mockGraph = createMockGraph()
        val vertexA = mockGraph.graph[0] // Get the first vertex
        val vertexC = mockGraph.graph[2] // Get the third vertex

        // Calculate the shortest path from vertex C to vertex A
        val shortestPath = mockGraph.calculateShortestPath(vertexC, vertexA, 0)

        // Expect that the shortest path is -1 since there is no path
        assertEquals(0, shortestPath)
    }

    @Test
    fun testCalculateShortestPath_AE() {
        val mockGraph = createMockGraph()
        val vertexA = mockGraph.graph[0] // Get the first vertex
        val vertexE = mockGraph.graph[4] // Get the fifth vertex

        // Calculate the shortest path from vertex A to vertex E
        val shortestPath = mockGraph.calculateShortestPath(vertexA, vertexE, 0)

        // Expect that the shortest path is 1 since it's the only path
        assertEquals(1, shortestPath)
    }

    @Test
    fun testCalculateShortestPath_heightLimit() {
        val mockGraph = createMockGraph()
        val vertexA = mockGraph.graph[0] // Get the first vertex
        val vertexC = mockGraph.graph[2] // Get the third vertex

        // Calculate the shortest path from vertex A to vertex E
        val shortestPath = mockGraph.calculateShortestPath(vertexA, vertexC, 5)

        // Expect that the shortest path is 1 since there's a tunnel road
        assertEquals(0, shortestPath)
    }

    @Test
    fun testCalculateShortestRoute_SimpleRoute() {
        val mockGraph = createMockGraph()
        val vertexA = mockGraph.graph[0] // Get the first vertex
        val vertexC = mockGraph.graph[2] // Get the third vertex

        // Calculate the shortest route from vertex A to vertex C without height restrictions
        val shortestRoute = mockGraph.calculateShortestRoute(vertexA, vertexC, 0)

        // Check that the shortest route is as expected
        assertEquals(listOf(vertexA, mockGraph.graph[1], vertexC), shortestRoute)
    }

    @Test
    fun testCalculateShortestRoute_HeightRestrictions() {
        val mockGraph = createMockGraph()
        val vertexA = mockGraph.graph[0] // Get the first vertex
        val vertexC = mockGraph.graph[2] // Get the third vertex

        // Calculate the shortest route from vertex A to vertex C with a height limit that should restrict one road
        val shortestRoute = mockGraph.calculateShortestRoute(vertexA, vertexC, 2)

        // Check that the shortest route avoids the road with a height limit of 3
        assertEquals(listOf(vertexA, mockGraph.graph[0], mockGraph.graph[1], vertexC), shortestRoute)
    }
}
