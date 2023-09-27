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
    val bases: Base,
    val events: Event,
    val emergencies: List<Emergency>,

) {
    val ongoingEmergencies = mutableListOf<Emergency>()
    val resolvedEmergencies: List<Emergency> = mutableListOf<Emergency>()
    val activeVehicles: List<Vehicle> = mutableListOf<Vehicle>()
    val unavailableVehicles: List<Vehicle> = mutableListOf<Vehicle>()
    val requests: List<Request> = mutableListOf<Request>()
    var assetsRerouted: Int = 0

    val emergencyToBase: MutableMap<Int, Base> = mutableMapOf()
    val emergencyToVehicles: MutableMap<Int, List<Vehicle>> = mutableMapOf()
    val vehicleToEmergency: MutableMap<Int, Emergency> = mutableMapOf()
    val vehiclesToBase: MutableMap<Int, Base> = mutableMapOf()
    val baseToVertex: MutableMap<Int, Vertex> = mutableMapOf() // should this be in map?

    /**
     * Initialises the DataHolder's mappings. Called by the DataHolder constructor.
     */
    private fun createMapping() {
    }

    /**
     * Updates DataHolder's lists of emergencies. Removes provided emergencies from
     * 'emergencies' and appends to 'ongoingEmergencies'
     */
    public fun updateScheduledEmergencies(emergencies: List<Emergency>) {
        for (emergency in emergencies){
            if (emergency.getEmergencyStatus() == EmergencyStatus.ONGOING) {
                ongoingEmergencies.add(emergency)
            }
        }
    }
}
