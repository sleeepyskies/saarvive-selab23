package de.unisaarland.cs.se.selab.dataClasses.bases

import de.unisaarland.cs.se.selab.dataClasses.vehicles.Vehicle

/**
 * abstract class that defines a base
 */
abstract class Base(
    internal val baseID: Int,
    private var staff: Int,
    private val vertexID: Int,
    private val vehicles: List<Vehicle>
)
