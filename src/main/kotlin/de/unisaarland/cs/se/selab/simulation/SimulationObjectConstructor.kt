package de.unisaarland.cs.se.selab.simulation

import de.unisaarland.cs.se.selab.dataClasses.bases.Base
import de.unisaarland.cs.se.selab.dataClasses.emergencies.Emergency
import de.unisaarland.cs.se.selab.dataClasses.events.*
import de.unisaarland.cs.se.selab.dataClasses.vehicles.Vehicle
import de.unisaarland.cs.se.selab.graph.Graph
import de.unisaarland.cs.se.selab.graph.Vertex
import de.unisaarland.cs.se.selab.parser.AssetParser
import de.unisaarland.cs.se.selab.parser.CountyParser
import de.unisaarland.cs.se.selab.parser.SimulationParser
import kotlin.system.exitProcess

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
    public fun createSimulation(): Simulation {
        // parse, validate and create map
        val countyParser = CountyParser(countyFile)
        val graph = countyParser.parse()

        // parse, validate and create assets
        val assetParser = AssetParser("src/main/resources/schema/assets.schema",assetFile)
        val (bases, vehicles) = assetParser.parse()

        // parse, validate and create events and emergencies
        val simulationParser = SimulationParser("src/main/resources/schema/simulation.schema",simulationFile)
        val emergencies = simulationParser.parseEmergencyCalls()
        val events = simulationParser.parseEvents()

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
            // If validation fails, exit
            exitProcess(1)
        }
    }

    /**
     * Creates the DataHolder object
     */
    private fun createDataHolder(
        graph: Graph, bases: List<Base>,
        events: MutableList<Event>,
        emergencies: MutableList<Emergency>
    ) {

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
            mapping[baseVertex]!!.add(base)
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
        val vehicle = vehicles.find { vehicle: Vehicle -> vehicle.id == event.vehicleID}
        return vehicle != null
    }

    /**
     * Helper method for validateEventsBasedOnGraph(). Validates if the roads exist
     */
    private fun validateGraphEvent(event: Event, graph: Graph): Boolean {
        when (event) {
            is RushHour -> return true
            is VehicleUnavailable -> return true // impossible to reach
            is Construction -> return roadExists(event.sourceID, event.targetID, graph)
            is RoadClosure -> return roadExists(event.sourceID, event.targetID, graph)
            is TrafficJam -> return roadExists(event.startVertex, event.endVertex, graph)
        }

        return true
    }

    /**
     *
     */
    private fun roadExists(sourceID: Int, targetID: Int, graph: Graph): Boolean {
        // find the two event vertices
        val sourceVertex = graph.graph.find { vertex: Vertex -> vertex.id == sourceID }
        val targetVertex = graph.graph.find { vertex: Vertex -> vertex.id == targetID }

        // check they exist
        if (sourceVertex == null || targetVertex == null) return false

        // check they are connected via an edge
        if (
            sourceVertex.connectingRoads[targetVertex] == null &&
            targetVertex.connectingRoads[sourceVertex] == null
            ) {
            return false
        }

        return true
    }

    /**
     * Cross validates the emergencies based on the graph
     */
    private fun validateEmergenciesBasedOnGraph(graph: Graph, emergencies: List<Emergency>): Boolean {

    }

}
