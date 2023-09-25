package de.unisaarland.cs.se.selab.simulation

import de.unisaarland.cs.se.selab.dataClasses.*
import de.unisaarland.cs.se.selab.graph.Graph
import de.unisaarland.cs.se.selab.graph.Vertex

class DataHolder(
    private val graph: Graph,
    private val bases: Base,
    private val events: Event,
    private val emergencies: List<Emergency>,
    private val ongoingEmergencies: List<Emergency> = mutableListOf<Emergency>(),
    private val resolvedEmergencies: List<Emergency> = mutableListOf<Emergency>(),
    private val activeVehicles: List<Vehicle> = mutableListOf<Vehicle>(),
    private val unavailableVehicles: List<Vehicle> = mutableListOf<Vehicle>(),
    private val requests: List<Request> = mutableListOf<Request>(),
    private var assetsRerouted: Int = 0,

    private val emergencyToBase: Map<Int, Base>,
    private val emergencyToVehicles: Map<Int, List<Vehicle>>,
    private val vehicleToEmergency: Map<Int, Emergency>,
    private val vehiclesToBase: Map<Int, Base>,
    private val baseToVertex: Map<Int, Vertex>
    ) {

    private fun createMapping() {
        // TODO: Implement method that constructs mappings
    }

    public fun updateScheduledEmergencies(emergencies: List<Emergency>) {
        // TODO: Implement method to update emergency lists
    }

}