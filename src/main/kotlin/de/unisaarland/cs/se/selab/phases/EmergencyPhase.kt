package de.unisaarland.cs.se.selab.phases

import FireStation
import Hospital
import PoliceStation
import de.unisaarland.cs.se.selab.dataClasses.bases.Base
import de.unisaarland.cs.se.selab.dataClasses.emergencies.Emergency
import de.unisaarland.cs.se.selab.dataClasses.emergencies.EmergencyType
import de.unisaarland.cs.se.selab.graph.Vertex
import de.unisaarland.cs.se.selab.simulation.DataHolder
import kotlin.reflect.typeOf

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
        emergencies.forEach { Emergency ->
            assignBaseToEmergency(Emergency, findClosestBase(Emergency))
        }
    }

    /**
     * Returns the closest responsible Base for the [emergency]
     */
    private fun findClosestBase(emergency: Emergency): Base {
        val listOfResponsibleBases = mutableListOf<Base>()
        when (emergency.emergencyType) {
            EmergencyType.FIRE, EmergencyType.ACCIDENT -> listOfResponsibleBases.addAll(dataHolder.bases.filterIsInstance<FireStation>())
            EmergencyType.CRIME -> listOfResponsibleBases.addAll(dataHolder.bases.filterIsInstance<PoliceStation>())
            EmergencyType.MEDICAL -> listOfResponsibleBases.addAll(dataHolder.bases.filterIsInstance<Hospital>())
        }

        val listOfEmergencyVertices = mutableListOf(emergency.location.first, emergency.location.second)
        var distance: Int = Int.MAX_VALUE
        var base: Base
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
                    when {
                        shortestPath == distance -> //check the baseID
                            shortestPath < distance
                        -> {

                        }
                    }
                }
            }
        }

    }
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
