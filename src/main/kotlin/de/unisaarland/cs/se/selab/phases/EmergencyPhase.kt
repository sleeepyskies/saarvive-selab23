package de.unisaarland.cs.se.selab.phases

import de.unisaarland.cs.se.selab.dataClasses.bases.Base
import de.unisaarland.cs.se.selab.dataClasses.emergencies.Emergency
import de.unisaarland.cs.se.selab.simulation.DataHolder

/**
 * Class for selecting emergencies for the current tick, adding them to list of active ones,
 * assigning bases to them and sorting. [dataHolder] - access to all lists, mappings
 */
class EmergencyPhase(private val dataHolder: DataHolder) {
    private var currentTick = 0

    /**
     * Main method that keeps track of all processes in EmergencyPhase: choosing the base, assigning, logging, sorting
     */
    fun execute() {
        scheduleEmergencies()


        currentTick++
    }

    /**
     * Gives the dataHolder list of emergencies that should be removed from emergencies list and added to ongoing list
     */
    private fun scheduleEmergencies() {
        val listOfScheduledEmergencies =
            dataHolder.emergencies.filter { emergency: Emergency -> emergency.startTick == this.currentTick }
                .toMutableList()
        dataHolder.updateScheduledEmergencies(listOfScheduledEmergencies)
    }

    /**
     * Assigns Bases for each [emergencies]
     */
    private fun assignBasesToEmergencies(emergencies: List<Emergency>) {

    }

    /**
     * Returns the closest responsible Base for the [emergency]
     */
    private fun findClosestBase(emergency: Emergency): Base {

    }

    /**
     * Assigns [base] to the [emergency]
     */
    private fun assignBaseToEmergency(emergency: Emergency, base: Base) {

    }

    /**
     * Create log for each ASSIGNED [emergencies] by ID
     */
    private fun logEmergenciesByID(emergencies: List<Emergency>) {

    }

    /**
     * Sort ongoing list by severity
     */
    private fun sortBySeverity() {

    }

}
