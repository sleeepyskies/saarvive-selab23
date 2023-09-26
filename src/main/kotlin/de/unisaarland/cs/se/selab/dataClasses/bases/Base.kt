package de.unisaarland.cs.se.selab.dataClasses

import de.unisaarland.cs.se.selab.dataClasses.vehicles.Vehicle

/**
 * abstract class that defines a base
 */
abstract class Base(
    private val baseID: Int,
    private var staff: Int,
    private val vertexID: String,
    private val vehicles: List<Vehicle>
)
