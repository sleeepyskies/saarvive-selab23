package de.unisaarland.cs.se.selab.graph

import de.unisaarland.cs.se.selab.dataClasses.bases.Base
import de.unisaarland.cs.se.selab.dataClasses.bases.FireStation
import de.unisaarland.cs.se.selab.dataClasses.bases.Hospital
import de.unisaarland.cs.se.selab.dataClasses.bases.PoliceStation
import de.unisaarland.cs.se.selab.dataClasses.emergencies.EmergencyType
import de.unisaarland.cs.se.selab.dataClasses.events.Construction
import de.unisaarland.cs.se.selab.dataClasses.events.Event
import de.unisaarland.cs.se.selab.dataClasses.events.RoadClosure
import de.unisaarland.cs.se.selab.dataClasses.events.RushHour
import de.unisaarland.cs.se.selab.dataClasses.events.TrafficJam
import de.unisaarland.cs.se.selab.global.Log
import de.unisaarland.cs.se.selab.global.Number
import de.unisaarland.cs.se.selab.global.StringLiterals
import java.util.concurrent.TimeoutException

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
        return helper.weightToTicks(weightOfRoute(start, destination, carHeight))
    }

    /**
     * Returns the weight of the shortest route from the start to destination
     * @param start the start-point of the algorithm
     * @param destination the end-point of the algorithm
     * @param carHeight the car's height, set to 0 when ignoring height restrictions
     */
    fun weightOfRoute(start: Vertex, destination: Vertex, carHeight: Int): Int {
        val visitedVertices = dijkstra(start, destination, carHeight)
        return visitedVertices[destination]?.first ?: 0
    }

    /**
     * Calculates the exact route a vehicle should take from it' current location to the destination.
     * Returns a list of vertices.
     * In case there are multiple shortest routes, the route with lower road ID's is chosen
     * @param start The last visited vertex of the vehicle
     * @param destination The destination vertex to drive to
     * @param carHeight The height of the vehicle driving
     */

    fun calculateShortestRoute(start: Vertex, destination: Vertex, carHeight: Int): List<Vertex> {
        val visitedVertices = dijkstra(start, destination, carHeight)

        val route = mutableListOf<Vertex>()
        var currentVertex1 = destination
        var previousVertex = visitedVertices[destination]?.second
        // check if the start vertex is reached
        while (previousVertex != null) {
            route.add(currentVertex1)
            currentVertex1 = previousVertex
            previousVertex = visitedVertices[currentVertex1]?.second
        }

        // include the start vertex
        route.add(start)
        return route.reversed().toMutableList()
    }

    private fun dijkstra(start: Vertex, destination: Vertex, carHeight: Int): MutableMap<Vertex, Pair<Int, Vertex?>> {
        var timeout = 0
        if (start == destination) return mutableMapOf(Pair(start, Pair(0, null)))
        // Map from a vertex to its distance form start, and previous vertex on path
        val visitedVertices: MutableMap<Vertex, Pair<Int, Vertex?>> = helper.initVisitedVertices(start, this.graph)
        val unvisitedVertices: MutableList<Vertex> = graph.toMutableList()
        var currentVertex = start

        // Algorithm
        while (unvisitedVertices.isNotEmpty()) {
            if (timeout > 50) throw TimeoutException("Dijkstra has looped over 50 times")
            if (currentVertex == destination) break
            // gets all relevant neighbors based on height restrictions
            val neighbors = currentVertex.connectingRoads.filter { (_, road) -> carHeight <= road.heightLimit }
            // updates neighbor distances
            helper.updateNeighbors(neighbors, visitedVertices, currentVertex, this.graph)

            unvisitedVertices.remove(currentVertex)
            // update nextVertex
            val nextVertex =
                helper.findNextVertex(
                    neighbors,
                    visitedVertices,
                    this.graph,
                    unvisitedVertices,
                    carHeight
                )
            if (nextVertex != null) {
                currentVertex = nextVertex
            }
            timeout++
        }
        return visitedVertices
    }

//    /**
//     * Finds and returns the closest relevant base for an emergency
//     * @param emergency The emergency to find a base for
//     * @param bases List of all bases of the correct base type
//     * @param baseToVertex A mapping of each base to it's vertex
//     */
//    fun findClosestBase(emergency: Emergency, bases: List<Base>, baseToVertex: MutableMap<Int, Vertex>): Base? {
//        // Filter bases by emergency type
//        val relevantBases = filterByEmergencyType(bases.toMutableList(), emergency)
//        assert(relevantBases.isNotEmpty())
//        // Create mapping of base to it's distance to the emergency
//        val distanceToEmergency: MutableMap<Base, Int> = mutableMapOf()
//        for (base in relevantBases) {
//            val firstDistance =
//                baseToVertex[base.baseID]?.let { calculateShortestPath(it, emergency.location.first, 0) }
//            val secondDistance =
//                baseToVertex[base.baseID]?.let { calculateShortestPath(it, emergency.location.second, 0) }
//            distanceToEmergency[base] = min(firstDistance ?: 0, secondDistance ?: 0)
//        }
//
//        var minDistance = Int.MAX_VALUE
//        var closestBase: Base? = null
//        for ((base, distance) in distanceToEmergency) {
//            if (distance < minDistance) {
//                minDistance = distance
//                closestBase = base
//            }
//            if (distance == minDistance) {
//                closestBase = if (base.baseID < (closestBase?.baseID ?: Int.MAX_VALUE)) base else closestBase
//            }
//        }
//        return closestBase
//    }

    /**
     * Filters the given list of bases according to the emergency.
     */
    private fun filterByEmergencyType(bases: MutableList<Base>, emergencyType: EmergencyType): MutableList<Base> {
        val relevantBases = mutableListOf<Base>()
        relevantBases.addAll(bases)
        for (base in bases) {
            when (Pair(emergencyType, getStringType(base))) {
                Pair(EmergencyType.FIRE, StringLiterals.FIRESTATION) -> Unit
                Pair(EmergencyType.CRIME, StringLiterals.POLICESTATION) -> Unit
                Pair(EmergencyType.MEDICAL, StringLiterals.HOSPITAL) -> Unit
                Pair(EmergencyType.ACCIDENT, StringLiterals.FIRESTATION) -> Unit
                else -> relevantBases.remove(base)
            }
        }
        return relevantBases
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
     * @param emergencyType The type of base to return
     * @param startBase The base to create the list for
     * @param bases A list of all bases in the simulation
     * @param baseToVertex A mapping of each base to it's vertex
     */
    fun findClosestBasesByProximity(
        emergencyType: EmergencyType,
        startBase: Base,
        bases: List<Base>,
        baseToVertex: MutableMap<Int, Vertex>
    ): List<Base> {
        val relevantBases = filterByEmergencyType(bases.toMutableList(), emergencyType)
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
//            if (road.pType in event.roadType) road.weight *= event.factor
//            road.activeEvents.add(event)
            // quick fix : ensuring events are applied in order on the road
            if (road.pType in event.roadType) {
                // prevent duplication of events during checks
                if (event !in road.activeEvents) road.activeEvents.add(event)

                // apply the first event
                // in its own class the event is checked if it's already active
                // if it isn't active it's applied
                if (event != road.activeEvents.first()) {
                    applyGraphEvent(road.activeEvents.first())
                    // move to the next road
                    continue
                }

                if (road !in event.roadAppliedList) {
                    road.weight *= event.factor
                    // adds it to road list this is applied to
                    // keeps track of which events the roads are applied to
                    event.roadAppliedList.add(road)
                }

                // Log as soon as the event is applied to first road
                if (event.roadAppliedList.size == 1) {
                    Log.displayEventStarted(event.eventID)
                }
            }
        }
    }

    private fun applyConstruction(event: Construction) {
        val startVertex = graph.find { it.id == event.sourceID } ?: ver
        val targetVertex = graph.find { it.id == event.targetID } ?: ver
        // puts the required road into the event
        event.affectedRoad = startVertex.connectingRoads[targetVertex.id] ?: r

        // prevents duplication of events in the list of active events
        if (event !in event.affectedRoad.activeEvents) {
            event.affectedRoad.activeEvents.add(event)
        }

        // if it isn't first in the queue ignore and apply the event that is first

        if (event != event.affectedRoad.activeEvents.first()) {
            applyGraphEvent(event.affectedRoad.activeEvents.first())
            return
        }
        // if the event hasn't been applied
        if (!event.isApplied) {
            // the factor is applied on the affected road
            event.affectedRoad.weight *= event.factor
            // check and change the road into a one way
            if (event.oneWayStreet) targetVertex.connectingRoads.remove(startVertex.id)
            // show that the event is applied
            event.isApplied = true
            // logging
            Log.displayEventStarted(event.eventID)
        }
    }
    private fun applyTrafficJam(event: TrafficJam) {
        val startVertex = graph.find { it.id == event.startVertex } ?: ver
        val targetVertex = graph.find { it.id == event.endVertex } ?: ver

        val requiredRoad = startVertex.connectingRoads[targetVertex.id] ?: r

        // prevents duplication during check
        if (event !in requiredRoad.activeEvents) requiredRoad.activeEvents.add(event)

        if (event != requiredRoad.activeEvents.first()) {
            // apply the first event
            // it's class will check if it is already applied
            applyGraphEvent(requiredRoad.activeEvents.first())
            // no need to apply this event anymore
            return
        }

        if (!event.isApplied) {
            event.affectedRoad = requiredRoad
            requiredRoad.weight *= event.factor
            event.isApplied = true
            Log.displayEventStarted(event.eventID)
        }
    }

    private fun applyRoadClosure(event: RoadClosure) {
        // find source vertex
        val sourceVertex = graph.find { it.id == event.sourceID } ?: ver
        // find target vertex
        val targetVertex = graph.find { it.id == event.targetID } ?: ver
        // find required road
        val requiredRoad = targetVertex.connectingRoads[sourceVertex.id] ?: r
        // puts the road into the event
        event.affectedRoad = requiredRoad

        // add this event to the list of event if it isn't present
        if (event !in requiredRoad.activeEvents) requiredRoad.activeEvents.add(event)

        if (event != requiredRoad.activeEvents.first()) {
            applyGraphEvent(requiredRoad.activeEvents.first())
            return
        }

        if (!event.isApplied) {
            // remove the road from the graph
            targetVertex.connectingRoads.remove(sourceVertex.id)
            sourceVertex.connectingRoads.remove(targetVertex.id)
            event.isApplied = true
            Log.displayEventStarted(event.eventID)
        }
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
            if (road == event.affectedRoad) {
                road.weight /= if (road.activeEvents[0] == event) event.factor else 1
                if (road.sType != SecondaryType.ONE_WAY_STREET && event.oneWayStreet) {
                    addRoadToMap(event)
                    break
                }
                event.affectedRoad.activeEvents.remove(event)
                // if the event queue for this road is not empty apply the event
                if (event.affectedRoad.activeEvents.isNotEmpty()) {
                    applyGraphEvent(event.affectedRoad.activeEvents.first())
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
        targetVertex.connectingRoads[sourceVertex.id] = event.affectedRoad
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
        event.affectedRoad.activeEvents.remove(event)
        // if the event queue for this road is not empty apply the event
        if (event.affectedRoad.activeEvents.isNotEmpty()) {
            applyGraphEvent(event.affectedRoad.activeEvents.first())
        }
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

                // if the event queue for this road is not empty apply the event at the start of the queue
                if (road.activeEvents.isNotEmpty()) {
                    applyGraphEvent(road.activeEvents.first())
                }
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
                // if the event queue for this road is not empty apply the event
                if (road.activeEvents.isNotEmpty()) {
                    applyGraphEvent(road.activeEvents.first())
                }
                return
            }
        }
    }
}
