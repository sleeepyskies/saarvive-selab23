package graphtests

import de.unisaarland.cs.se.selab.graph.Graph
import de.unisaarland.cs.se.selab.graph.PrimaryType
import de.unisaarland.cs.se.selab.graph.Road
import de.unisaarland.cs.se.selab.graph.SecondaryType
import de.unisaarland.cs.se.selab.graph.Vertex
import org.junit.jupiter.api.Test

class CalculateShortestRouteTest {
    private lateinit var graph: Graph

    // Vertices
    val vertex0 = Vertex(0, mutableMapOf())
    val vertex1 = Vertex(1, mutableMapOf())
    val vertex2 = Vertex(2, mutableMapOf())
    val vertex3 = Vertex(3, mutableMapOf())
    val vertex4 = Vertex(4, mutableMapOf())
    val vertex5 = Vertex(5, mutableMapOf())

    // Roads
    val w10h5 = Road(
        PrimaryType.SIDE_STREET,
        SecondaryType.NONE,
        "Bikini_Bottom",
        "Krusty_Krab_Street",
        10,
        5
    )
    val w5h5 = Road(
        PrimaryType.MAIN_STREET,
        SecondaryType.NONE,
        "Bikini_Bottom",
        "Not_Krusty_Krab_Street",
        5,
        5
    )

    val w20h5 = Road(
        PrimaryType.MAIN_STREET,
        SecondaryType.NONE,
        "Bikini_Bottom",
        "Not_Krusty_Krab_Street",
        20,
        5
    )

    val w2h5 = Road(
        PrimaryType.MAIN_STREET,
        SecondaryType.NONE,
        "Bikini_Bottom",
        "Not_Krusty_Krab_Street",
        2,
        5
    )

    val w10h2 = Road(
        PrimaryType.MAIN_STREET,
        SecondaryType.NONE,
        "Bikini_Bottom",
        "Not_Krusty_Krab_Street",
        10,
        2
    )

    @Test
    fun simplethreeVertexRoute() {
        vertex0.connectingRoads[1] = w10h5
        vertex1.connectingRoads[0] = w10h5

        vertex1.connectingRoads[2] = w5h5
        vertex2.connectingRoads[1] = w5h5

        val roads = listOf(w10h5, w5h5)
        val vertices = listOf(vertex0, vertex1, vertex2)
        this.graph = Graph(vertices, roads)

        val route = graph.calculateShortestRoute(vertex0, vertex2, 2)
        assert(route == listOf(vertex1, vertex2))
    }

    @Test
    fun chooseBetween2RoutesA() {
        vertex0.connectingRoads[1] = w10h5
        vertex1.connectingRoads[0] = w10h5

        vertex1.connectingRoads[2] = w5h5
        vertex2.connectingRoads[1] = w5h5

        vertex0.connectingRoads[2] = w20h5
        vertex2.connectingRoads[0] = w20h5

        val roads = listOf(w10h5, w5h5, w20h5)
        val vertices = listOf(vertex0, vertex1, vertex2)
        this.graph = Graph(vertices, roads)

        val route = graph.calculateShortestRoute(vertex0, vertex2, 2)
        assert(route == listOf(vertex1, vertex2))
    }

    @Test
    fun chooseBetween2RoutesB() {
        vertex0.connectingRoads[1] = w10h5
        vertex1.connectingRoads[0] = w10h5

        vertex1.connectingRoads[2] = w5h5
        vertex2.connectingRoads[1] = w5h5

        vertex0.connectingRoads[2] = w10h2
        vertex2.connectingRoads[0] = w10h2

        val roads = listOf(w10h5, w5h5, w10h2)
        val vertices = listOf(vertex0, vertex1, vertex2)
        this.graph = Graph(vertices, roads)

        val route = graph.calculateShortestRoute(vertex0, vertex2, 2)
        assert(route == listOf(vertex2))
    }

    @Test
    fun checkWithHeighLimit() {
        vertex0.connectingRoads[1] = w10h5
        vertex1.connectingRoads[0] = w10h5

        vertex1.connectingRoads[2] = w5h5
        vertex2.connectingRoads[1] = w5h5

        vertex0.connectingRoads[2] = w10h2
        vertex2.connectingRoads[0] = w10h2

        val roads = listOf(w10h5, w5h5, w10h2)
        val vertices = listOf(vertex0, vertex1, vertex2)
        this.graph = Graph(vertices, roads)

        val route = graph.calculateShortestRoute(vertex0, vertex2, 3)
        assert(route == listOf(vertex1, vertex2))
    }

    @Test
    fun chooseBetweenRoutesComplex() {
        vertex0.connectingRoads[1] = w10h5
        vertex1.connectingRoads[0] = w10h5

        vertex1.connectingRoads[2] = w5h5
        vertex2.connectingRoads[1] = w5h5

        vertex0.connectingRoads[2] = w20h5
        vertex2.connectingRoads[0] = w20h5

        vertex2.connectingRoads[3] = w5h5
        vertex3.connectingRoads[2] = w5h5

        vertex2.connectingRoads[4] = w2h5
        vertex4.connectingRoads[2] = w2h5

        vertex3.connectingRoads[5] = w10h5
        vertex5.connectingRoads[3] = w10h5

        vertex4.connectingRoads[5] = w2h5
        vertex5.connectingRoads[4] = w2h5

        val roads = listOf(w10h5, w5h5, w20h5, w2h5)
        val vertices = listOf(vertex0, vertex1, vertex2, vertex3, vertex4, vertex5)
        this.graph = Graph(vertices, roads)

        val route = graph.calculateShortestRoute(vertex0, vertex5, 3)
        print(route)
        assert(route == listOf(vertex1, vertex2, vertex4, vertex5))
    }
}
