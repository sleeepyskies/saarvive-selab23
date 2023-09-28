package de.unisaarland.cs.se.selab.simulation

import de.unisaarland.cs.se.selab.dataClasses.bases.Base
import de.unisaarland.cs.se.selab.dataClasses.emergencies.Emergency
import de.unisaarland.cs.se.selab.dataClasses.events.Event
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
        val assets = assetParser.parse()
        val bases = assets.first

        // parse, validate and create events and emergencies
        val simulationParser = SimulationParser("src/main/resources/schema/simulation.schema",simulationFile)
        val emergencies = simulationParser.parseEmergencyCalls()
        val events = simulationParser.parseEvents()

        // cross validation and construction
        if (
            validateAssetsBasedOnGraph(graph, bases) &&
            validateEmergenciesBasedOnGraph(graph, emergencies) &&
            validateEventsBasedOnGraph(graph, events)
            ) {
            // If validation succeeds return simulation
            val dataHolder = DataHolder(graph, bases, events.toMutableList(), emergencies)
            return Simulation(dataHolder, maxTick)
        } else {
            // If validation fails exit
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
    private fun validateEventsBasedOnGraph(graph: Graph, events: List<Event>): Boolean {
        for (event in events) {

        }
    }

    /**
     * Cross validates the emergencies based on the graph
     */
    private fun validateEmergenciesBasedOnGraph(graph: Graph, emergencies: List<Emergency>): Boolean {

    }

}
