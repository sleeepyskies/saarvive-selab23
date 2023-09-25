package de.unisaarland.cs.se.selab.graph

import de.unisaarland.cs.se.selab.dataClasses.Base
import de.unisaarland.cs.se.selab.dataClasses.Vehicle
import de.unisaarland.cs.se.selab.dataClasses.emergencies.Emergency
import de.unisaarland.cs.se.selab.dataClasses.events.Event

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
    fun calculateShortestPath(start: Vertex, destination: Vertex, carHeight: Int): Int {
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

            val connectionsMap = currentVertex.connectingRoads // gets the connectingRoads Map from vertex
            for ((vertices, roads) in connectionsMap) {
                if (carHeight <= roads.heightLimit) {
                    val distance = (visitedVertices[currentVertex]?.first ?: 0) + roads.weight
                    if (distance < (visitedVertices[vertices]?.first ?: 0)) {
                        visitedVertices[vertices] = Pair(distance, currentVertex)
                    }
                    if (distance < minWeight) {
                        nextVertex = vertices
                        minWeight = distance
                    }
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
     */
    fun calculateShortestRoute(vehicle: Vehicle, destination: Vertex): List<Vertex> {
        TODO("Unimplemented method")
    }

    /**
     * Calculates the best route from a vehicle's location to an emergency vertex.
     * @param vehicle The vehicle to calculate the route for, contains location
     * @param emergency The emergency to use as a destination. Has a pair of vertices as location
     */
    fun calculateBestRoute(vehicle: Vehicle, emergency: Emergency) {
        TODO("Unimplemented method")
    }

    /**
     * Finds and returns the closest relevant base for an emergency
     * @param emergency The emergency to find a base for
     */
    fun findClosestBase(emergency: Emergency): Base {
        TODO("Unimplemented method")
    }

    /**
     * Returns a list of bases responsible for a certain emergency type sorted by proximity to the provided base
     * @param emergency The type of base to return
     * @param base The base to create the list for
     */
    fun findClosestBasesByProximity(emergency: Emergency, base: Base): List<Base> {
        TODO("Unimplemented method")
    }

    /**
     * Applies the effect of the given graph event to the graph
     * @param event The event to apply the effects of
     */
    fun applyGraphEvent(event: Event) {
        TODO("Unimplemented method")
    }

    /**
     * Reverts the effect of a given graph event on the graph
     * @param event The event to revert the effect of
     */
    fun revertGraphEvent(event: Event) {
        TODO("Unimplemented method")
    }
}
