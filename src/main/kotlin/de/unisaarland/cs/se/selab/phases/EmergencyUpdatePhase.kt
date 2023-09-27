package de.unisaarland.cs.se.selab.phases

import de.unisaarland.cs.se.selab.dataClasses.emergencies.Emergency
import de.unisaarland.cs.se.selab.dataClasses.vehicles.Vehicle
import de.unisaarland.cs.se.selab.simulation.DataHolder

class EmergencyUpdatePhase(private val dataHolder: DataHolder) : Phase {
    /**
     * The main execute method of the EmergencyUpdatePhase
     */
    override fun execute() {
    }

    /**
     * Sends all the vehicles assigned to an emergency back to their
     * respective bases
     */
    private fun sendVehiclesBack(vehicles: List<Vehicle>) {
    }

    /**
     * Reduces the max duration of all ongoing emergencies
     */
    private fun reduceMaxDuration(emergencies: List<Emergency>) {
    }

    /**
     * Reduces the handle time of all emergencies being handled
     */
    private fun reduceHandleTime(emergencies: List<Emergency>) {
    }
}
