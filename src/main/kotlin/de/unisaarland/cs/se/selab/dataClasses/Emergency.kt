package de.unisaarland.cs.se.selab.dataClasses

import de.unisaarland.cs.se.selab.graph.Vertex
import de.unisaarland.cs.se.selab.dataClasses.EmergencyStatus
import de.unisaarland.cs.se.selab.dataClasses.EmergencyType
import de.unisaarland.cs.se.selab.dataClasses.VehicleType


class Emergency(
    val id: Int,
    val emergencyType: EmergencyType,
    val severity: Int,
    val startTick: Int, // we won´t change it?
    val handleTime: Int, // we won´t change it?
    val maxDuration: Int, // we won´t change it?
    val villageName: String,
    val roadName: String
) {
    private val location: Pair<Vertex, Vertex> = TODO()
    private lateinit var emergencyStatus: EmergencyStatus
    private lateinit var requiredVehicles: Map<VehicleType, Int> // here following what we need?
    private lateinit var requiredCapacity: Map<CapacityType, Int>// here following what we need?

    private fun calculateRequiredVehicles(): Map<VehicleType, Int> {
        return TODO("Provide the return value")
    }

    private fun calculateRequiredCapacity(): Map<CapacityType, Int> {
        return TODO("Provide the return value")
    }
}