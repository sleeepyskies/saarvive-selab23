package de.unisaarland.cs.se.selab.graph

import de.unisaarland.cs.se.selab.dataClasses.Base
import de.unisaarland.cs.se.selab.dataClasses.Vehicle
import de.unisaarland.cs.se.selab.dataClasses.emergencies.Emergency
import de.unisaarland.cs.se.selab.dataClasses.events.Event


class Graph (private val graph: List<Vertex>){

    /**
     * Returns the shortest time in ticks needed to travel from start the vertex
     * to the destination vertex
     * @param start the start-point of the algorithm
     * @param destination the end-point of the algorithm
     * @param carHeight the car's height, set to 0 when ignoring height restrictions
     */
    fun calculateShortestPath (start: Vertex, destination: Vertex, carHeight: Int): Int {

        // creates a new mapping of all vertices from graph to MAX_VALUE, the start vertex is set to 0
        val unvisitedVertices : MutableMap<Vertex, Pair<Int, Vertex?>> = mutableMapOf()
        for (vertex in graph) {
            if (vertex == start) {
                unvisitedVertices[vertex] = Pair(0, start)
            } else {
                unvisitedVertices[vertex] = Pair(Int.MAX_VALUE, null)
            }
        }

        // algorithm
        var currentVertex = start
        var visitedVertices : MutableList<Vertex> = mutableListOf()

        // repeat algorithm until destination vertex has been reached
        while (currentVertex != destination) {

        }

        // once destination vertex has been reached, return its value
        return unvisitedVertices[destination]?.first ?: -1

    }

    // Method to calculate the shortest route for a vehicle to a destination, returns a list of Vertices
    fun calculateShortestRoute(vehicle: Vehicle, destination: Vertex): List<Vertex> {
        return emptyList()
    }

    // Method to calculate the best route for a vehicle to emergency
    fun calculateBestRoute(vehicle: Vehicle, emergency: Emergency) : Unit {
    }

    // Method to find the closest base to an emergency
    fun findClosestBase(emergency: Emergency): Base? {
        // TODO Implement logic to find the closest base to the emergency
        // TODO need to adjust the method signature and return type as needed
        return null
    }

    // Method to find a list of closest bases by proximity to an emergency and a given base
    fun findClosestBasesByProximity(emergency: Emergency, base: Base): List<Base> {
        // TODO Implement logic to find the closest bases to the emergency considering a specific base
        return emptyList()
    }

    // Method to apply an event to the graph
    fun applyGraphEvent(event: Event) {
        // TODO Implement logic to apply the event to the graph ;this method modifies the graph structure
    }

    // Method to revert an event from the graph
    fun revertGraphEvent(event: Event) {
        // TODO Implement logic to revert the effects of an event from the graph
    }

}
