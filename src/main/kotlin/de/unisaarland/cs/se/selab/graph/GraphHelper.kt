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
        neighbors: Map<Int, Road>,
        visitedVertices: MutableMap<
            Vertex,
            Pair<Int, Vertex?>
            >,
        currentVertex: Vertex,
        graph: List<Vertex>
    ) {
        for ((neighbor, road) in neighbors) {
            // currentRouteWeight + roadWeight
            val distance = (visitedVertices[currentVertex]?.first ?: 0) + road.weight
            // if newWeight < oldWeight
            if (distance < (visitedVertices[graph.find { vertex: Vertex -> vertex.id == neighbor }]?.first ?: 0)) {
                visitedVertices[
                    graph.find { vertex: Vertex ->
                        vertex.id == neighbor
                    } ?: Vertex(0, mutableMapOf())
                ] = Pair(distance, currentVertex)
            }
        }
    }

    /**
     * Returns the weight as ticks need to travel
     */
    fun weightToTicks(weight: Int): Int {
        if (weight < Number.TEN) return 1
        return if (weight % Number.TEN == 0) {
            weight / Number.TEN // number is already a multiple of ten
        } else {
            (weight + (Number.TEN - weight % Number.TEN)) / Number.TEN // round up
        }
    }

    /**
     * Finds the next vertex to be used in Dijkstra's algorithm. Chooses the vertex connected
     * to the road with the smallest weight.
     */
    fun findNextVertex(
        neighbors: Map<Int, Road>,
        visitedVertices: Map<Vertex, Pair<Int, Vertex?>>,
        graph: List<Vertex>,
        unvisitedVertices: List<Vertex>,
        carHeight: Int,
    ): Vertex? {
        var nextVertex: Vertex? = null
        var minWeight = Int.MAX_VALUE

        // find the closest direct and unvisited neighbor
        for ((neighbor, _) in neighbors) {
            val distance = visitedVertices[graph.find { vertex: Vertex -> vertex.id == neighbor }]?.first ?: 0
            if (distance < minWeight && unvisitedVertices.contains(
                    graph.find { vertex: Vertex -> vertex.id == neighbor }
                )
            ) {
                minWeight = distance
                nextVertex = graph.find { vertex: Vertex -> vertex.id == neighbor }
            }
        }
        // only return nextVertex if not null
        if (nextVertex != null) return nextVertex

        // find visited vertex, that has at least one unvisited vertex neighbor
        // a list of all visited vertices
        val visited = graph.filter { vertex -> !unvisitedVertices.contains(vertex) }
        // a list of all unvisited vertex ids
        val unvisitedIDs = unvisitedVertices.map { it.id }
        // find a visited vertex that has at least one unvisited neighbor
        nextVertex = condition(visited, unvisitedIDs, carHeight)
        return nextVertex
    }

    /**
     * Returns a visited vertex that has at least one reachable and unvisited neighbor
     */
    private fun condition(vertexList: List<Vertex>, idList: List<Int>, carHeight: Int): Vertex? {
        for (vertex in vertexList) {
            for (id in idList) {
                if (
                    vertex.connectingRoads.keys.contains(id) &&
                    carHeight <= (vertex.connectingRoads[id]?.heightLimit ?: 0)
                ) {
                    return vertex
                }
            }
        }
        return null
    }
}
