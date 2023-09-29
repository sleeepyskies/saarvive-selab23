package de.unisaarland.cs.se.selab.graph

import de.unisaarland.cs.se.selab.dataClasses.bases.Base
import de.unisaarland.cs.se.selab.dataClasses.bases.FireStation
import de.unisaarland.cs.se.selab.dataClasses.bases.Hospital
import de.unisaarland.cs.se.selab.dataClasses.bases.PoliceStation
import de.unisaarland.cs.se.selab.dataClasses.emergencies.Emergency
import de.unisaarland.cs.se.selab.dataClasses.emergencies.EmergencyType
import de.unisaarland.cs.se.selab.dataClasses.events.Construction
import de.unisaarland.cs.se.selab.dataClasses.events.Event
import de.unisaarland.cs.se.selab.dataClasses.events.RoadClosure
import de.unisaarland.cs.se.selab.dataClasses.events.RushHour
import de.unisaarland.cs.se.selab.dataClasses.events.TrafficJam
import de.unisaarland.cs.se.selab.global.StringLiterals
import java.lang.Integer.min
import java.util.*

/**
 * Holds the data for the simulation graph consisting of vertices and roads.
 * @param graph A list of vertices containing connecting roads
 * @param roads A list of all the roads in the graph
 */
class Graph(val graph: List<Vertex>, val roads: List<Road>) {
    private val helper = GraphHelper()

    // extra objects used to resolve nullability issues
    // private val v = Vehicle(VehicleType.FIREFIGHTER_TRANSPORTER, 1, 1, 1, 1)
    // private val b = Base(1, 1, 1, listOf(v))
    private val r = Road(PrimaryType.COUNTY_ROAD, SecondaryType.NONE, "s", "u", 1, 1)
    private val ver = Vertex(1, mutableMapOf<Int, Road>())

    /**
     * Returns the shortest time in ticks needed to travel from start the vertex
     * to the destination vertex
     * @param start the start-point of the algorithm
     * @param destination the end-point of the algorithm
     * @param carHeight the car's height, set to 0 when ignoring height restrictions
     */
    fun calculateShortestPath(start: Vertex, destination: Vertex, carHeight: Int): Int {
        val visitedVertices: MutableMap<Vertex, Pair<Int, Vertex?>> = helper.initVisitedVertices(start, this.graph)
        val unvisitedVertices: MutableList<Vertex> = graph.toMutableList()
        var currentVertex = start

        // Algorithm
        while (unvisitedVertices.isNotEmpty()) {
            // gets all relevant neighbors based on height restrictions
            val neighbors = currentVertex.connectingRoads.filter { (_, road) -> carHeight <= road.heightLimit }
            // updates neighbor distances
            helper.updateNeighbors(neighbors, visitedVertices, currentVertex)

            unvisitedVertices.remove(currentVertex)
            // update nextVertex
            val nextVertex = helper.findNextVertex(neighbors, visitedVertices)
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
     * @param vehiclePosition The last visited vertex of the vehicle
     * @param destination The destination vertex to drive to
     * @param vehicleHeight The height of the vehicle driving
     */
    fun calculateShortestRoute(vehiclePosition: Vertex, destination: Vertex, vehicleHeight: Int): MutableList<Vertex> {
        var route = emptyList<Vertex>()
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
        val unvisitedVertices = PriorityQueue<Vertex> { v1, v2 -> (distances[v1] ?: 0) - (distances[v2] ?: 0) }

        // initializing distances and previous vertices for all vertices in the graph
        for (vertex in graph) {
            distances[vertex] = if (vertex == vehiclePosition) 0 else Int.MAX_VALUE
            previousVertices[vertex] = null
            unvisitedVertices.offer(vertex)
        }

        // dijkstra's algorithm using the above structure
        while (unvisitedVertices.isNotEmpty()) {
            /**
             * .poll() finds the next vertex in the queue in the order of the lambda expression
             */
            val currentVertex = unvisitedVertices.poll()

            if (currentVertex == destination) {
                // found the shortest path to the end vertex
                route = helper.buildRoute(destination, previousVertices)
            }

            // traverse connected vertices
            helper.exploreNeighbours(currentVertex, distances, previousVertices, vehicleHeight, graph)
        }
        return route.toMutableList()
    }

    /**
     * Finds and returns the closest relevant base for an emergency
     * @param emergency The emergency to find a base for
     * @param bases List of all bases of the correct base type
     * @param baseToVertex A mapping of each base to it's vertex
     */
    fun findClosestBase(emergency: Emergency, bases: List<Base>, baseToVertex: MutableMap<Int, Vertex>): Base? {
        // Filter bases by emergency type
        val relevantBases = filterByEmergencyType(bases.toMutableList(), emergency)
        assert(relevantBases.isNotEmpty())
        // Create mapping of base to it's distance to the emergency
        val distanceToEmergency: MutableMap<Base, Int> = mutableMapOf()
        for (base in relevantBases) {
            val firstDistance =
                baseToVertex[base.baseID]?.let { calculateShortestPath(it, emergency.location.first, 0) }
            val secondDistance =
                baseToVertex[base.baseID]?.let { calculateShortestPath(it, emergency.location.second, 0) }
            distanceToEmergency[base] = min(firstDistance ?: 0, secondDistance ?: 0)
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
     * Filters the given list of bases according to the emergency.
     */
    private fun filterByEmergencyType(bases: MutableList<Base>, emergency: Emergency): MutableList<Base> {
        for (base in bases) {
            when (Pair(emergency.emergencyType, getStringType(base))) {
                Pair(EmergencyType.FIRE, StringLiterals.FIRESTATION) -> Unit
                Pair(EmergencyType.CRIME, StringLiterals.POLICESTATION) -> Unit
                Pair(EmergencyType.MEDICAL, StringLiterals.HOSPITAL) -> Unit
                Pair(EmergencyType.ACCIDENT, StringLiterals.FIRESTATION) -> Unit
                else -> bases.remove(base)
            }
        }
        return bases
    }

    /**
     * Returns the type of base as a string
     */
    private fun getStringType(base: Base): String {
        when (base) {
            is FireStation -> return StringLiterals.FIRESTATION
            is Hospital -> return StringLiterals.HOSPITAL
            is PoliceStation -> return StringLiterals.POLICESTATION
        }
        return ""
    }

    /**
     * Returns a list of bases responsible for a certain emergency type sorted by proximity to the provided base
     * @param emergency The type of base to return
     * @param startBase The base to create the list for
     * @param bases A list of all bases in the simulation
     * @param baseToVertex A mapping of each base to it's vertex
     */
    fun findClosestBasesByProximity(
        emergency: Emergency,
        startBase: Base,
        bases: List<Base>,
        baseToVertex: MutableMap<Int, Vertex>
    ): List<Base> {
        val relevantBases = filterByEmergencyType(bases.toMutableList(), emergency)
        // stores the distance of each base from the start base
        val distanceMapping = mutableMapOf<Base, Int>()
        val startBaseVertex = baseToVertex[startBase.baseID] ?: ver

        for (nextBase in relevantBases) {
            // ignore the start base
            if (nextBase == startBase) continue
            val nextBaseVertex = baseToVertex[nextBase.baseID] ?: ver
            // get the shortest distance from the start base
            distanceMapping[nextBase] = calculateShortestPath(startBaseVertex, nextBaseVertex, 0)
        }

        return distanceMapping.entries.sortedBy { it.value }.map { it.key }
    }

    /**
     * Applies the effect of the given graph event to the graph
     * @param event The event to apply the effects of
     */
    fun applyGraphEvent(event: Event) {
        // applyEvent(event)
        when (event) {
            is RushHour -> applyRushHour(event)
            is TrafficJam -> applyTrafficJam(event)
            is RoadClosure -> applyRoadClosure(event)
            is Construction -> applyConstruction(event)
        }
    }
    private fun applyRushHour(event: RushHour) {
        for (road in roads) {
            if (road.pType in event.roadType) road.weight *= event.factor
            road.activeEvents.add(event)
        }
    }

    private fun applyConstruction(event: Construction) {
        val startVertex = graph.find { it.id == event.sourceID } ?: ver
        val targetVertex = graph.find { it.id == event.targetID } ?: ver
        // puts the required into the event
        event.affectedRoad = startVertex.connectingRoads[targetVertex.id] ?: r
        // the factor is applied on the affected road
        event.affectedRoad.weight *= event.factor
        // check and change the road into a one way
        if (event.streetClosed) startVertex.connectingRoads.remove(targetVertex.id)
    }
    private fun applyTrafficJam(event: TrafficJam) {
        val startVertex = graph.find { it.id == event.startVertex } ?: ver
        val targetVertex = graph.find { it.id == event.endVertex } ?: ver

        val requiredRoad = startVertex.connectingRoads[targetVertex.id] ?: r
        requiredRoad.weight *= event.factor
        requiredRoad.activeEvents.add(event)

        event.affectedRoad = requiredRoad
    }

    private fun applyRoadClosure(event: RoadClosure) {
        // find source vertex
        val sourceVertex = graph.find { vertex: Vertex -> vertex.id == event.sourceID } ?: ver
        // find target vertex
        val targetVertex = graph.find { vertex: Vertex -> vertex.id == event.targetID } ?: ver
        // find required road
        val requiredRoad = targetVertex.connectingRoads[sourceVertex.id] ?: r
        // puts the road into the event
        event.affectedRoad = requiredRoad
        // remove the road from the graph
        targetVertex.connectingRoads.remove(sourceVertex.id)
        sourceVertex.connectingRoads.remove(targetVertex.id)
        // add this event to the list of event
        requiredRoad.activeEvents.add(event)
    }

    /**
     *
     */
    fun revertGraphEvent(event: Event) {
        when (event) {
            is Construction -> revertConstruction(event)
            is RoadClosure -> revertRoadClosure(event)
            is RushHour -> revertRushHour(event)
            is TrafficJam -> revertTrafficJam(event)
        }
    }

    /**
     * Reverts the effect of a construction event on the map
     */
    private fun revertConstruction(event: Construction) {
        for (road in roads) {
            // TODO does not account for village name
            if (road == event.affectedRoad) {
                road.weight /= if (road.activeEvents[0] == event) event.factor else 1
                if (road.sType != SecondaryType.ONE_WAY_STREET && event.streetClosed) {
                    addRoadToMap(event)
                    break
                }
            }
        }
    }

    /**
     * Adds a road back to the map after a construction event has ended.
     */
    private fun addRoadToMap(event: Construction) {
        // find source vertex
        val sourceVertex = graph.find { vertex: Vertex -> vertex.id == event.sourceID } ?: ver
        // find target vertex
        val targetVertex = graph.find { vertex: Vertex -> vertex.id == event.targetID } ?: ver

        // add road back to the map
        sourceVertex.connectingRoads[targetVertex.id] = event.affectedRoad
    }

    /**
     * Reverts the effect of a road closure event on the map
     */
    private fun revertRoadClosure(event: RoadClosure) {
        // find source vertex
        val sourceVertex = graph.find { vertex: Vertex -> vertex.id == event.sourceID } ?: ver
        // find target vertex
        val targetVertex = graph.find { vertex: Vertex -> vertex.id == event.targetID } ?: ver

        // add road back to the map
        targetVertex.connectingRoads[sourceVertex.id] = event.affectedRoad
        sourceVertex.connectingRoads[targetVertex.id] = event.affectedRoad
    }

    /**
     * Reverts the effect of a rush hour event on the map
     */
    private fun revertRushHour(event: RushHour) {
        // get all affected road types
        val roadTypes = event.roadType
        // iterate over roads
        for (road in roads) {
            if (roadTypes.contains(road.pType)) {
                // only revert effect if event is front of list
                road.weight /= if (road.activeEvents[0] == event) event.factor else 1
                road.activeEvents.remove(event)
            }
        }
    }

    /**
     * Reverts the effect of a traffic jam event on the map
     */
    private fun revertTrafficJam(event: TrafficJam) {
        for (road in roads) {
            // find affected road
            if (road == event.affectedRoad) {
                road.weight /= if (road.activeEvents[0] == event) event.factor else 1
                road.activeEvents.remove(event)
                return
            }
        }
    }
}
