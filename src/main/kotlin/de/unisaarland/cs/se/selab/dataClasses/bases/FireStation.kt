package de.unisaarland.cs.se.selab.dataClasses.bases

import de.unisaarland.cs.se.selab.dataClasses.vehicles.Vehicle

/**
 * represents a fire station base
 */
data class FireStation(
    override val baseID: Int,
    override var staff: Int,
    override val vertexID: Int,
    override val vehicles: List<Vehicle>
) : Base(baseID, staff, vertexID, vehicles)
