package de.unisaarland.cs.se.selab.dataClasses.vehicles

import de.unisaarland.cs.se.selab.dataClasses.VehicleType
import de.unisaarland.cs.se.selab.graph.Road
import de.unisaarland.cs.se.selab.graph.Vertex

/**
 * a super class which defines vehicles
 */
open class Vehicle(
    open val vehicleType: VehicleType,
    open val id: Int,
    open val staffCapacity: Int,
    open val height: Int,
    open val assignedBaseID: Int
)
