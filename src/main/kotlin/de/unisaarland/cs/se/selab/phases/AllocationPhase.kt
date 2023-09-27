package de.unisaarland.cs.se.selab.phases

import de.unisaarland.cs.se.selab.dataClasses.bases.Base
import de.unisaarland.cs.se.selab.dataClasses.emergencies.Emergency
import de.unisaarland.cs.se.selab.dataClasses.emergencies.EmergencyStatus
import de.unisaarland.cs.se.selab.dataClasses.vehicles.*
import de.unisaarland.cs.se.selab.graph.Graph
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
        val assignableAssets = getAssignableAssets(dataHolder.bases[0], dataHolder.emergencies[0]) //need to change accordingly
        assignBasedOnCapacity(assignableAssets, dataHolder.emergencies[0]) //need to change accordingly

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
    /** Assigns vehicles to an emergency based on their capacity
     *
     * @param assets The list of vehicles that can be assigned to the emergency from the assigned base
     * @param emergency The emergency that needs to be assigned vehicles
     */
    private fun assignBasedOnCapacity(assets: List<Vehicle>, emergency: Emergency):Unit {
        val requiredCapacity = emergency.requiredCapacity
        for (asset in assets) {
            // check if vehicle has the required capacity
            val vehicleCapacity = getVehicleCapacity(asset)
            if (vehicleCapacity.first in requiredCapacity) {
                if (vehicleCapacity.second >= requiredCapacity[vehicleCapacity.first]!! && assignIfCanArriveOnTime(asset, emergency)){
                    // assign vehicle to emergency, update vehicle status
                    asset.assignedEmergencyID = emergency.id
                    asset.vehicleStatus = VehicleStatus.ASSIGNED_TO_EMERGENCY
                    emergency.requiredCapacity[vehicleCapacity.first] =- vehicleCapacity.second
                    // add information about assigned vehicle to dataHolder
                    dataHolder.vehicleToEmergency[asset.id] = emergency
                } else {
                    // reroute vehicle -> need to implement
                    emergency.requiredCapacity[vehicleCapacity.first] = emergency.requiredCapacity[vehicleCapacity.first]!! - vehicleCapacity.second
                    asset.assignedEmergencyID = emergency.id
                    asset.vehicleStatus = VehicleStatus.ASSIGNED_TO_EMERGENCY
                    dataHolder.vehicleToEmergency[asset.id] = emergency

                }
            }
        }

    }

    private fun assignIfCanArriveOnTime(vehicle: Vehicle, emergency: Emergency): Boolean {
        val vehiclePosition = vehicle.lastVisitedVertex
        val emergencyPosition = emergency.location
        // calculate time to arrive at emergency at vertex 1
        val timeToArrive1 =
            dataHolder.graph.calculateShortestPath(vehiclePosition, emergencyPosition.first, vehicle.height)
        // calculate time to arrive at emergency at vertex 2
        val timeToArrive2 =
            dataHolder.graph.calculateShortestPath(vehiclePosition, emergencyPosition.second, vehicle.height)

        return (timeToArrive1 <= emergency.maxDuration - emergency.handleTime) ||
                (timeToArrive2 <= emergency.maxDuration - emergency.handleTime)
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
