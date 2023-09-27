package de.unisaarland.cs.se.selab.phases

import de.unisaarland.cs.se.selab.dataClasses.bases.Base
import de.unisaarland.cs.se.selab.dataClasses.bases.FireStation
import de.unisaarland.cs.se.selab.dataClasses.bases.Hospital
import de.unisaarland.cs.se.selab.dataClasses.bases.PoliceStation
import de.unisaarland.cs.se.selab.dataClasses.emergencies.Emergency
import de.unisaarland.cs.se.selab.dataClasses.emergencies.EmergencyStatus
import de.unisaarland.cs.se.selab.dataClasses.emergencies.EmergencyType
import de.unisaarland.cs.se.selab.global.Log
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
        val scheduledEmergencies = scheduleEmergencies()
        assignBasesToEmergencies(scheduledEmergencies)
        logEmergenciesByID(scheduledEmergencies)
        sortBySeverity()
        currentTick++
    }

    /**
     * Gives the dataHolder list of emergencies that should be removed from emergencies list and added to ongoing list.
     */
    private fun scheduleEmergencies(): MutableList<Emergency> {
        val listOfScheduledEmergencies =
            dataHolder.emergencies.filter { emergency: Emergency -> emergency.startTick == this.currentTick }
                .toMutableList()
        dataHolder.updateScheduledEmergencies(listOfScheduledEmergencies)
        return listOfScheduledEmergencies
    }

    /**
     * Assigns Bases for each [emergencies]
     */
    private fun assignBasesToEmergencies(emergencies: List<Emergency>) {
        emergencies.forEach { emergency ->
            assignBaseToEmergency(emergency, findClosestBase(emergency))
        }
    }

    /**
     * Returns the closest responsible Base for the [emergency]
     */
    private fun findClosestBase(emergency: Emergency): Base {
        val listOfResponsibleBases = mutableListOf<Base>()
        // Retrieving list of responsible bases
        when (emergency.emergencyType) {
            EmergencyType.FIRE, EmergencyType.ACCIDENT -> listOfResponsibleBases.addAll(
                dataHolder.bases.filterIsInstance<FireStation>()
            )

            EmergencyType.CRIME -> listOfResponsibleBases.addAll(dataHolder.bases.filterIsInstance<PoliceStation>())
            EmergencyType.MEDICAL -> listOfResponsibleBases.addAll(dataHolder.bases.filterIsInstance<Hospital>())
        }

        val listOfEmergencyVertices = mutableListOf(emergency.location.first, emergency.location.second)
        var distance: Int = Int.MAX_VALUE
        var base: Base? = null
        // Check for the shortest distance and Base
        listOfResponsibleBases.forEach { responsibleBase ->
            run {
                val baseId = responsibleBase.baseID
                val baseVertex = dataHolder.baseToVertex[baseId]!!
                listOfEmergencyVertices.forEach { vertex ->
                    val shortestPath = dataHolder.graph.calculateShortestPath(
                        baseVertex,
                        vertex,
                        0
                    )
                    val routeAndBase = chooseBaseAndRoute(shortestPath, distance, base, baseId, responsibleBase)
                    distance = routeAndBase.first
                    base = routeAndBase.second
                }
            }
        }
        return base!!
    }

    /**
     * Checks for shorter path and returns the base and distance
     */
    private fun chooseBaseAndRoute(
        shortestPath: Int,
        distance: Int,
        base: Base?,
        baseId: Int,
        responsibleBase: Base
    ): Pair<Int, Base> {
        var distance = distance
        var base = base
        when {
            shortestPath == distance -> if (base == null || base!!.baseID > baseId) {
                base = responsibleBase
            }

            shortestPath < distance -> {
                distance = shortestPath
                base = responsibleBase
            }
        }
        return Pair(distance, base!!)
    }

    /**
     * Assigns [base] to the [emergency]
     */
    private fun assignBaseToEmergency(emergency: Emergency, base: Base) {
        emergency.emergencyStatus = EmergencyStatus.ASSIGNED
        this.dataHolder.emergencyToBase[emergency.id] = base
    }

    /**
     * Create log for each ASSIGNED [emergencies] by ID
     */
    private fun logEmergenciesByID(emergencies: List<Emergency>) {
        val sortedByID = emergencies.sortedBy { emergency: Emergency -> emergency.id }
        sortedByID.forEach { sortedEmergency ->
            run {
                val sortedEmergencyID = sortedEmergency.id
                Log.displayEmergencyAssignment(
                    sortedEmergencyID,
                    this.dataHolder.emergencyToBase[sortedEmergencyID]!!.baseID
                )
            }
        }
    }

    /**
     * Sort ongoing list by severity
     */
    private fun sortBySeverity() {
        this.dataHolder.ongoingEmergencies.sortedWith(compareBy<Emergency> { it.severity }.thenBy { it.id })
    }
}
