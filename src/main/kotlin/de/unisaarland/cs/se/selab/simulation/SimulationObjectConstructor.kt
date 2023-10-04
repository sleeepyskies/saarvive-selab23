package de.unisaarland.cs.se.selab.simulation

import de.unisaarland.cs.se.selab.dataClasses.bases.Base
import de.unisaarland.cs.se.selab.dataClasses.emergencies.Emergency
import de.unisaarland.cs.se.selab.dataClasses.events.Construction
import de.unisaarland.cs.se.selab.dataClasses.events.Event
import de.unisaarland.cs.se.selab.dataClasses.events.RoadClosure
import de.unisaarland.cs.se.selab.dataClasses.events.RushHour
import de.unisaarland.cs.se.selab.dataClasses.events.TrafficJam
import de.unisaarland.cs.se.selab.dataClasses.events.VehicleUnavailable
import de.unisaarland.cs.se.selab.dataClasses.vehicles.Vehicle
import de.unisaarland.cs.se.selab.graph.Graph
import de.unisaarland.cs.se.selab.graph.Road
import de.unisaarland.cs.se.selab.graph.SecondaryType
import de.unisaarland.cs.se.selab.graph.Vertex
import de.unisaarland.cs.se.selab.parser.AssetParser
import de.unisaarland.cs.se.selab.parser.CountyParser
import de.unisaarland.cs.se.selab.parser.EventsParser
import de.unisaarland.cs.se.selab.parser.SimulationParser
import java.util.*

/**
 * Is responsible for calling the parsers, cross
 * validating and constructing the DataHolder and Simulation
 */
class SimulationObjectConstructor(
    private val countyFile: String,
    private val assetFile: String,
    private val simulationFile: String,
    private val maxTick: Int?,
) {
    /**
     * Creates and returns a parsed and validated Simulation object
     */
    fun createSimulation(): Simulation? {
        val countyParser: CountyParser
        val graph: Graph

        val assetParser: AssetParser
        val bases: List<Base>
        val vehicles: List<Vehicle>

        val simulationParser: SimulationParser
        val eventsParser: EventsParser
        val emergencies: List<Emergency>
        val events: List<Event>
        try {
            // parse, validate and create map
            countyParser = CountyParser(countyFile)
            graph = countyParser.parse()

            // parse, validate and create assets
            assetParser = AssetParser("assets.schema", assetFile)
            val (parsedVehiclesList, parsedBasesList) = assetParser.parse()
            bases = parsedBasesList
            vehicles = parsedVehiclesList

            // parse, validate and create events and emergencies
            simulationParser = SimulationParser("simulation.schema", simulationFile, graph)
            simulationParser.parse()
            emergencies = simulationParser.parsedEmergencies
            eventsParser = EventsParser("simulation.schema", simulationFile, vehicles)
            eventsParser.parse()
            events = eventsParser.parsedEvents
        } catch (_: IllegalArgumentException) {
            return null
        }

        // init vehicles lastVisitedVertex
        for (vehicle in vehicles) {
            val base = bases.find { base: Base -> base.baseID == vehicle.assignedBaseID }
            if (base != null) {
                graph.graph.find { vertex: Vertex -> vertex.id == base.vertexID }.also {
                    if (it != null) {
                        vehicle.lastVisitedVertex = it
                    }
                }
            }
        }

        // cross validation and construction
        return if (
            validateAssetsBasedOnGraph(graph, bases) &&
            validateEmergenciesBasedOnGraph(graph, emergencies) &&
            validateEventsBasedOnGraph(graph, events, vehicles)
        ) {
            // If validation succeeds return simulation
            val dataHolder = DataHolder(graph, bases, events.toMutableList(), emergencies)
            return Simulation(dataHolder, maxTick)
        } else {
            null
        }
    }

    /**
     * Cross validates the assets based on the graph
     */
    private fun validateAssetsBasedOnGraph(graph: Graph, bases: List<Base>): Boolean {
        // Init map of each vertex to a list of bases located on it
        val mapping: MutableMap<Vertex, MutableList<Base>> = mutableMapOf()
        for (vertex in graph.graph) {
            mapping[vertex] = mutableListOf()
        }

        // Check each base exists on the map
        for (base in bases) {
            // find base vertex
            val baseVertex = graph.graph.find { vertex: Vertex -> vertex.id == base.vertexID }
            // add base to mapping
            if (baseVertex != null) {
                // add base to mapping
                mapping[baseVertex]?.add(base)
            } else {
                return false
            }
        }

        // Check each vertex has at most one base on it
        for ((_, baseList) in mapping) {
            if (baseList.size > 1) return false
        }
        return true
    }

    /**
     * Cross validates the events based on the graph
     */
    private fun validateEventsBasedOnGraph(graph: Graph, events: List<Event>, vehicles: List<Vehicle>): Boolean {
        for (event in events) {
            when (event) {
                is VehicleUnavailable -> if (!validateVehicleEvent(event, vehicles)) return false
                is Construction -> if (!validateOneWayStreet(event, graph)) return false
                else -> if (!validateGraphEvent(event, graph)) return false
            }
        }
        return true
    }

    /** Helper method for validateEventsBasedOnGraph(). Validates if oneWay street street does not exist */
    private fun validateOneWayStreet(event: Construction, graph: Graph): Boolean {
        val vertex1 = graph.graph.find { vertex: Vertex -> vertex.id == event.sourceID }
        val vertex2 = graph.graph.find { vertex: Vertex -> vertex.id == event.targetID }
        val road = vertex1?.connectingRoads?.get(vertex2?.id)
        return if (road != null && road.sType == SecondaryType.NONE) {
            event.affectedRoad = road
            event.oneWayStreet = true
            true
        } else if (road != null && road.sType == SecondaryType.ONE_WAY_STREET) {
            event.affectedRoad = road
            event.oneWayStreet = false
            true
        } else {
            false
        }
    }

    /**
     * Helper method for validateEventsBasedOnGraph(). Validates if the events vehicle ID exists
     */
    private fun validateVehicleEvent(event: VehicleUnavailable, vehicles: List<Vehicle>): Boolean {
        val vehicle = vehicles.find { vehicle: Vehicle -> vehicle.id == event.vehicleID }
        return vehicle != null
    }

    /**
     * Helper method for validateEventsBasedOnGraph(). Validates if the roads exist
     */
    private fun validateGraphEvent(event: Event, graph: Graph): Boolean {
        return when (event) {
            is RushHour -> true
            is Construction -> roadExists(event.sourceID, event.targetID, graph)
            is RoadClosure -> roadExists(event.sourceID, event.targetID, graph)
            is TrafficJam -> roadExists(event.startVertex, event.endVertex, graph)
            else -> throw IllegalArgumentException("Unsupported event type: ${event::class.simpleName}")
        }
    }

    /**
     * Checks if the vertices and road exists, as well as checking the vertices are connected via the edge
     */
    private fun roadExists(sourceID: Int, targetID: Int, graph: Graph): Boolean {
        // Find the source and target vertices
        val sourceVertex = graph.graph.find { vertex -> vertex.id == sourceID }
        val targetVertex = graph.graph.find { vertex -> vertex.id == targetID }

        // Check if source and target vertices exist
        if (sourceVertex == null || targetVertex == null) return false

        // Use Breadth First Search to check for connectivity
        val visited = mutableSetOf<Int>()
        val queue = LinkedList<Int>()

        queue.add(sourceID)
        visited.add(sourceID)

        while (queue.isNotEmpty()) {
            val currentVertexID = queue.poll()
            val currentVertex = graph.graph.find { vertex -> vertex.id == currentVertexID }

            // Check if the current vertex is the target vertex
            if (currentVertexID == targetID) return true

            // Explore neighbors (vertices connected by roads)
            currentVertex?.connectingRoads?.keys?.forEach { neighborID ->
                if (neighborID !in visited) {
                    queue.add(neighborID)
                    visited.add(neighborID)
                }
            }
        }

        return false
    }

    /**
     * Checks that the road name and village name correspond to a road in the map
     */
    private fun getRoad(roadName: String, villageName: String, graph: Graph): Road? {
        return graph.roads.find { road: Road -> road.roadName == roadName && road.villageName == villageName }
    }

    /**
     * Cross validates the emergencies based on the graph
     * Needs to validate:
     *      - if check the emergency's road name and village name correspond to a road
     *      - two emergencies don't happen at the same time on the same road
     */
    private fun validateEmergenciesBasedOnGraph(graph: Graph, emergencies: List<Emergency>): Boolean {
        // init mapping
        val mapping: MutableMap<Road, MutableList<Emergency>> = mutableMapOf()
        for (road in graph.roads) {
            mapping[road] = mutableListOf()
        }

        // check each emergency road exists
        for (emergency in emergencies) {
            // get emergency road anc check it exists
            val emergencyRoad = getRoad(emergency.roadName, emergency.villageName, graph)

            // add emergency to the mapping
            if (emergencyRoad != null) {
                // add base to mapping
                mapping[emergencyRoad]?.add(emergency)
            } else {
                return false
            }
        }

        // check two emergencies at the same location do not occur at the same time
        // loop over all emergencyLists
        for ((_, emergencyList) in mapping) {
            if (!checkOverlappingEmergencies(emergencyList)) return false
        }

        return true
    }

    /**
     * Checks that no two emergencies in the given list overlap in their duration
     */
    private fun checkOverlappingEmergencies(emergencyList: List<Emergency>): Boolean {
        for (emergencyOne in emergencyList) {
            for (emergencyTwo in emergencyList) {
                if (emergencyOne != emergencyTwo && isInRange(emergencyOne, emergencyTwo)) {
                    return false
                }
            }
        }
        return true
    }

    private fun isInRange(emergencyOne: Emergency, emergencyTwo: Emergency): Boolean {
        return emergencyTwo.startTick >= emergencyOne.startTick &&
            emergencyTwo.startTick < emergencyOne.startTick + emergencyOne.handleTime
    }
}
