package de.unisaarland.cs.se.selab.dataClasses.emergencies

import de.unisaarland.cs.se.selab.dataClasses.CapacityType
import de.unisaarland.cs.se.selab.graph.Vertex
import de.unisaarland.cs.se.selab.dataClasses.VehicleType


class Emergency(
    val id: Int,
    val emergencyType: EmergencyType,
    val severity: Int,
    val startTick: Int,
    var handleTime: Int, // change it for "stalking" required time on every tick
    val maxDuration: Int,
    val villageName: String,
    val roadName: String
) {
    private val location: Pair<Vertex, Vertex> = TODO()
    private var emergencyStatus: EmergencyStatus = EmergencyStatus.UNASSIGNED
    private val requiredVehicles: Map<VehicleType, Int> = this.calculateRequiredVehicles() // add and remove dynamically
    private val requiredCapacity: Map<CapacityType, Int> =
        this.calculateRequiredCapacity() // add and remove dynamically

    private fun calculateRequiredVehicles(): Map<VehicleType, Int> {
        return TODO("Provide the return value")
    }

    private fun calculateRequiredCapacity(): Map<CapacityType, Int> {
        return TODO("Provide the return value")
    }
}