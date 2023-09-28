package de.unisaarland.cs.se.selab.simulation

import de.unisaarland.cs.se.selab.dataClasses.bases.Base
import de.unisaarland.cs.se.selab.dataClasses.emergencies.Emergency
import de.unisaarland.cs.se.selab.dataClasses.events.Event
import de.unisaarland.cs.se.selab.graph.Graph
import de.unisaarland.cs.se.selab.parser.AssetParser
import de.unisaarland.cs.se.selab.parser.CountyParser
import de.unisaarland.cs.se.selab.parser.SimulationParser

/**
 * This class is responsible for calling the parsers, cross validating the parsed results,
 * constructing the DataHolder and returning the constructed Simulation
 */
class SimulationObjectConstructor(
    private val countyFile: String,
    private val assetFile: String,
    private val simulationFile: String,
    private val maxTick: Int?
) {

    /**
     * This method handles all the classes logic.
     */
    public fun createSimulation(): Simulation {
        val countyParser = CountyParser(countyFile)
        val assetParser = AssetParser("src/main/resources/schema/assets.schema",assetFile)
        val simulationParser = SimulationParser("src/main/resources/schema/simulation.schema", simulationFile)

    }

    /**
     * Creates an instance of a DataHolder based the parsed and validated objects
     */
    private fun createDataHolder(
        graph: Graph,
        bases: List<Base>,
        events: MutableList<Event>,
        emergencies: MutableList<Emergency>
    ): DataHolder {
        return DataHolder(graph, bases, events, emergencies)
    }

    /**
     * Cross validates the assets with the graph
     */
    private fun validateAssetsBasedOnGraph(graph: Graph, bases: List<Base>): Boolean {

    }

    /**
     * Predicate used for cross validation of assets against the graph
     */
    private fun assetCrossValidationPredicate(base: Base, graph: Graph): Boolean {

    }

    /**
     * Cross validates the events based on the graph
     */
    private fun validateEventsBasedOnGraph(graph: Graph, events: List<Event>): Boolean {

    }

    /**
     * Cross validates the emergencies based on the graph
     */
    private fun validateEmergenciesBasedOnGraph(graph: Graph, emergencies: Emergency): Boolean {

    }

}
