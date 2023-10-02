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
    val graph: Graph,
    val bases: List<Base>,
    var events: MutableList<Event>,
    val emergencies: MutableList<Emergency>,

) {
    var ongoingEmergencies = mutableListOf<Emergency>()
    val resolvedEmergencies: MutableList<Emergency> = mutableListOf()
    val activeVehicles: MutableList<Vehicle> = mutableListOf()
    val rechargingVehicles: MutableList<Vehicle> = mutableListOf()
    val unavailableVehicles: MutableList<Int> = mutableListOf()
    val requests: MutableList<Request> = mutableListOf()
    var assetsRerouted: Int = 0

    val emergencyToBase: MutableMap<Int, Base> = mutableMapOf()
    val emergencyToVehicles: MutableMap<Int, MutableList<Vehicle>> = mutableMapOf()
    val vehicleToEmergency: MutableMap<Int, Emergency> = mutableMapOf()
    val vehiclesToBase: MutableMap<Int, Base> = initVehiclesToBase()
    val baseToVertex: MutableMap<Int, Vertex> = initBaseToVertex() // should this be in map?

    /**
     * Initialises the vehiclesToBase and baseToVertex mappings in DataHolder
     */
    private fun initVehiclesToBase(): MutableMap<Int, Base> {
        val mapping = mutableMapOf<Int, Base>()
        for (base in this.bases) {
            // get the vehicles in the base
            val vehicles = base.vehicles
            // add vehicleID and base to mapping
            for (vehicle in vehicles) {
                mapping[vehicle.id] = base
            }
        }
        return mapping
    }

    private fun initBaseToVertex(): MutableMap<Int, Vertex> {
        val mapping = mutableMapOf<Int, Vertex>()

        for (base in bases) {
            val baseVertex = graph.graph.find { vertex -> vertex.id == base.vertexID }
            if (baseVertex != null) {
                mapping[base.baseID] = baseVertex
            } else {
                throw NoSuchElementException("Base with ID ${base.baseID} does not have a corresponding vertex.")
            }
        }

        return mapping
    }

    /**
     * Updates DataHolder's lists of emergencies. Removes provided emergencies from
     * 'emergencies' and appends to 'ongoingEmergencies'
     */
    fun updateScheduledEmergencies(emergencies: MutableList<Emergency>) {
        for (emergency in emergencies) {
            this.emergencies.remove(emergency)
            ongoingEmergencies.add(emergency)
        }
    }

    /**
     * initializes the location of each emergency
     */
    init {
        for (emergency in emergencies) {
            val roadList = graph.roads
            // get the road this emergency is on
            val road = roadList.first { it.roadName == emergency.roadName }
            val vertexList = graph.graph
            // find the vertices it is connected to
            val vertices = vertexList.filter { it.connectingRoads.containsValue(road) }
            val vertexPair = Pair(vertices[0], vertices[1])
            emergency.location = vertexPair
        }
    }
}
