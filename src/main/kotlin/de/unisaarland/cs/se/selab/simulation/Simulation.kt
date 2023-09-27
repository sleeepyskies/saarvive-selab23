package de.unisaarland.cs.se.selab.simulation

import de.unisaarland.cs.se.selab.global.Log
import de.unisaarland.cs.se.selab.phases.*

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
        do {
            phases.forEach { phase: Phase -> phase.execute() }
        } while (shouldContinue())
        Log.displayStatistics(
            this.dataHolder.emergencies + this.dataHolder.ongoingEmergencies +
                    this.dataHolder.resolvedEmergencies,
            this.dataHolder.assetsRerouted
        )
    }

    /**
     * Checks the list of emergencies: all RESOLVED or FAILED -> finish the simulation
     * Compares the current tick and the max tick if provided
     */
    private fun shouldContinue(): Boolean {

    }
}
