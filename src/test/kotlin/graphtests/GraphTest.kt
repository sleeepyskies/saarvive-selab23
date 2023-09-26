package graphtests

import de.unisaarland.cs.se.selab.dataClasses.events.Event
import de.unisaarland.cs.se.selab.graph.*
import kotlin.test.Test
import kotlin.test.assertEquals

class GraphTest {
    private fun createMockGraph(): Graph {
        val vertexA = Vertex(0, mutableMapOf())
        val vertexB = Vertex(1, mutableMapOf())
        val vertexC = Vertex(2, mutableMapOf())
        val vertexD = Vertex(3, mutableMapOf())
        val vertexE = Vertex(4, mutableMapOf())

        val empty = mutableListOf<Event>()
        val roadAB = Road(PrimaryType.MAIN_STREET, SecondaryType.NONE, "VillageA", "RoadAB", 5, 2, empty)
        val roadBC = Road(PrimaryType.SIDE_STREET, SecondaryType.TUNNEL, "VillageB", "RoadBC", 3, 3,empty)
        val roadCD = Road(PrimaryType.SIDE_STREET, SecondaryType.ONE_WAY_STREET, "VillageC", "RoadCD", 2, 1,empty)
        val roadAE = Road(PrimaryType.SIDE_STREET, SecondaryType.NONE, "VillageD", "RoadDE", 1, 1,empty)
        val roadDE = Road(PrimaryType.SIDE_STREET, SecondaryType.NONE, "VillageD", "RoadDE", 4, 1,empty)
        // Connect vertices with roads
        vertexA.connectingRoads[vertexB] = roadAB
        vertexB.connectingRoads[vertexC] = roadBC

        // Create a list of vertices and roads for the Graph class
        val vertices = listOf(vertexA, vertexB, vertexC, vertexD, vertexE)
        val roads = listOf(roadAB, roadBC, roadCD, roadAE, roadDE)
        return Graph(vertices, roads)
    }

    @Test
    fun testCalculateShortestPath_SameVertex() {
        val mockGraph = createMockGraph()
        val vertexA = mockGraph.graph[0] // Get the first vertex

        // Calculate theshortest path from a vertex to itself
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
    fun testCalculateShortestPath_AE(){
        val mockGraph = createMockGraph()
        val vertexA = mockGraph.graph[0] // Get the first vertex
        val vertexE = mockGraph.graph[4] // Get the fifth vertex

        // Calculate the shortest path from vertex A to vertex E
        val shortestPath = mockGraph.calculateShortestPath(vertexA, vertexE, 0)

        // Expect that the shortest path is 1 since it's the only path
        assertEquals(1, shortestPath)
    }


}
