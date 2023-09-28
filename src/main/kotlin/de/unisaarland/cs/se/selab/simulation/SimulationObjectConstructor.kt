package de.unisaarland.cs.se.selab.simulation

import de.unisaarland.cs.se.selab.dataClasses.bases.Base
import de.unisaarland.cs.se.selab.dataClasses.emergencies.Emergency
import de.unisaarland.cs.se.selab.dataClasses.events.Event
import de.unisaarland.cs.se.selab.graph.Graph

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

    }

    /**
     * Creates an instance of a DataHolder based the parsed and validated objects
     */
    private fun createDataHolder(
        graph: Graph, bases: List<Base>,
        events: MutableList<Event>,
        emergencies: MutableList<Emergency>
    ): DataHolder {

    }

    /**
     * Cross validates the assets with the graph
     */
    private fun validateAssetsBasedOnGraph(graph: Graph, bases: List<Base>): Boolean {

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
