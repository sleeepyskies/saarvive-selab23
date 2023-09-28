package de.unisaarland.cs.se.selab.simulation

import de.unisaarland.cs.se.selab.dataClasses.bases.Base
import de.unisaarland.cs.se.selab.dataClasses.emergencies.Emergency
import de.unisaarland.cs.se.selab.dataClasses.events.Event
import de.unisaarland.cs.se.selab.graph.Graph
import de.unisaarland.cs.se.selab.graph.Vertex

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
        //code
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
    private fun validateEventsBasedOnGraph(graph: Graph, events: List<Event>) {

    }

    /**
     * Cross validates the emergencies based on the graph
     */
    private fun validateEmergenciesBasedOnGraph(graph: Graph, emergencies: Emergency) {

    }

}
