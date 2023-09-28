package de.unisaarland.cs.se.selab.phases

import de.unisaarland.cs.se.selab.dataClasses.Request
import de.unisaarland.cs.se.selab.dataClasses.bases.Base
import de.unisaarland.cs.se.selab.dataClasses.emergencies.Emergency
import de.unisaarland.cs.se.selab.dataClasses.vehicles.Ambulance
import de.unisaarland.cs.se.selab.dataClasses.vehicles.CapacityType
import de.unisaarland.cs.se.selab.dataClasses.vehicles.FireTruckWater
import de.unisaarland.cs.se.selab.dataClasses.vehicles.FireTruckWithLadder
import de.unisaarland.cs.se.selab.dataClasses.vehicles.PoliceCar
import de.unisaarland.cs.se.selab.dataClasses.vehicles.Vehicle
import de.unisaarland.cs.se.selab.dataClasses.vehicles.VehicleStatus
import de.unisaarland.cs.se.selab.dataClasses.vehicles.VehicleType
import de.unisaarland.cs.se.selab.simulation.DataHolder

/** Represents the allocation phase of the simulation
 *
 */
class AllocationPhase(private val dataHolder: DataHolder) : Phase {
    private var currentTick = 0
    private val nextRequestId = 0

    /** Executes the allocation phase
     */
    override fun execute() {
        for (emergency in dataHolder.emergencies) {
            if (emergency.startTick == currentTick) {
                val base = dataHolder.emergencyToBase[emergency.id]
                if (base != null) {
                    assignBasedOnCapacity(getAssignableAssets(base, emergency), emergency)
                }
            }
        }
    }

    private fun getAssignableAssets(base: Base, emergency: Emergency): List<Vehicle> {
        val requiredVehicles = emergency.requiredVehicles
        val vehicles = base.vehicles

        return vehicles
            .filter { it.vehicleStatus == VehicleStatus.IN_BASE }
            .filter { it.vehicleType in requiredVehicles }
    }

    private fun getVehicleCapacity(vehicle: Vehicle): Pair<CapacityType, Int> {
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
    private fun assignBasedOnCapacity(assets: List<Vehicle>, emergency: Emergency) {
        val requiredCapacity = emergency.requiredCapacity
        for (asset in assets) {
            // check if vehicle has the required capacity
            val vehicleCapacity = getVehicleCapacity(asset)
            if (vehicleCapacity.first in requiredCapacity) {
                if (vehicleCapacity.second >= requiredCapacity[vehicleCapacity.first]!! &&
                    assignIfCanArriveOnTime(asset, emergency)
                ) {
                    // assign vehicle to emergency, update vehicle status
                    asset.assignedEmergencyID = emergency.id
                    asset.vehicleStatus = VehicleStatus.ASSIGNED_TO_EMERGENCY
                    emergency.requiredCapacity[vehicleCapacity.first] = -vehicleCapacity.second
                    // add information about assigned vehicle to dataHolder
                    dataHolder.vehicleToEmergency[asset.id] = emergency
                } else {
                    // reroute vehicle -> need to implement
                    rerouteVehicle(
                        getReallocatableVehicles(dataHolder.emergencyToBase[emergency.id]!!, emergency),
                        emergency
                    )
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
            base.vehicles.filter {
                it.vehicleStatus == VehicleStatus.ASSIGNED_TO_EMERGENCY ||
                    it.vehicleStatus == VehicleStatus.MOVING_TO_BASE ||
                    it.vehicleStatus == VehicleStatus.MOVING_TO_BASE
            }
        return activeVehicles.filter {
            it.vehicleType in neededTypes &&
                it.assignedEmergencyID != emergency.id &&
                dataHolder.vehicleToEmergency[it.id]!!.severity < emergency.severity
        }
    }

    private fun rerouteVehicle(vehicles: List<Vehicle>, emergency: Emergency) {
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
                updateEmergencyAfterReroute(dataHolder.vehicleToEmergency[vehicle.id]!!, vehicle)
            } else if (timeToArrive2 <= emergency.maxDuration - emergency.handleTime) {
                vehicle.currentRoute =
                    dataHolder.graph.calculateShortestRoute(vehiclePosition, emergencyPosition.second, vehicle.height)
                        .toMutableList()
                dataHolder.assetsRerouted++
                updateEmergencyAfterReroute(dataHolder.vehicleToEmergency[vehicle.id]!!, vehicle)
            } else {
                createRequest(emergency, dataHolder.emergencyToBase[emergency.id]!!, emergency.requiredVehicles)
            }
        }
    }

    private fun updateEmergencyAfterReroute(emergency: Emergency, vehicle: Vehicle) {
        emergency.requiredVehicles[vehicle.vehicleType] = emergency.requiredVehicles[vehicle.vehicleType]!! + 1
        emergency.requiredCapacity[getVehicleCapacity(vehicle).first] =
            emergency.requiredCapacity[getVehicleCapacity(vehicle).first]!! + getVehicleCapacity(vehicle).second
    }

    private fun getBasesByProximity(emergency: Emergency, base: Base): List<Base> {
        val baseVertex = dataHolder.baseToVertex
        val allBases = dataHolder.bases

        return dataHolder.graph.findClosestBasesByProximity(emergency, base, allBases, baseVertex)
    }

    private fun createRequest(emergency: Emergency, base: Base, requiredVehicles: MutableMap<VehicleType, Int>) {
        val basesToVisit = getBasesByProximity(emergency, base)
        val baseIds = basesToVisit.map { it.baseID }
        val request = Request(baseIds, emergency.id, nextRequestId, requiredVehicles, emergency.requiredCapacity)
        dataHolder.requests.add(request)
    }

}
