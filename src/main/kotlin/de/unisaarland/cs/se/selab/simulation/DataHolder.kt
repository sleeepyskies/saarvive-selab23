package de.unisaarland.cs.se.selab.simulation

import de.unisaarland.cs.se.selab.dataClasses.Request
import de.unisaarland.cs.se.selab.dataClasses.bases.Base
import de.unisaarland.cs.se.selab.dataClasses.emergencies.Emergency
import de.unisaarland.cs.se.selab.dataClasses.emergencies.EmergencyStatus
import de.unisaarland.cs.se.selab.dataClasses.events.Event
import de.unisaarland.cs.se.selab.dataClasses.vehicles.Vehicle
import de.unisaarland.cs.se.selab.graph.Graph
import de.unisaarland.cs.se.selab.graph.Vertex

/**b
 * The DataHolder stores all relevant simulation data for the phases to manipulate.
 * Additionally, it contains multiple mappings used for fast lookups by the phases.
 */
class DataHolder(
    val graph: Graph,
    val bases: List<Base>,
    val events: Event,
    val emergencies: List<Emergency>,

) {
    val ongoingEmergencies = mutableListOf<Emergency>()
    val resolvedEmergencies: List<Emergency> = mutableListOf<Emergency>()
    val activeVehicles: MutableList<Vehicle> = mutableListOf<Vehicle>()
    val rechargingVehicles: MutableList<Vehicle> = mutableListOf<Vehicle>()
    val unavailableVehicles: List<Vehicle> = mutableListOf<Vehicle>()
    val requests: List<Request> = mutableListOf<Request>()
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
            val baseVertex = graph.graph.find { vertex: Vertex -> vertex.id == base.vertexID }!!
            mapping[base.baseID] = baseVertex
        }
        return mapping
    }

    /**
     * Updates DataHolder's lists of emergencies. Removes provided emergencies from
     * 'emergencies' and appends to 'ongoingEmergencies'
     */
    public fun updateScheduledEmergencies(emergencies: MutableList<Emergency>) {
        for (emergency in emergencies) {
            if (emergency.getEmergencyStatus() == EmergencyStatus.ONGOING) {
                emergencies.remove(emergency)
                ongoingEmergencies.add(emergency)
            }
        }
    }
}
