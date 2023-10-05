package de.unisaarland.cs.se.selab.dataClasses

import de.unisaarland.cs.se.selab.dataClasses.vehicles.CapacityType
import de.unisaarland.cs.se.selab.dataClasses.vehicles.VehicleType

/** A request that is created whe there are no enough assets to handle an emergency.
 */
data class Request(
    val baseIDsToVisit: List<Int>,
    val emergencyID: Int,
    val requestID: Int?,
    val requiredVehicles: MutableMap<VehicleType, Int>,
    val requiredCapacity: MutableMap<CapacityType, Int>
)
