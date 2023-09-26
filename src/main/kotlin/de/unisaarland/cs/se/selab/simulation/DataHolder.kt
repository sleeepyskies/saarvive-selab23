package de.unisaarland.cs.se.selab.simulation

import de.unisaarland.cs.se.selab.dataClasses.Request
import de.unisaarland.cs.se.selab.dataClasses.bases.Base
import de.unisaarland.cs.se.selab.dataClasses.emergencies.Emergency
import de.unisaarland.cs.se.selab.dataClasses.events.Event
import de.unisaarland.cs.se.selab.dataClasses.vehicles.Vehicle
import de.unisaarland.cs.se.selab.graph.Graph
import de.unisaarland.cs.se.selab.graph.Vertex

/**
 * The DataHolder stores all relevant simulation data for the phases to manipulate.
 * Additionally, it contains multiple mappings used for fast lookups by the phases.
 */
class DataHolder(
    private val graph: Graph,
    private val bases: Base,
    private val events: Event,
    private val emergencies: List<Emergency>,

) {
    private val ongoingEmergencies: List<Emergency> = mutableListOf<Emergency>()
    private val resolvedEmergencies: List<Emergency> = mutableListOf<Emergency>()
    private val activeVehicles: List<Vehicle> = mutableListOf<Vehicle>()
    private val unavailableVehicles: List<Vehicle> = mutableListOf<Vehicle>()
    private val requests: List<Request> = mutableListOf<Request>()
    private var assetsRerouted: Int = 0

    private val emergencyToBase: MutableMap<Int, Base> = mutableMapOf()
    private val emergencyToVehicles: MutableMap<Int, List<Vehicle>> = mutableMapOf()
    private val vehicleToEmergency: MutableMap<Int, Emergency> = mutableMapOf()
    private val vehiclesToBase: MutableMap<Int, Base> = mutableMapOf()
    private val baseToVertex: MutableMap<Int, Vertex> = mutableMapOf() // should this be in map?

    /**
     * Initialises the DataHolder's mappings. Called by the DataHolder constructor.
     */
    private fun createMapping() {
        TODO("Implement method that constructs mappings")
    }

    /**
     * Updates DataHolder's lists of emergencies. Removes provided emergencies from
     * 'emergencies' and appends to 'ongoingEmergencies'
     */
    public fun updateScheduledEmergencies(emergencies: List<Emergency>) {
        TODO("Implement method to update emergency lists")
    }
}
