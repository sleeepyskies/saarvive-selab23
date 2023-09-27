package de.unisaarland.cs.se.selab.dataClasses.bases

import de.unisaarland.cs.se.selab.dataClasses.vehicles.Vehicle

/**
 * abstract class that defines a base
 */
abstract class Base(
    public open val baseID: Int,
    private var staff: Int,
    public val vertexID: Int,
    public val vehicles: List<Vehicle>
)
