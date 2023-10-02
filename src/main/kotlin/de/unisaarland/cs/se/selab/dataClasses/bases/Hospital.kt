package de.unisaarland.cs.se.selab.dataClasses.bases

import de.unisaarland.cs.se.selab.dataClasses.vehicles.Vehicle

/**
 * represents a hospital base
 */
data class Hospital(
    override val baseID: Int,
    override val vertexID: Int,
    override var staff: Int,
    public var doctors: Int,
    override val vehicles: MutableList<Vehicle>
) : Base(baseID, staff, vertexID, vehicles)
