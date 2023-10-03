package de.unisaarland.cs.se.selab.phases

import de.unisaarland.cs.se.selab.dataClasses.Request
import de.unisaarland.cs.se.selab.dataClasses.bases.Base
import de.unisaarland.cs.se.selab.dataClasses.emergencies.Emergency
import de.unisaarland.cs.se.selab.dataClasses.emergencies.EmergencyStatus
import de.unisaarland.cs.se.selab.dataClasses.vehicles.Ambulance
import de.unisaarland.cs.se.selab.dataClasses.vehicles.CapacityType
import de.unisaarland.cs.se.selab.dataClasses.vehicles.FireTruckWater
import de.unisaarland.cs.se.selab.dataClasses.vehicles.FireTruckWithLadder
import de.unisaarland.cs.se.selab.dataClasses.vehicles.PoliceCar
import de.unisaarland.cs.se.selab.dataClasses.vehicles.Vehicle
import de.unisaarland.cs.se.selab.dataClasses.vehicles.VehicleStatus
import de.unisaarland.cs.se.selab.simulation.DataHolder

/** Represents the allocation phase of the simulation
 *
 */
class AllocationPhase(private val dataHolder: DataHolder) : Phase {
    private var nextRequestId = 0
    private val allocationHelper = AllocationHelper(dataHolder)

    /**
     * Executes the allocation phase
     */
    override fun execute() {
        for (emergency in dataHolder.ongoingEmergencies) {
            if (emergency.emergencyStatus == EmergencyStatus.ASSIGNED ||
                emergency.emergencyStatus == EmergencyStatus.ONGOING
            ) {
                val base = dataHolder.emergencyToBase[emergency.id] ?: Base(1, 1, 1, mutableListOf())

                val assignableVehicle = getAssignableAssets(base, emergency)
                assignBasedOnCapacity(assignableVehicle, emergency)

                if (emergency.requiredVehicles.isNotEmpty()) {
                    val reallocatableVehicles = getReallocatableVehicles(base, emergency)
                    assignBasedOnCapacity(reallocatableVehicles, emergency)
                }

                if (emergency.requiredVehicles.isNotEmpty()) {
                    createRequest(emergency, base)
                }
            }
        }
    }

    /**
     * Returns all vehicles that are in a base that are of the correct vehicle type for an emergency
     */
    fun getAssignableAssets(base: Base, emergency: Emergency): List<Vehicle> {
        val requiredVehicles = emergency.requiredVehicles
        val vehicles = base.vehicles

        return vehicles
            .filter { it.vehicleStatus == VehicleStatus.IN_BASE }
            .filter { it.isAvailable }
            .filter { it.vehicleType in requiredVehicles }
    }

    /**
     * Returns the current amount of capacity a vehicle has
     */
    private fun getVehicleCapacity(vehicle: Vehicle): Pair<CapacityType, Int> {
        return when (vehicle) {
            is PoliceCar -> Pair(CapacityType.CRIMINAL, vehicle.maxCriminalCapacity - vehicle.currentCriminalCapcity)
            is FireTruckWater -> Pair(CapacityType.WATER, vehicle.maxWaterCapacity - vehicle.currentWaterCapacity)
            is Ambulance -> Pair(CapacityType.PATIENT, if (vehicle.hasPatient) 0 else 1)
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
        val requiredVehicle = emergency.requiredVehicles
        for (asset in assets) if (requiredVehicle.isNotEmpty()) {
            // check if vehicle has the required capacity
            val vehicleCapacity = getVehicleCapacity(asset)
            // allocationHelper.assignBasedOnCapacity(asset, vehicleCapacity, emergency)
            checkAndAssign(vehicleCapacity, asset, emergency)
            // val arrival = getTimeToArrive(asset, emergency)
            // Log.displayAssetAllocation(asset.id, emergency.id, arrival)
        } else {
            break
        }
        emergency.emergencyStatus = EmergencyStatus.ONGOING
    }

    /**
     *  check if a vehicle fits the requirements for an emergency and assigns accordingly
     */
    fun checkAndAssign(
        vehicleCapacity: Pair<CapacityType, Int>,
        asset: Vehicle,
        emergency: Emergency
    ) {
        allocationHelper.assignBasedOnCapacity(asset, vehicleCapacity, emergency)
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
                (dataHolder.vehicleToEmergency[it.id]?.severity ?: 0) < emergency.severity
        }
    }

    private fun getBasesByProximity(emergency: Emergency, base: Base): List<Base> {
        val baseVertex = dataHolder.baseToVertex
        val allBases = dataHolder.bases

        return dataHolder.graph.findClosestBasesByProximity(emergency, base, allBases, baseVertex)
    }

    private fun createRequest(emergency: Emergency, base: Base) {
        val basesToVisit = getBasesByProximity(emergency, base)
        val baseIds = basesToVisit.map { it.baseID }
        val requiredVehicles = emergency.requiredVehicles
        val request = Request(baseIds, emergency.id, nextRequestId, requiredVehicles, emergency.requiredCapacity)
        dataHolder.requests.add(request)
        nextRequestId++
    }
}
