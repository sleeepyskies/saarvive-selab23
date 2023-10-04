package graphtests

import de.unisaarland.cs.se.selab.dataClasses.bases.Hospital
import de.unisaarland.cs.se.selab.dataClasses.bases.PoliceStation
import de.unisaarland.cs.se.selab.dataClasses.emergencies.Emergency
import de.unisaarland.cs.se.selab.dataClasses.emergencies.EmergencyType
import de.unisaarland.cs.se.selab.dataClasses.events.Construction
import de.unisaarland.cs.se.selab.dataClasses.events.RoadClosure
import de.unisaarland.cs.se.selab.dataClasses.events.RushHour
import de.unisaarland.cs.se.selab.dataClasses.events.TrafficJam
import de.unisaarland.cs.se.selab.dataClasses.vehicles.Ambulance
import de.unisaarland.cs.se.selab.dataClasses.vehicles.PoliceCar
import de.unisaarland.cs.se.selab.dataClasses.vehicles.VehicleType
import de.unisaarland.cs.se.selab.graph.Graph
import de.unisaarland.cs.se.selab.graph.PrimaryType
import de.unisaarland.cs.se.selab.graph.Road
import de.unisaarland.cs.se.selab.graph.SecondaryType
import de.unisaarland.cs.se.selab.graph.Vertex
import kotlin.test.Test

class GraphComplexClassTests {

    /**
     * Creates a graph for testing purposes
     */
    val road24 = Road(PrimaryType.COUNTY_ROAD, SecondaryType.NONE, "Coffee_land", "Kamuccino", 30, 10)
    val road41 = Road(PrimaryType.SIDE_STREET, SecondaryType.NONE, "Coffee_shop", "Min_Tea", 15, 10)
    val road16 = Road(PrimaryType.SIDE_STREET, SecondaryType.NONE, "Coffee_shop", "Sky_presser", 5, 10)
    val road67 = Road(PrimaryType.SIDE_STREET, SecondaryType.NONE, "Coffee_shop", "Irish_coffee", 10, 10)
    val road43 = Road(PrimaryType.SIDE_STREET, SecondaryType.NONE, "Coffee_shop", "Umamacchiato", 15, 10)
    val road37 = Road(PrimaryType.SIDE_STREET, SecondaryType.NONE, "Coffee_shop", "Caramel_kotlin", 15, 10)
    val road47 = Road(PrimaryType.MAIN_STREET, SecondaryType.NONE, "Coffee_shop", "Road_to_life", 40, 10)

    val road79 = Road(PrimaryType.MAIN_STREET, SecondaryType.NONE, "Coffee_land", "Parser_not_parsing", 10, 4)
    val road75 = Road(PrimaryType.MAIN_STREET, SecondaryType.NONE, "Coffee_land", "Cinnamon_Rolls", 15, 10)
    val road50 = Road(PrimaryType.MAIN_STREET, SecondaryType.NONE, "Coffee_land", "Napoleon_attacking_UdS", 15, 10)
    val road90 = Road(PrimaryType.SIDE_STREET, SecondaryType.NONE, "Coffee_land", "Not_us_dying", 10, 10)

    val road410 = Road(
        PrimaryType.SIDE_STREET,
        SecondaryType.ONE_WAY_STREET,
        "Coffee_shop",
        "Going_nowhere",
        5,
        10
    )
    val road1011 = Road(
        PrimaryType.SIDE_STREET,
        SecondaryType.ONE_WAY_STREET,
        "Coffee_shop",
        "Going_nowhere2",
        15,
        10
    )
    val road118 = Road(
        PrimaryType.SIDE_STREET,
        SecondaryType.ONE_WAY_STREET,
        "Coffee_shop",
        "Going_nowhere3",
        15,
        10
    )

    val road84 = Road(
        PrimaryType.SIDE_STREET,
        SecondaryType.ONE_WAY_STREET,
        "Coffee_shop",
        "Going_nowhere4",
        5,
        10
    )

    val road1012 = Road(
        PrimaryType.SIDE_STREET,
        SecondaryType.TUNNEL,
        "Coffee_shop",
        "Under_ground",
        5,
        2
    )

    val road1211 = Road(
        PrimaryType.SIDE_STREET,
        SecondaryType.TUNNEL,
        "Coffee_shop",
        "Literally_under_ground",
        5,
        2
    )

    val vertex2 = Vertex(2, mutableMapOf(Pair(4, road24)))
    val vertex4 =
        Vertex(
            4,
            mutableMapOf(Pair(2, road24), Pair(1, road41), Pair(3, road43), Pair(7, road47), Pair(10, road410))
        )
    val vertex1 = Vertex(1, mutableMapOf(Pair(4, road41), Pair(6, road16)))
    val vertex3 = Vertex(3, mutableMapOf(Pair(4, road43), Pair(7, road37)))
    val vertex6 = Vertex(6, mutableMapOf(Pair(1, road16), Pair(7, road67)))
    val vertex7 =
        Vertex(7, mutableMapOf(Pair(4, road47), Pair(3, road37), Pair(6, road67), Pair(9, road79), Pair(5, road75)))

    // For testing with height restriction
    val vertex9 = Vertex(9, mutableMapOf(Pair(7, road79), Pair(0, road90)))
    val vertex5 = Vertex(5, mutableMapOf(Pair(7, road75), Pair(0, road50)))
    val vertex0 = Vertex(0, mutableMapOf(Pair(9, road90), Pair(5, road50)))

    // For testing with OneWayRoads and tunnels
    val vertex8 = Vertex(8, mutableMapOf(Pair(4, road84)))
    val vertex10 = Vertex(10, mutableMapOf(Pair(11, road1011), Pair(12, road1012)))
    val vertex11 = Vertex(11, mutableMapOf(Pair(8, road118), Pair(12, road1211)))
    val vertex12 = Vertex(12, mutableMapOf(Pair(10, road1012), Pair(11, road1211)))

    private val graph = Graph(
        listOf(
            vertex0, vertex1, vertex2, vertex3, vertex4, vertex5, vertex6, vertex7, vertex8, vertex9, vertex10,
            vertex11, vertex12
        ),
        listOf(
            road24, road41, road16, road67, road43, road37, road47, road79, road75, road50, road90, road410,
            road1011, road118, road84, road1012, road1211
        )
    )

    // Bases
    val hospital1 = Hospital(1, 2, 2, 2, mutableListOf(Ambulance(VehicleType.AMBULANCE, 1, 1, 1, 1)))
    val hospital2 = Hospital(2, 10, 2, 2, mutableListOf(Ambulance(VehicleType.AMBULANCE, 2, 1, 1, 2)))
    val hospital3 = Hospital(3, 8, 2, 2, mutableListOf(Ambulance(VehicleType.AMBULANCE, 3, 1, 1, 3)))
    val hospital4 = Hospital(4, 3, 2, 2, mutableListOf(Ambulance(VehicleType.AMBULANCE, 4, 1, 1, 4)))
    val hospital5 = Hospital(5, 1, 2, 2, mutableListOf(Ambulance(VehicleType.AMBULANCE, 5, 1, 1, 5)))
    val police = PoliceStation(6, 4, 2, 2, mutableListOf(PoliceCar(VehicleType.POLICE_CAR, 6, 1, 1, 6, 1)))
    val listOfBases = listOf(hospital1, hospital2, hospital3, hospital4, hospital5, police)
    val baseToVertex = mutableMapOf(
        Pair(1, vertex2),
        Pair(2, vertex10),
        Pair(3, vertex8),
        Pair(4, vertex3),
        Pair(5, vertex1),
        Pair(6, vertex4)
    )

    val emergency = Emergency(2, EmergencyType.MEDICAL, 2, 1, 5, 8, "Coffee_land", "Kamuccino")

    // Events
    val constructionEvent = Construction(1, 10, 1, 1, 11, 12, true)
    val roadClosureEvent = RoadClosure(2, 10, 1, 10, 12)
    val rushHourEvent = RushHour(3, 10, 1, listOf(PrimaryType.SIDE_STREET), 2)
    val trafficJamEvent = TrafficJam(4, 10, 1, 2, 4, 1)

    val constructionEventForProximity = Construction(5, 10, 1, 3, 4, 10, false)

    @Test
    fun calculateShortestPathWithSameNumTicks() {
        val path = graph.calculateShortestPath(graph.graph[2], graph.graph[7], 0) // vertex 2 to vertex 7
        assert(path == 6)
    }

    @Test
    fun calculateShortestRouteTwoPossibilities() {
        val route = graph.calculateShortestRoute(graph.graph[2], graph.graph[7], 0) // vertex 2 to vertex 7
        val expectedRoute = listOf(graph.graph[2], graph.graph[1], graph.graph[6], graph.graph[7])
        assert(route == expectedRoute)
    }

    @Test
    fun calculateShortestPathWithHeightRestriction() {
        val path1 = graph.calculateShortestPath(graph.graph[7], graph.graph[0], 5) // vertex 7 to vertex 0
        assert(path1 == 3)

        val path2 = graph.calculateShortestPath(graph.graph[7], graph.graph[0], 3) // vertex 7 to vertex 0
        assert(path2 == 2)
    }

    @Test
    fun calculateShortestRouteTwoPossibilitiesWithHeightRestriction() {
        val route1 = graph.calculateShortestRoute(graph.graph[7], graph.graph[0], 5) // vertex 7 to vertex 0
        val expectedRoute1 = listOf(graph.graph[5], graph.graph[0])
        assert(route1 == expectedRoute1)

        val route2 = graph.calculateShortestRoute(graph.graph[7], graph.graph[0], 3) // vertex 7 to vertex 0
        val expectedRoute2 = listOf(graph.graph[9], graph.graph[0])
        assert(route2 == expectedRoute2)
    }

    @Test
    fun calculateShortestPathWithOneWayRoads() {
        val path1 = graph.calculateShortestPath(graph.graph[4], graph.graph[11], 0) // vertex 4 to vertex 11
        assert(path1 == 2)
        // with height restrictions
        val path2 = graph.calculateShortestPath(graph.graph[4], graph.graph[11], 4) // vertex 4 to vertex 11
        assert(path2 == 2)
    }

    @Test
    fun calculateShortestRouteWithOneWayRoads() {
        val route1 = graph.calculateShortestRoute(graph.graph[4], graph.graph[11], 0) // vertex 4 to vertex 11
        val expectedRoute1 = listOf(graph.graph[10], graph.graph[12], graph.graph[11])
        assert(route1 == expectedRoute1)
        // With height restrictions
        val route2 = graph.calculateShortestRoute(graph.graph[4], graph.graph[11], 5) // vertex 4 to vertex 11
        val expectedRoute2 = listOf(graph.graph[10], graph.graph[11])
        assert(route2 == expectedRoute2)
        // Back road
        val route3 = graph.calculateShortestRoute(graph.graph[12], graph.graph[4], 0) // vertex 12 to vertex 4
        val expectedRoute3 = listOf(graph.graph[11], graph.graph[8], graph.graph[4])
        assert(route3 == expectedRoute3)
    }

    @Test
    fun calculateWeightOfTheRoute1() {
        val path = graph.weightOfRoute(graph.graph[2], graph.graph[7], 0) // vertex 2 to vertex 7
        assert(path == 60)
    }

    @Test
    fun calculateWeightOfTheRoute2() {
        val path1 = graph.weightOfRoute(graph.graph[7], graph.graph[0], 5) // vertex 7 to vertex 0
        assert(path1 == 30)

        val path2 = graph.weightOfRoute(graph.graph[7], graph.graph[0], 3) // vertex 7 to vertex 0
        assert(path2 == 20)
    }

    @Test
    fun calculateWeightOfTheRoute3() {
        // With one way streets
        val path1 = graph.weightOfRoute(graph.graph[4], graph.graph[11], 5) // vertex 4 to vertex 11
        assert(path1 == 20)

        val path2 = graph.weightOfRoute(graph.graph[4], graph.graph[11], 0) // vertex 4 to vertex 11
        assert(path2 == 15)

        val path3 = graph.weightOfRoute(graph.graph[12], graph.graph[4], 0) // vertex 12 to vertex 4
        assert(path3 == 25)
    }

    // With events
    @Test
    fun testMapWithConstructionEvent() {
        graph.applyGraphEvent(constructionEvent)
        val path = graph.calculateShortestPath(graph.graph[10], graph.graph[11], 0) // vertex 10 to vertex 11
        assert(path == 2)

        val weight = graph.weightOfRoute(graph.graph[10], graph.graph[11], 0) // vertex 10 to vertex 11
        assert(weight == 15)

        val route = graph.calculateShortestRoute(graph.graph[10], graph.graph[11], 0) // vertex 10 to vertex 11
        val expectedRoute = listOf(graph.graph[11])
        assert(route == expectedRoute)

        graph.revertGraphEvent(constructionEvent)

        val path2 = graph.calculateShortestPath(graph.graph[10], graph.graph[11], 0) // vertex 10 to vertex 11
        assert(path2 == 1)

        val weight2 = graph.weightOfRoute(graph.graph[10], graph.graph[11], 0) // vertex 10 to vertex 11
        assert(weight2 == 10)

        val route2 = graph.calculateShortestRoute(graph.graph[10], graph.graph[11], 0) // vertex 10 to vertex 11
        val expectedRoute2 = listOf(graph.graph[12], graph.graph[11])
        assert(route2 == expectedRoute2)
    }

    @Test
    fun testMapWithRoadClosureEvent() {
        graph.applyGraphEvent(roadClosureEvent)
        val path = graph.calculateShortestPath(graph.graph[10], graph.graph[11], 0) // vertex 10 to vertex 11
        assert(path == 2)

        val weight = graph.weightOfRoute(graph.graph[10], graph.graph[11], 0) // vertex 10 to vertex 11
        assert(weight == 15)

        val route = graph.calculateShortestRoute(graph.graph[10], graph.graph[11], 0) // vertex 10 to vertex 11
        val expectedRoute = listOf(graph.graph[11])
        assert(route == expectedRoute)

        graph.revertGraphEvent(roadClosureEvent)

        val path2 = graph.calculateShortestPath(graph.graph[10], graph.graph[11], 0) // vertex 10 to vertex 11
        assert(path2 == 1)

        val weight2 = graph.weightOfRoute(graph.graph[10], graph.graph[11], 0) // vertex 10 to vertex 11
        assert(weight2 == 10)

        val route2 = graph.calculateShortestRoute(graph.graph[10], graph.graph[11], 0) // vertex 10 to vertex 11
        val expectedRoute2 = listOf(graph.graph[12], graph.graph[11])
        assert(route2 == expectedRoute2)
    }

    @Test
    fun testMapWithRushHourEvent() {
        graph.applyGraphEvent(rushHourEvent)
        val path = graph.calculateShortestPath(graph.graph[2], graph.graph[7], 0) // vertex 2 to vertex 7
        assert(path == 7)

        val weight = graph.weightOfRoute(graph.graph[2], graph.graph[7], 0) // vertex 2 to vertex 7
        assert(weight == 70)

        val route = graph.calculateShortestRoute(graph.graph[2], graph.graph[7], 0) // vertex 2 to vertex 7
        val expectedRoute = listOf(graph.graph[4], graph.graph[7])
        assert(route == expectedRoute)

        graph.revertGraphEvent(rushHourEvent)

        val path2 = graph.calculateShortestPath(graph.graph[2], graph.graph[7], 0) // vertex 2 to vertex 7
        assert(path2 == 6)

        val weight2 = graph.weightOfRoute(graph.graph[2], graph.graph[7], 0) // vertex 2 to vertex 7
        assert(weight2 == 60)

        val route2 = graph.calculateShortestRoute(graph.graph[2], graph.graph[7], 0) // vertex 2 to vertex 7
        val expectedRoute2 = listOf(graph.graph[4], graph.graph[1], graph.graph[6], graph.graph[7])
        assert(route2 == expectedRoute2)
    }

    @Test
    fun testMapWithTrafficJamEvent() {
        graph.applyGraphEvent(trafficJamEvent)
        val path = graph.calculateShortestPath(graph.graph[2], graph.graph[7], 0) // vertex 2 to vertex 7
        assert(path == 6)

        val weight = graph.weightOfRoute(graph.graph[2], graph.graph[7], 0) // vertex 2 to vertex 7
        assert(weight == 60)

        val route = graph.calculateShortestRoute(graph.graph[2], graph.graph[7], 0) // vertex 2 to vertex 7
        val expectedRoute = listOf(graph.graph[4], graph.graph[3], graph.graph[7])
        assert(route == expectedRoute)

        graph.revertGraphEvent(trafficJamEvent)

        val path2 = graph.calculateShortestPath(graph.graph[2], graph.graph[7], 0) // vertex 2 to vertex 7
        assert(path2 == 6)

        val weight2 = graph.weightOfRoute(graph.graph[2], graph.graph[7], 0) // vertex 2 to vertex 7
        assert(weight2 == 60)

        val route2 = graph.calculateShortestRoute(graph.graph[2], graph.graph[7], 0) // vertex 2 to vertex 7
        val expectedRoute2 = listOf(graph.graph[4], graph.graph[1], graph.graph[6], graph.graph[7])
        assert(route2 == expectedRoute2)
    }

    @Test
    fun findClosestBaseByProximityTest1() {
        val checkingList = graph.findClosestBasesByProximity(emergency, hospital1, listOfBases, baseToVertex)
        val expectedList = listOf(hospital2, hospital4, hospital5, hospital3)
        assert(checkingList == expectedList)
    }

    @Test
    fun findClosestBaseByProximityTest2() {
        // With events
        graph.applyGraphEvent(constructionEventForProximity)
        val checkingList = graph.findClosestBasesByProximity(emergency, hospital1, listOfBases, baseToVertex)
        val expectedList = listOf(hospital2, hospital4, hospital5, hospital3)
        assert(checkingList == expectedList)
    }
}
