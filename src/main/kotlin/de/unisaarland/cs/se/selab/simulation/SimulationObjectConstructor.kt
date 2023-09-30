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
import de.unisaarland.cs.se.selab.global.Log
import de.unisaarland.cs.se.selab.graph.Graph
import de.unisaarland.cs.se.selab.graph.Road
import de.unisaarland.cs.se.selab.graph.Vertex
import de.unisaarland.cs.se.selab.parser.AssetParser
import de.unisaarland.cs.se.selab.parser.CountyParser
import de.unisaarland.cs.se.selab.parser.SimulationParser

/**
 * Is responsible for calling the parsers, cross
 * validating and constructing the DataHolder and Simulation
 */
class SimulationObjectConstructor(
    private val countyFile: String,
    private val assetFile: String,
    private val simulationFile: String,
    private val maxTick: Int?
) {
    /**
     * Creates and returns a parsed and validated Simulation object
     */
    public fun createSimulation(): Simulation? {
        // parse, validate and create map
        var countyParser: CountyParser
        var graph: Graph
        try {
            countyParser = CountyParser(countyFile)
            graph = countyParser.parse()
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Map file is invalid", e)
        }

        // parse, validate and create assets
        var assetParser: AssetParser
        var bases: List<Base>
        var vehicles: List<Vehicle>
        try {
            assetParser = AssetParser("assets.schema", assetFile)
            bases = assetParser.parseBases()
            vehicles = assetParser.allVehicles
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Asset file is invalid", e)
        }

        // parse, validate and create events and emergencies
        val simulationParser = SimulationParser("simulation.schema", simulationFile)
        val emergencies: MutableList<Emergency>
        val events: MutableList<Event>
        simulationParser.parseEmergencyCalls()
        simulationParser.parseEvents()
        if (simulationParser.validEmergency && simulationParser.validEvent) {
            Log.displayInitializationInfoValid(simulationFile)
            emergencies = simulationParser.parsedEmergencies
            events = simulationParser.parsedEvents
        } else {
            return null
        }

        // cross validation and construction
        if (
            validateAssetsBasedOnGraph(graph, bases) &&
            validateEmergenciesBasedOnGraph(graph, emergencies) &&
            validateEventsBasedOnGraph(graph, events, vehicles)
        ) {
            // If validation succeeds return simulation
            val dataHolder = DataHolder(graph, bases, events.toMutableList(), emergencies)
            return Simulation(dataHolder, maxTick)
        } else {
            // If validation fails, throw an exception
            check(false) { "Validation of simulation data failed" }
            return null
        }
    }

    /**
     * Cross validates the assets based on the graph
     */
    private fun validateAssetsBasedOnGraph(graph: Graph, bases: List<Base>): Boolean {
        // Init map
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
                else -> if (!validateGraphEvent(event, graph)) return false
            }
        }
        return true
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
        when (event) {
            is RushHour -> return true
            is Construction -> return roadExists(event.sourceID, event.targetID, graph)
            is RoadClosure -> return roadExists(event.sourceID, event.targetID, graph)
            is TrafficJam -> return roadExists(event.startVertex, event.endVertex, graph)
            else -> throw IllegalArgumentException("Unsupported event type: ${event::class.simpleName}")
        }
    }

    /**
     * Checks if the vertices and road exists, as well as checking the vertices are connected via the edge
     */
    private fun roadExists(sourceID: Int, targetID: Int, graph: Graph): Boolean {
        // find the two event vertices
        val sourceVertex = graph.graph.find { vertex: Vertex -> vertex.id == sourceID }
        val targetVertex = graph.graph.find { vertex: Vertex -> vertex.id == targetID }

        // check they exist
        if (sourceVertex == null || targetVertex == null) return false

        // check they are connected via an edge
        if (
            sourceVertex.connectingRoads[targetVertex.id] == null &&
            targetVertex.connectingRoads[sourceVertex.id] == null
        ) {
            return false
        }

        return true
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
