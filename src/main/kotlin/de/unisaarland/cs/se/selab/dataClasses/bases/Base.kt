package de.unisaarland.cs.se.selab.dataClasses.bases

import de.unisaarland.cs.se.selab.dataClasses.vehicles.Vehicle

/**
 * abstract class that defines a base
 */
abstract class Base(
    open val baseID: Int,
    open var staff: Int,
    open val vertexID: Int,
    open val vehicles: List<Vehicle>
)
