package de.unisaarland.cs.se.selab.graph

import de.unisaarland.cs.se.selab.dataClasses.bases.Base
import de.unisaarland.cs.se.selab.dataClasses.emergencies.Emergency
import de.unisaarland.cs.se.selab.dataClasses.events.Event
import de.unisaarland.cs.se.selab.dataClasses.vehicles.Vehicle
import java.util.PriorityQueue

/**
 * Holds the data for the simulation graph consisting of vertices and roads.
 * @param graph A list of vertices containing connecting roads
 */
class Graph(private val graph: List<Vertex>) {
    /**
     * Returns the shortest time in ticks needed to travel from start the vertex
     * to the destination vertex
     * @param start the start-point of the algorithm
     * @param destination the end-point of the algorithm
     * @param carHeight the car's height, set to 0 when ignoring height restrictions
     */
    public fun calculateShortestPath(start: Vertex, destination: Vertex, carHeight: Int): Int {
        // create a structure to store for each vertex it's shortest distance from start and previous vertex
        val visitedVertices: MutableMap<Vertex, Pair<Int, Vertex?>> = mutableMapOf()
        for (vertex in graph) {
            visitedVertices[vertex] = if (vertex == start) Pair(0, null) else Pair(Int.MAX_VALUE, null)
        }

        // Algorithm
        var currentVertex = start
        var nextVertex: Vertex? = null // determines the next vertex to use based on road weight
        val unvisitedVertices: MutableList<Vertex> = graph.toMutableList()

        // repeat algorithm until each vertex has been visited
        while (unvisitedVertices.isNotEmpty()) {
            var minWeight = Int.MAX_VALUE // used for setting nextVertex
            val neighbors = currentVertex.connectingRoads.filter { (_, road) ->
                carHeight <= road.heightLimit
            }
            for ((neighbor, road) in neighbors) {
                // currentRouteWeight + roadWeight
                val distance = (visitedVertices[currentVertex]?.first ?: 0) + road.weight
                // if newWeight < oldWeight
                if (distance < (visitedVertices[neighbor]?.first ?: 0)) {
                    visitedVertices[neighbor] = Pair(distance, currentVertex)
                }
                // visitedVertices[neighbor] = if (distance < (visitedVertices[neighbor]?.first ?: 0)) Pair(distance, currentVertex)
                if (distance < minWeight) {
                    nextVertex = neighbor
                    minWeight = distance
                }
            }
            unvisitedVertices.remove(currentVertex)
            if (nextVertex != null) {
                currentVertex = nextVertex
            }
        }
        return visitedVertices[destination]?.first ?: -1
    }

    /**
     * Calculates the exact route a vehicle should take from it' current location to the destination.
     * Returns a list of vertices.
     * In case there are multiple shortest routes, the route with lower road ID's is chosen
     * @param vehicle The vehicle to calculate the route for, contains location
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
             * if all the vertexes are explored it returns null and the loop breaks
             * otherwise it assigns this vertex as the current one
             */
            val currentVertex = unvisitedVertices.poll() ?: break

            if (currentVertex == destination) {
                // found the shortest path to the end vertex
                route = buildRoute(destination, previousVertices)
            }
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
        return route
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
     * @param vehicle The vehicle to calculate the route for, contains location
     * @param emergency The emergency to use as a destination. Has a pair of vertices as location
     */
    public fun calculateBestRoute(vehicle: Vehicle, emergency: Emergency) {
        TODO("Unimplemented method")
    }

    /**
     * Finds and returns the closest relevant base for an emergency
     * @param emergency The emergency to find a base for
     * @param bases List of all bases of the correct base type
     */
    public fun findClosestBase(emergency: Emergency, bases: List<Base>): Base {
        // Create mapping of base to it's distance to the emergency
        val distanceToEmergency: MutableMap<Base, Int> = mutableMapOf()
        for (base in bases) {
            // Should this method take all bases or only bases of certain type?
            /* val firstDistance = calculateShortestPath(baseToVertex[base.baseID], emergency.location.first)
            val secondDistance = calculateShortestPath(baseToVertex[base.baseID], emergency.location.second)
            distanceToEmergency[base] = min(firstDistance, secondDistance) */
        }
        TODO("Return the base with the shortest distance in the mapping")
    }

    /**
     * Returns a list of bases responsible for a certain emergency type sorted by proximity to the provided base
     * @param emergency The type of base to return
     * @param base The base to create the list for
     */
    public fun findClosestBasesByProximity(emergency: Emergency, base: Base): List<Base> {
        TODO("Unimplemented method")
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
