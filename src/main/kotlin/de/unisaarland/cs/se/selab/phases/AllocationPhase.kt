package de.unisaarland.cs.se.selab.phases

import de.unisaarland.cs.se.selab.dataClasses.bases.Base
import de.unisaarland.cs.se.selab.dataClasses.emergencies.Emergency
import de.unisaarland.cs.se.selab.dataClasses.emergencies.EmergencyStatus
import de.unisaarland.cs.se.selab.dataClasses.vehicles.*
import de.unisaarland.cs.se.selab.simulation.DataHolder

/** Represents the allocation phase of the simulation
 *
 */
class AllocationPhase (val dataHolder: DataHolder){
    val currentTick = 0
    val nextRequestId = 0

    /** Executes the allocation phase
     */
    public fun execute() {
        var assignableAssets = getAssignableAssets(dataHolder.bases[0], dataHolder.emergencies[0]) //need to change accor dingly
        assignBasedOnCapacity(assignableAssets,)

    }

    private fun getAssignableAssets(base: Base, emergency: Emergency): List<Vehicle> {
        val requiredVehicles = emergency.requiredVehicles
        val vehicles = base.vehicles

        return vehicles
            .filter { it.getVehicleStatus() == VehicleStatus.IN_BASE }
            .filter { it.vehicleType in requiredVehicles }
    }

    private fun getVehicleCapacity(vehicle: Vehicle): Pair <CapacityType,Int> {
        return when (vehicle) {
            is PoliceCar -> Pair(CapacityType.CRIMINAL, vehicle.maxCriminalCapacity)
            is FireTruckWater -> Pair(CapacityType.WATER, vehicle.maxWaterCapacity)
            is Ambulance -> Pair(CapacityType.PATIENT, vehicle.maxPatientCapacity)
            is FireTruckWithLadder -> Pair(CapacityType.LADDER_LENGTH, vehicle.ladderLength)
            else -> Pair(CapacityType.NONE, 0)
        }
    }
    private fun assignBasedOnCapacity(assets: List<Vehicle>, emergency: Emergency):Unit {

    }

    private fun assignIfCanArriveOnTime(vehicle: Vehicle, emergency: Emergency): Unit {
        return Unit
    }

    private fun getReallocatableVehicles(vehicles: List<Vehicle>, emergency: Emergency): List<Vehicle> {
        return listOf<Vehicle>()
    }

    private fun rerouteVehicle(vehicles: List<Vehicle>, emergency: Emergency): Unit {
        return Unit
    }

    private fun updateEmergencyAfterReroute(emergency: Emergency, vehicles: List<Vehicle>): Unit {
        return Unit
    }

    private fun getBasesByProximity(base: Base): List<Base> {
        return listOf<Base>()
    }

    private fun createRequest(emergency: Emergency, base: Base, requiredVehicles: MutableMap<VehicleType, Int>): Unit {
        return Unit
    }

    private fun updateEmergencyStatus (emergency: Emergency, status: EmergencyStatus):Unit{

    }

}
