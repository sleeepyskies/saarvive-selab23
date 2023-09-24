package de.unisaarland.cs.se.selab.graph

import de.unisaarland.cs.se.selab.basesAndAssets.Base
import de.unisaarland.cs.se.selab.basesAndAssets.Vehicle
import de.unisaarland.cs.se.selab.emergencies.Emergency
import de.unisaarland.cs.se.selab.events.Event


class Graph (private val graph: List<Vertex>){
    // Method that calculates shortest path from start to destination for a vehicle, considering its height
    fun calculateShortestPath (start: Vertex, destination: Vertex, carHeight: Int):Int {
        // Dijkstra algorithm goes here
        return 0
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
