package de.unisaarland.cs.se.selab.graph

import FireStation
import Hospital
import PoliceStation
import de.unisaarland.cs.se.selab.dataClasses.bases.Base
import de.unisaarland.cs.se.selab.dataClasses.emergencies.Emergency
import de.unisaarland.cs.se.selab.dataClasses.emergencies.EmergencyType
import de.unisaarland.cs.se.selab.dataClasses.events.Event
import de.unisaarland.cs.se.selab.dataClasses.vehicles.Vehicle
import java.lang.Integer.min
import java.util.PriorityQueue

/**
 * Holds the data for the simulation graph consisting of vertices and roads.
 * @param graph A list of vertices containing connecting roads
 * @param roads A list of all the roads in the graph
 */
class Graph(private val graph: List<Vertex>, private val roads: List<Road>) {
    /**
     * Returns the shortest time in ticks needed to travel from start the vertex
     * to the destination vertex
     * @param start the start-point of the algorithm
     * @param destination the end-point of the algorithm
     * @param carHeight the car's height, set to 0 when ignoring height restrictions
     */
    fun calculateShortestPath(start: Vertex, destination: Vertex, carHeight: Int): Int {
        val visitedVertices: MutableMap<Vertex, Pair<Int, Vertex?>> = initVisitedVertices(start)
        val unvisitedVertices: MutableList<Vertex> = graph.toMutableList()
        var currentVertex = start

        // Algorithm
        while (unvisitedVertices.isNotEmpty()) {
            // gets all relevant neighbors based on height restrictions
            val neighbors = findValidNeighbors(currentVertex, carHeight)
            // updates neighbor distances
            updateNeighbors(neighbors, visitedVertices, currentVertex)

            unvisitedVertices.remove(currentVertex)
            // update nextVertex
            val nextVertex = findNextVertex(neighbors, visitedVertices)
            if (nextVertex != null) {
                currentVertex = nextVertex
            }
        }
        return visitedVertices[destination]?.first ?: -1
    }

    /**
     * Creates a mapping of each vertex in the graph to it's distance to the start vertex
     * and the previous vertex on the path. Distance initialised to Int.MAX_VALUE, previous vertex
     * initialised to null.
     */
    private fun initVisitedVertices(start: Vertex): MutableMap<Vertex, Pair<Int, Vertex?>> {
        val visitedVertices: MutableMap<Vertex, Pair<Int, Vertex?>> = mutableMapOf()
        for (vertex in graph) {
            visitedVertices[vertex] = if (vertex == start) Pair(0, null) else Pair(Int.MAX_VALUE, null)
        }
        return visitedVertices
    }

    /**
     * Finds all vertices a vehicle can travel to based on the road's height restriction
     */
    private fun findValidNeighbors(vertex: Vertex, carHeight: Int): Map<Vertex, Road> {
        return vertex.connectingRoads.filter { (_, road) -> carHeight <= road.heightLimit }
    }

    /**
     * Updates the distance for each neighbor and adds currentVertex as the previous vertex for each neighbor.
     */
    private fun updateNeighbors(
        neighbors: Map<Vertex, Road>,
        visitedVertices: MutableMap<
            Vertex,
            Pair<Int, Vertex?>
            >,
        currentVertex: Vertex
    ) {
        for ((neighbor, road) in neighbors) {
            // currentRouteWeight + roadWeight
            val distance = (visitedVertices[currentVertex]?.first ?: 0) + road.weight
            // if newWeight < oldWeight
            if (distance < (visitedVertices[neighbor]?.first ?: 0)) {
                visitedVertices[neighbor] = Pair(distance, currentVertex)
            }
        }
    }

    /**
     * Finds the next vertex to be used in Dijkstra's algorithm. Chooses the vertex connected
     * to the road with the smallest weight.
     */
    private fun findNextVertex(
        neighbors: Map<Vertex, Road>,
        visitedVertices: Map<Vertex, Pair<Int, Vertex?>>
    ): Vertex? {
        var nextVertex: Vertex? = null
        var minWeight = Int.MAX_VALUE

        for ((neighbor, road) in neighbors) {
            val distance = visitedVertices[neighbor]?.first ?: 0
            if (distance < minWeight) {
                minWeight = distance
                nextVertex = neighbor
            }
        }

        return nextVertex
    }

    /**
     * Calculates the exact route a vehicle should take from it' current location to the destination.
     * Returns a list of vertices.
     * In case there are multiple shortest routes, the route with lower road ID's is chosen
     * @param vehiclePosition The last visited vertex of the vehicle
     * @param destination The destination vertex to drive to
     * @param vehicleHeight The height of the vehicle driving
     */
    public fun calculateShortestRoute(vehiclePosition: Vertex, destination: Vertex, vehicleHeight: Int): List<Vertex> {
        var route = listOf<Vertex>()
        // maps how far all the vertices are from the current vertex
        val distances = mutableMapOf<Vertex, Int>()
        // allows a chain of previous vertices to be created that can be backtracked
        val previousVertices = mutableMapOf<Vertex, Vertex?>()

        /**
         * end of the queue init is a lambda expression that specifies how to compare two vertices (v1 and v2)
         * based on their distances from the start vertex (in distances)
         * ensures that vertices with smaller distances are dequeued from the priority queue first
         * !! double bang operator
         * used when certain that the object won't be null and want to access it without null safety checks
         */
        val unvisitedVertices = PriorityQueue<Vertex> { v1, v2 -> distances[v1]!! - distances[v2]!! }

        // initializing distances and previous vertices for all vertices in the graph
        for (vertex in graph) {
            distances[vertex] = if (vertex == vehiclePosition) 0 else Int.MAX_VALUE
            previousVertices[vertex] = null
            unvisitedVertices.offer(vertex)
        }

        // dijkstra's algorithm using the above structure
        while (unvisitedVertices.isNotEmpty()) {
            /**
             * .poll() finds the next vertex in the queue in the order of the lamda expression
             */
            val currentVertex = unvisitedVertices.poll()

            if (currentVertex == destination) {
                // found the shortest path to the end vertex
                route = buildRoute(destination, previousVertices)
            }

            // traverse connected vertices
            exploreNeighbours(currentVertex, distances, previousVertices, vehicleHeight)
        }
        return route
    }

    /**
     * used within the calculateShortestRoute method to explore all the connected vertices
     * and update the mappings when shortest vertex is found
     */
    private fun exploreNeighbours(
        currentVertex: Vertex,
        distances: MutableMap<Vertex, Int>,
        previousVertices: MutableMap<Vertex, Vertex?>,
        vehicleHeight: Int
    ) {
        for ((neighborVertex, connectingRoad) in currentVertex.connectingRoads) {
            // check if the cars height allows it to drive on the road
            if (vehicleHeight > connectingRoad.heightLimit) {
                continue
            }
            // calculate the weight of the route up till the neighbouring vertex
            val tentativeDistance = distances[currentVertex]!! + connectingRoad.weight
            // check if the route through this neighbour is shorter than the previous found route
            if (tentativeDistance < distances[neighborVertex]!!) {
                distances[neighborVertex] = tentativeDistance
                // update for backtracking
                previousVertices[neighborVertex] = currentVertex
            }
        }
    }

    /**
     * used within the calculateShortestRoute method to create the route
     * @param previousVertices contains backtracking of each vertex to its previous one in the optimal route
     * the functions parses through the backtracking
     */
    private fun buildRoute(endVertex: Vertex, previousVertices: Map<Vertex, Vertex?>): List<Vertex> {
        val route = mutableListOf<Vertex>()
        var currentVertex = endVertex
        var previousVertex = previousVertices[endVertex]
        // check if the start vertex is reached
        while (previousVertex != null) {
            route.add(currentVertex)
            currentVertex = previousVertex
            previousVertex = previousVertices[currentVertex]
        }
        // add the starting vertex to the list (not sure if it should be included)
        route.add(currentVertex)
        // the list of vertices starts with the first vertex in the route
        return route.reversed()
    }

    /**
     * Calculates the best route from a vehicle's location to an emergency vertex.
     * @param vehiclePosition The position of the vehicle to calculate the route for
     * @param emergency The emergency to use as a destination. Has a pair of vertices as location
     */
    public fun calculateBestRoute(vehiclePosition: Vertex, emergency: Emergency) {
        TODO("Unimplemented method")
    }

    /**
     * Finds and returns the closest relevant base for an emergency
     * @param emergency The emergency to find a base for
     * @param bases List of all bases of the correct base type
     * @param baseToVertex A mapping of each base to it's vertex
     */
    public fun findClosestBase(emergency: Emergency, bases: List<Base>, baseToVertex: MutableMap<Int, Vertex>): Base? {
        // Filter bases by emergency type
        val relevantBases = filterByEmergencyType(bases.toMutableList(), emergency)
        assert(relevantBases.isNotEmpty())
        // Create mapping of base to it's distance to the emergency
        val distanceToEmergency: MutableMap<Base, Int> = mutableMapOf()
        for (base in relevantBases) {
            val firstDistance = calculateShortestPath(baseToVertex[base.baseID]!!, emergency.location.first, 0)
            val secondDistance = calculateShortestPath(baseToVertex[base.baseID]!!, emergency.location.second, 0)
            distanceToEmergency[base] = min(firstDistance, secondDistance)
        }

        var minDistance = Int.MAX_VALUE
        var closestBase: Base? = null
        for ((base, distance) in distanceToEmergency) {
            if (distance < minDistance) {
                minDistance = distance
                closestBase = base
            }
            if (distance == minDistance) {
                closestBase = if (base.baseID < (closestBase?.baseID ?: Int.MAX_VALUE)) base else closestBase
            }
        }
        return closestBase
    }

    /**
     * Filters the given list of bases based on the emergency.
     */
    private fun filterByEmergencyType(bases: MutableList<Base>, emergency: Emergency): MutableList<Base> {
        for (base in bases) {
            when (Pair(emergency.emergencyType, getStringType(base))) {
                Pair(EmergencyType.FIRE, "FireStation") -> Unit
                Pair(EmergencyType.CRIME, "PoliceStation") -> Unit
                Pair(EmergencyType.MEDICAL, "Hospital") -> Unit
                Pair(EmergencyType.ACCIDENT, "FireStation") -> Unit
                else -> bases.remove(base)
            }
        }
        return bases
    }

    /**
     * Returns the type of a base as a string
     */
    private fun getStringType(base: Base): String {
        when (base) {
            is FireStation -> return "FireStation"
            is Hospital -> return "FireStation"
            is PoliceStation -> return "PoliceStation"
        }
        return ""
    }

    /**
     * Returns a list of bases responsible for a certain emergency type sorted by proximity to the provided base
     * @param emergency The type of base to return
     * @param base The base to create the list for
     */
    public fun findClosestBasesByProximity(
        emergency: Emergency,
        startBase: Base,
        bases: List<Base>,
        baseToVertex: MutableMap<Int, Vertex>
    ): List<Base> {
        val relevantBases = filterByEmergencyType(bases.toMutableList(), emergency)
        // stores the distance of each base from the start base
        val distanceMapping = mutableMapOf<Base, Int>()
        val startBaseVertex = baseToVertex[startBase.baseID]

        for (nextBase in relevantBases) {
            // ignore the start base
            if (nextBase == startBase) continue
            val nextBaseVertex = baseToVertex[nextBase.baseID]
            // get the shortest distance from the start base
            distanceMapping[nextBase] = calculateShortestPath(startBaseVertex!!, nextBaseVertex!!, 0)
        }

        /**
         * sort the bases by closest distance to the start bases
         * converts the map into a list
         */
        val sortedBases = distanceMapping.entries.sortedBy { it.value }.map { it.key }

        return sortedBases
    }

    /**
     * Applies the effect of the given graph event to the graph
     * @param event The event to apply the effects of
     */
    public fun applyGraphEvent(event: Event) {
        TODO("Unimplemented method")
    }

    /**
     * Reverts the effect of a given graph event on the graph
     * @param event The event to revert the effect of
     */
    public fun revertGraphEvent(event: Event) {
        TODO("Unimplemented method")
    }
}
