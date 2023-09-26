package de.unisaarland.cs.se.selab.dataClasses

/** A request that is created whe there are no enough assets to handle an emergency.
 */
data class Request(
    private val baseIDsToVisit: List<Int>,
    private val emergencyID: Int,
    private val requestID: Int,
    private val requiredVehicles: Map<VehicleType, Int>,
    private val requiredCapacity: Map<CapacityType, Int>
)

