package de.unisaarland.cs.se.selab.dataClasses

data class Request(
    private val baseIDsToVisit: List<Int>,
    private val emergencyID: Int,
    private val requestID: Int,
    private val requiredVehicles: Map<VehicleType, Int>,
    private val requiredCapacity: Map<CapacityType, Int>
)

