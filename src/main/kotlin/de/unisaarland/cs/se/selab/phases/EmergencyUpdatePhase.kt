package de.unisaarland.cs.se.selab.phases

import de.unisaarland.cs.se.selab.dataClasses.emergencies.Emergency
import de.unisaarland.cs.se.selab.dataClasses.emergencies.EmergencyStatus
import de.unisaarland.cs.se.selab.dataClasses.vehicles.Ambulance
import de.unisaarland.cs.se.selab.dataClasses.vehicles.FireTruckWater
import de.unisaarland.cs.se.selab.dataClasses.vehicles.PoliceCar
import de.unisaarland.cs.se.selab.dataClasses.vehicles.Vehicle
import de.unisaarland.cs.se.selab.dataClasses.vehicles.VehicleStatus
import de.unisaarland.cs.se.selab.global.Log
import de.unisaarland.cs.se.selab.simulation.DataHolder

/**
 * This phase is responsible for updating all ongoing emergencies in the simulation
 */
class EmergencyUpdatePhase(private val dataHolder: DataHolder) : Phase {
    var currentTick = 0

    /**
     * The main execute method of the EmergencyUpdatePhase
     */
    override fun execute() {
        reduceHandleTime(
            dataHolder.ongoingEmergencies.filter { emergency: Emergency ->
                emergency.emergencyStatus == EmergencyStatus.HANDLING
            }
        )
        // needs to be here so we don't fail component tests
        reduceMaxDuration(dataHolder.ongoingEmergencies)
        // sees which emergencies have reached and take action accordingly
        checkHandling(dataHolder.ongoingEmergencies)
        updateEmergencies(dataHolder.ongoingEmergencies)
        currentTick++
    }

    /**
     * Reduces the max duration of all ongoing emergencies
     */
    private fun reduceMaxDuration(emergencies: List<Emergency>) {
        for (emergency in emergencies) {
            emergency.maxDuration -= 1
        }
    }

    /**
     * Reduces the handle time of all emergencies being handled
     */
    private fun reduceHandleTime(emergencies: List<Emergency>) {
        for (emergency in emergencies) {
            emergency.handleTime -= 1
        }
    }

    /**
     * Sends all the vehicles assigned to an emergency back to their
     * respective bases
     */
    private fun sendVehiclesBack(vehicles: MutableList<Vehicle>) {
        for (vehicle in vehicles) {
            vehicle.vehicleStatus = VehicleStatus.MOVING_TO_BASE
            vehicle.assignedEmergencyID = null
            dataHolder.baseToVertex[vehicle.assignedBaseID]?.let { baseVertex ->
                vehicle.currentRoute = dataHolder.graph.calculateShortestRoute(
                    vehicle.lastVisitedVertex,
                    baseVertex,
                    vehicle.height
                )
            }
            vehicle.remainingRouteWeight =
                dataHolder.graph.weightOfRoute(
                    vehicle.currentRoute.first(),
                    vehicle.currentRoute.last(),
                    vehicle.height
                )
            // vehicles only have a road if the emergency vertex is not the same as the base vertex
            // quick fix
            if (vehicle.currentRoute.size > 1) {
                vehicle.currentRoad = vehicle.currentRoute.first().connectingRoads[vehicle.currentRoute[1].id]
            }
            vehicle.weightTillLastVisitedVertex = 0
            vehicle.lastVisitedVertex = vehicle.currentRoute.first()
            vehicle.currentRouteWeightProgress = 0

            // check is special vehicle is updated
            checkSpecialVehicle(vehicle)
        }
    }

    private fun checkSpecialVehicle(vehicle: Vehicle) {
        when (vehicle) {
            is FireTruckWater -> {
                vehicle.currentWaterCapacity = vehicle.currentWaterCapacity - vehicle.assignedWaterAmt
                // no water amount assigned
                vehicle.assignedWaterAmt = 0
                vehicle.isAvailable = false
                vehicle.vehicleStatus = VehicleStatus.MOVING_TO_BASE
            }
            is Ambulance -> if (vehicle.assignedPatient) {
                vehicle.assignedPatient = false
                vehicle.hasPatient = true
                vehicle.vehicleStatus = VehicleStatus.MOVING_TO_BASE
            }
            is PoliceCar -> {
                vehicle.currentCriminalCapcity = vehicle.currentCriminalCapcity + vehicle.assignedCriminalAmt
                vehicle.assignedCriminalAmt = 0
                vehicle.isAvailable = false
                vehicle.vehicleStatus = VehicleStatus.MOVING_TO_BASE
            }
        }
    }

    /**
     * Checks if emergencies have failed or become resolved and updates accordingly
     */
    private fun updateEmergencies(emergencies: List<Emergency>) {
        val removeEmergencies: MutableList<Emergency> = mutableListOf()
        for (emergency in emergencies) {
            val condition2 = emergency.emergencyStatus == EmergencyStatus.HANDLING &&
                    (0 == emergency.maxDuration - emergency.handleTime)
            // emergency resolved
            if (emergency.handleTime == 0) {
                emergency.emergencyStatus = EmergencyStatus.RESOLVED
                // Log emergency resolved
                Log.displayEmergencyResolved(emergency.id)
                removeEmergencies.add(emergency)
                dataHolder.resolvedEmergencies.add(emergency)
                dataHolder.emergencyToVehicles[emergency.id]?.let { sendVehiclesBack(it) }
            } else if (emergency.emergencyStatus != EmergencyStatus.HANDLING &&
                0 == emergency.maxDuration - emergency.handleTime + 1)
             {
                emergency.emergencyStatus = EmergencyStatus.FAILED
                Log.displayEmergencyFailed(emergency.id)
                removeEmergencies.add(emergency)
                dataHolder.resolvedEmergencies.add(emergency)
                dataHolder.emergencyToVehicles[emergency.id]?.let { sendVehiclesBack(it) }
            }
        }
        dataHolder.ongoingEmergencies.removeAll(removeEmergencies)
    }

    private fun checkHandling(emegergencies: List<Emergency>) {
        for (emergency in emegergencies) {
            // quick fix: second part of &&
            if (allVehiclesReached(emergency) && emergency.emergencyStatus != EmergencyStatus.HANDLING) {
                startHandling(emergency)
            }
        }
    }

    /**
     * checks if all the vehicles have reached the assigned emergency
     */
    private fun allVehiclesReached(emergency: Emergency): Boolean {
        if (emergency.requiredVehicles.isEmpty()) {
            val vehicleIdsAssignedToEmergency = dataHolder.vehicleToEmergency
                .filterValues { it == emergency }.keys.toList()
            val vehiclesAssignedToEmergency = dataHolder.activeVehicles
                .filter { it.id in vehicleIdsAssignedToEmergency }
            val vehiclesReachedEmergency = dataHolder.emergencyToVehicles[emergency.id]
            if (vehiclesReachedEmergency != null) {
                return vehiclesReachedEmergency.containsAll(vehiclesAssignedToEmergency)
            }
        }
        return false
    }

    private fun startHandling(emergency: Emergency) {
        Log.displayEmergencyHandlingStart(emergency.id)
        emergency.emergencyStatus = EmergencyStatus.HANDLING
        val assignedVehicles = dataHolder.emergencyToVehicles.entries
            .find { it.key == emergency.id }?.value?.toList().orEmpty()
        for (vehicle in assignedVehicles) {
            vehicle.vehicleStatus = VehicleStatus.HANDLING
        }
    }
}
