package de.unisaarland.cs.se.selab.graph

import de.unisaarland.cs.se.selab.global.Number

/**
 * A class containing helper methods
 * for Graph to reduce amount of methods in one class
 */
class GraphHelper {
    // calculateShortestPath helper methods
    /**
     * Creates a mapping of each vertex in the graph to it's distance to the start vertex
     * and the previous vertex on the path. Distance initialised to Int.MAX_VALUE, previous vertex
     * initialised to null.
     */
    fun initVisitedVertices(start: Vertex, graph: List<Vertex>): MutableMap<Vertex, Pair<Int, Vertex?>> {
        val visitedVertices: MutableMap<Vertex, Pair<Int, Vertex?>> = mutableMapOf()
        for (vertex in graph) {
            visitedVertices[vertex] = if (vertex == start) Pair(0, null) else Pair(Int.MAX_VALUE, null)
        }
        return visitedVertices
    }

    /**
     * Updates the distance for each neighbor and adds currentVertex as the previous vertex for each neighbor.
     */
    fun updateNeighbors(
        neighbors: Map<Vertex, Road>,
        visitedVertices: MutableMap<
            Vertex,
            Pair<Int, Vertex?>
            >,
        currentVertex: Vertex
    ) {
        for ((neighbor, road) in neighbors) {
            // currentRouteWeight + roadWeight
            val distance = (visitedVertices[currentVertex]?.first ?: 0) + weightToTicks(road.weight)
            // if newWeight < oldWeight
            if (distance < (visitedVertices[neighbor]?.first ?: 0)) {
                visitedVertices[neighbor] = Pair(distance, currentVertex)
            }
        }
    }

    /**
     * Returns the weight as ticks need to travel
     */
    private fun weightToTicks(weight: Int): Int {
        if (weight < Number.TEN) return 1
        return if (weight % Number.TEN == 0) {
            weight // number is already a multiple of ten
        } else {
            weight + (Number.TEN - weight % Number.TEN) // round up
        }
    }

    /**
     * Finds the next vertex to be used in Dijkstra's algorithm. Chooses the vertex connected
     * to the road with the smallest weight.
     */
    fun findNextVertex(
        neighbors: Map<Vertex, Road>,
        visitedVertices: Map<Vertex, Pair<Int, Vertex?>>
    ): Vertex? {
        var nextVertex: Vertex? = null
        var minWeight = Int.MAX_VALUE

        for ((neighbor, _) in neighbors) {
            val distance = visitedVertices[neighbor]?.first ?: 0
            if (distance < minWeight) {
                minWeight = distance
                nextVertex = neighbor
            }
        }

        return nextVertex
    }

    // calculateShortestRoute helper methods
    /**
     * used within the calculateShortestRoute method to explore all the connected vertices
     * and update the mappings when shortest vertex is found
     */
    fun exploreNeighbours(
        currentVertex: Vertex,
        distances: MutableMap<Vertex, Int>,
        previousVertices: MutableMap<Vertex, Vertex?>,
        vehicleHeight: Int,
        vertices: List<Vertex>
    ) {
        for ((neighborVertexID, connectingRoad) in currentVertex.connectingRoads) {
            // check if the cars height allows it to drive on the road
            if (vehicleHeight > connectingRoad.heightLimit) {
                continue
            }
            // get the actual vertex
            val neighborVertex = vertices.first { it.id == neighborVertexID }
            // calculate the weight of the route up till the neighbouring vertex
            val tentativeDistance = (distances[currentVertex] ?: 0) + connectingRoad.weight
            // check if the route through this neighbour is shorter than the previous found route
            if (tentativeDistance < (distances[neighborVertex] ?: 0)) {
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
    fun buildRoute(endVertex: Vertex, previousVertices: Map<Vertex, Vertex?>): MutableList<Vertex> {
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
        return route.reversed().toMutableList()
    }
}
