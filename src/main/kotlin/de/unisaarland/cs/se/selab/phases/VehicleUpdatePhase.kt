package de.unisaarland.cs.se.selab.phases

import de.unisaarland.cs.se.selab.dataClasses.emergencies.Emergency
import de.unisaarland.cs.se.selab.dataClasses.emergencies.EmergencyStatus
import de.unisaarland.cs.se.selab.dataClasses.vehicles.Vehicle
import de.unisaarland.cs.se.selab.dataClasses.vehicles.VehicleStatus
import de.unisaarland.cs.se.selab.simulation.DataHolder

/**
 * This phase is responsible for moving all active vehicles,
 * checks if vehicles have reached their emergency,
 * and updates both vehicle and emergency statuses
 */
class VehicleUpdatePhase(dataHolder: DataHolder) {

    public fun execute() {

    }

    private fun updateVehiclePosition(vehicle: Vehicle) {

    }

    private fun checkVehicleReachedEmergency(vehicle: Vehicle): Boolean {

    }

    private fun updateEmergencyStatus(emergency: Emergency, status: EmergencyStatus) {

    }

    private fun updateVehicleAvailability(vehicle: Vehicle) {

    }

    private fun updateVehicleStatus(vehicle: Vehicle, status: VehicleStatus) {

    }
}