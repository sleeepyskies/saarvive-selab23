package graphtests

import de.unisaarland.cs.se.selab.graph.*
import kotlin.test.Test
import kotlin.test.assertEquals

class GraphTest {
    private fun createMockGraph(): Graph {
        val vertexA = Vertex(0, mutableMapOf())
        val vertexB = Vertex(1, mutableMapOf())
        val vertexC = Vertex(2, mutableMapOf())

        val roadAB = Road(PrimaryType.MAIN_STREET, SecondaryType.NONE, "VillageA", "RoadAB", 5, 2)
        val roadBC = Road(PrimaryType.SIDE_STREET, SecondaryType.ONE_WAY_STREET, "VillageB", "RoadBC", 3, 1)

        // Connect vertices with roads
        vertexA.connectingRoads[vertexB] = roadAB
        vertexB.connectingRoads[vertexC] = roadBC

        // Create a list of vertices and roads for the Graph class
        val vertices = listOf(vertexA, vertexB, vertexC)
        val roads = listOf(roadAB, roadBC)
        return Graph(vertices, roads)
    }

    @Test
    fun testCalculateShortestPath_SameVertex() {
        val mockGraph = createMockGraph()
        val vertexA = mockGraph.graph[0] // Get the first vertex

        // Calculate shortest path from a vertex to itself
        val shortestPath = mockGraph.calculateShortestPath(vertexA, vertexA, 0)

        // Expect that the shortest path is 0 since it's the same vertex
        assertEquals(0, shortestPath)
    }


}
