package de.unisaarland.cs.se.selab.dataClasses.bases

import de.unisaarland.cs.se.selab.dataClasses.vehicles.Vehicle

/**
 * represents a police station base
 */
data class PoliceStation(
    override val baseID: Int,
    override val vertexID: Int,
    override var staff: Int,
    public var dogs: Int,
    override val vehicles: List<Vehicle>
) : Base(baseID, staff, vertexID, vehicles)
