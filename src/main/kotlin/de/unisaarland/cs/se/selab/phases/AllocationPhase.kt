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
        val assignableAssets =
            getAssignableAssets(dataHolder.bases[0], dataHolder.emergencies[0]) //need to change accordingly
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
            is Ambulance -> Pair(CapacityType.PATIENT, 1)
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
                if (vehicleCapacity.second >= requiredCapacity[vehicleCapacity.first]!! &&
                    assignIfCanArriveOnTime(asset, emergency)){
                    // assign vehicle to emergency, update vehicle status
                    asset.assignedEmergencyID = emergency.id
                    asset.vehicleStatus = VehicleStatus.ASSIGNED_TO_EMERGENCY
                    emergency.requiredCapacity[vehicleCapacity.first] =- vehicleCapacity.second
                    // add information about assigned vehicle to dataHolder
                    dataHolder.vehicleToEmergency[asset.id] = emergency
                } else {
                    // reroute vehicle -> need to implement
                    rerouteVehicle(
                        getReallocatableVehicles(dataHolder.emergencyToBase[emergency.id]!!, emergency),
                        emergency)
                    emergency.requiredCapacity[vehicleCapacity.first] =
                        emergency.requiredCapacity[vehicleCapacity.first]!! - vehicleCapacity.second
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

        return timeToArrive1 <= emergency.maxDuration - emergency.handleTime ||
                timeToArrive2 <= emergency.maxDuration - emergency.handleTime
    }

    private fun getReallocatableVehicles(base: Base, emergency: Emergency): List<Vehicle> {
        val neededTypes = emergency.requiredVehicles.keys
        // get all vehicles that are assigned to the emergency or are moving to the emergency
        val activeVehicles =
            base.vehicles.filter { it.getVehicleStatus() == VehicleStatus.ASSIGNED_TO_EMERGENCY ||
                it.getVehicleStatus() == VehicleStatus.MOVING_TO_BASE ||
                it.getVehicleStatus() == VehicleStatus.MOVING_TO_BASE  }
        return activeVehicles.filter { it.vehicleType in neededTypes &&
                it.assignedEmergencyID != emergency.id &&
                dataHolder.vehicleToEmergency[it.id]!!.severity < emergency.severity }
    }

    private fun rerouteVehicle(vehicles: List<Vehicle>, emergency: Emergency): Unit {
        for (vehicle in vehicles) {
            val vehiclePosition = vehicle.lastVisitedVertex
            val emergencyPosition = emergency.location
            // calculate time to arrive at emergency at vertex 1
            val timeToArrive1 =
                dataHolder.graph.calculateShortestPath(vehiclePosition, emergencyPosition.first, vehicle.height)
            // calculate time to arrive at emergency at vertex 2
            val timeToArrive2 =
                dataHolder.graph.calculateShortestPath(vehiclePosition, emergencyPosition.second, vehicle.height)
            // check if vehicle can arrive on time
            if (timeToArrive1 <= emergency.maxDuration - emergency.handleTime) {
                vehicle.currentRoute =
                    dataHolder.graph.calculateShortestRoute(vehiclePosition, emergencyPosition.first, vehicle.height)
                        .toMutableList()
                dataHolder.assetsRerouted++
                val emergency = dataHolder.vehicleToEmergency[vehicle.id]!!
                updateEmergencyAfterReroute(emergency, vehicle)
            } else if (timeToArrive2 <= emergency.maxDuration - emergency.handleTime) {
                vehicle.currentRoute =
                    dataHolder.graph.calculateShortestRoute(vehiclePosition, emergencyPosition.second, vehicle.height)
                        .toMutableList()
                dataHolder.assetsRerouted++
                val emergency = dataHolder.vehicleToEmergency[vehicle.id]!!
                updateEmergencyAfterReroute(emergency, vehicle)
            } else {
                // vehicle cannot arrive on time
            }
        }
    }

    private fun updateEmergencyAfterReroute(emergency: Emergency, vehicle: Vehicle): Unit {
        emergency.requiredVehicles[vehicle.vehicleType] = emergency.requiredVehicles[vehicle.vehicleType]!! + 1
        emergency.requiredCapacity[getVehicleCapacity(vehicle).first] =
            emergency.requiredCapacity[getVehicleCapacity(vehicle).first]!! + getVehicleCapacity(vehicle).second
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
