package de.unisaarland.cs.se.selab.phases

import de.unisaarland.cs.se.selab.dataClasses.CapacityType
import de.unisaarland.cs.se.selab.dataClasses.VehicleType
import de.unisaarland.cs.se.selab.dataClasses.bases.Base
import de.unisaarland.cs.se.selab.dataClasses.emergencies.Emergency
import de.unisaarland.cs.se.selab.dataClasses.emergencies.EmergencyStatus
import de.unisaarland.cs.se.selab.dataClasses.vehicles.Vehicle
import de.unisaarland.cs.se.selab.simulation.DataHolder

/** Represents the allocation phase of the simulation
 *
 */
class AllocationPhase (dataHolder: DataHolder){
    val currentTick = 0
    val nextRequestId = 0

    /** Executes the allocation phase
     */
    public fun execute() {
        //code
    }

    private fun getAssignableAssets(base: Base, emergency: Emergency): List<Vehicle> {
        return listOf<Vehicle>()
    }

    private fun assignBasedOnCapacity(assets: List<Vehicle>, emergency: Emergency):Unit {
        //code
    }

    private fun getVehicleCapacity(vehicle: Vehicle): Pair <CapacityType,Int> {
        return Pair(CapacityType.PATIENT, 0)
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
