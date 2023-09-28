package de.unisaarland.cs.se.selab.simulation

import de.unisaarland.cs.se.selab.dataClasses.bases.Base
import de.unisaarland.cs.se.selab.dataClasses.emergencies.Emergency
import de.unisaarland.cs.se.selab.dataClasses.events.Event
import de.unisaarland.cs.se.selab.graph.Graph

/**
 * Is responsible for calling the parser classes
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

    private fun createDataHolder(
        graph: Graph, bases: List<Base>,
        events: MutableList<Event>,
        emergencies: MutableList<Emergency>
    ) {

    }

    private fun validateAssetsBasedOnGraph(graph: Graph, bases: List<Base>) {

    }

    private fun validateEventsBasedOnGraph(graph: Graph, events: List<Event>) {

    }

    private fun validateEmergenciesBasedOnGraph(graph: Graph, emergencies: Emergency) {

    }

}
