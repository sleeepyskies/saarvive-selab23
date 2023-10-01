package de.unisaarland.cs.se.selab.simulation

import de.unisaarland.cs.se.selab.global.Log
import de.unisaarland.cs.se.selab.phases.AllocationPhase
import de.unisaarland.cs.se.selab.phases.EmergencyPhase
import de.unisaarland.cs.se.selab.phases.EmergencyUpdatePhase
import de.unisaarland.cs.se.selab.phases.MapUpdatePhase
import de.unisaarland.cs.se.selab.phases.Phase
import de.unisaarland.cs.se.selab.phases.RequestPhase
import de.unisaarland.cs.se.selab.phases.VehicleUpdatePhase

/**
 * Runs the whole simulation. [dataHolder] keeps track of all the data, [maxTicks] the max number of ticks the program
 * can run (might be provided from the console)
 */
class Simulation(private val dataHolder: DataHolder, private val maxTicks: Int?) {
    private var currentTick = 0

    /**
     * "Processor" of the program, works in cycles
     */
    fun start() {
        val phases = listOf(
            EmergencyPhase(this.dataHolder),
            AllocationPhase(this.dataHolder),
            RequestPhase(this.dataHolder),
            VehicleUpdatePhase(this.dataHolder),
            EmergencyUpdatePhase(this.dataHolder),
            MapUpdatePhase(this.dataHolder)
        )
        Log.displaySimulationStart()
        while (shouldContinue()) {
            Log.displaySimulationTick(this.currentTick)
            phases.forEach { phase: Phase -> phase.execute() }
            currentTick++
        } // end of while loop
        val combinedList =
            this.dataHolder.emergencies + this.dataHolder.ongoingEmergencies +
                this.dataHolder.resolvedEmergencies
        Log.displayStatistics(combinedList, this.dataHolder.assetsRerouted, this.currentTick)
    }

    /**
     * Checks the list of emergencies: all RESOLVED or FAILED -> finish the simulation
     * Compares the current tick and the max tick if provided
     */
    private fun shouldContinue(): Boolean {
        return if (this.maxTicks == null) !emergenciesHandled() else !emergenciesHandled() && !isLastTick()
    }

    /**
     * Checks if the current tick is the last, or can continue
     */
    private fun isLastTick(): Boolean {
        return this.currentTick == this.maxTicks
    }

    /**
     * Checks if all emergencies are handled (RESOLVED or FAILED)
     */
    private fun emergenciesHandled(): Boolean {
        return dataHolder.emergencies.isEmpty() && dataHolder.ongoingEmergencies.isEmpty()
    }
}
