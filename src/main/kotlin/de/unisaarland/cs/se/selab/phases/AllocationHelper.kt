package de.unisaarland.cs.se.selab.phases

import de.unisaarland.cs.se.selab.dataClasses.bases.Base
import de.unisaarland.cs.se.selab.dataClasses.bases.Hospital
import de.unisaarland.cs.se.selab.dataClasses.bases.PoliceStation
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

/**
 * has the same logic as the request phase but with emergencies instead of requests
 */
class AllocationHelper(val dataHolder: DataHolder) {

    private fun canAssignVehicle(vehicle: Vehicle, assignedBase: Base): Boolean {
        if (vehicle.vehicleType == VehicleType.K9_POLICE_CAR ||
            vehicle.vehicleType == VehicleType.EMERGENCY_DOCTOR_CAR
        ) {
            when (assignedBase) {
                is PoliceStation -> return assignedBase.dogs > 0
                is Hospital -> return assignedBase.doctors > 0
            }
        }

        return true
    }

    /**
     * assigns a vehicle to the emergency and updates the corresponding attributes
     */

    private fun assignVehicle(vehicle: Vehicle, emergency: Emergency) {
        val requiredVehicles = emergency.requiredVehicles
        val emergencyVertex = emergency.location.first
        val graph = dataHolder.graph
        val base = dataHolder.vehiclesToBase[vehicle.id] ?: Base(1, 1, 1, mutableListOf())

        val shortestPath = getTimeToArrive(vehicle, emergency)
        // check if the car can reach in time
        if (shortestPath < emergency.maxDuration) {
            // update the required vehicles for the request
            if (requiredVehicles[vehicle.vehicleType] == 0) {
                requiredVehicles.remove(vehicle.vehicleType)
            } else {
                updateEmergencyRequirment(vehicle, emergency)
                updateBase(base, vehicle)
                // update the vehicle since its assigned an emergency
                vehicle.assignedEmergencyID = emergency.id
                vehicle.vehicleStatus = VehicleStatus.ASSIGNED_TO_EMERGENCY
                vehicle.currentRoute = graph.calculateShortestRoute(
                    vehicle.lastVisitedVertex,
                    emergencyVertex,
                    vehicle.height
                ).toMutableList()
                dataHolder.vehicleToEmergency[vehicle.id] = emergency
                // add this vehicle to the list of active vehicles if its not there
                if (dataHolder.activeVehicles.find { it.id == vehicle.id } == null) {
                    dataHolder.activeVehicles.add(vehicle)
                }
            }
        }
    }

    /**
     * implements the logic of assigning vehicles that depend on capacity type
     */
    fun assignBasedOnCapacity(vehicle: Vehicle, vehicleCapacity: Pair<CapacityType, Int>, emergency: Emergency) {
        if (vehicleCapacity.first == CapacityType.NONE) {
            val base = dataHolder.vehiclesToBase[vehicle.id] ?: Base(1, 1, 1, mutableListOf())
            if (canAssignVehicle(vehicle, base)) assignVehicle(vehicle, emergency)
        } else {
            when (vehicle) {
                is FireTruckWater -> assignFireTruckWater(vehicle, emergency)
                is FireTruckWithLadder -> assignFireTruckLadder(vehicle, emergency)
                is Ambulance -> assignAmbulance(vehicle, emergency)
                is PoliceCar -> assignPoliceCar(vehicle, emergency)
            }
        }
    }

    /**
     * implements the logic of assigning a firetruck
     */
    private fun assignFireTruckWater(vehicle: FireTruckWater, emergency: Emergency) {
        if (emergency.requiredCapacity.containsKey(CapacityType.WATER)) {
            val requiredNum = emergency.requiredVehicles[VehicleType.FIRE_TRUCK_WATER] ?: 0
            val requiredGallons = emergency.requiredCapacity[CapacityType.WATER] ?: 0
            val fireTruckCapacity = vehicle.maxWaterCapacity - vehicle.currentWaterCapacity

            // if the vehicles aren't need anymore
            if (requiredNum == 0) {
                emergency.requiredCapacity.remove(CapacityType.WATER)
                emergency.requiredVehicles.remove(VehicleType.FIRE_TRUCK_WATER)
            } else if (requiredNum == 1) {
                // if one vehicle is required then it needs to have exact capacity
                if (fireTruckCapacity >= requiredGallons) {
                    assignVehicle(vehicle, emergency)
                }
            } else {
                assignVehicle(vehicle, emergency)
            }
        }
    }

    /**
     * implements the logic of assigning a firetruck with ladder
     */
    private fun assignFireTruckLadder(vehicle: FireTruckWithLadder, emergency: Emergency) {
        if (emergency.requiredCapacity.containsKey(CapacityType.LADDER_LENGTH)) {
            val requiredNum = emergency.requiredVehicles[VehicleType.FIRE_TRUCK_LADDER]
            val requiredLadderLen = emergency.requiredCapacity[CapacityType.LADDER_LENGTH]

            if ((requiredNum ?: 0) == 0) {
                emergency.requiredCapacity.remove(CapacityType.LADDER_LENGTH)
                emergency.requiredVehicles.remove(VehicleType.FIRE_TRUCK_LADDER)
            } else {
                // checking if this vehicle has the right length
                if (vehicle.ladderLength >= (requiredLadderLen ?: 0)) {
                    assignVehicle(vehicle, emergency)
                }
            }
        }
    }

    /**
     * implements the logic of assigning a police car
     */
    private fun assignPoliceCar(vehicle: PoliceCar, emergency: Emergency) {
        val requiredNum = emergency.requiredVehicles[VehicleType.POLICE_CAR] ?: 0
        val requiredCriminalNum = emergency.requiredCapacity[CapacityType.CRIMINAL] ?: 0
        val currentCriminalCapacity = vehicle.maxCriminalCapacity - vehicle.currentCriminalCapcity

        if (currentCriminalCapacity >= requiredCriminalNum) {
            assignVehicle(vehicle, emergency)
            emergency.requiredCapacity[CapacityType.CRIMINAL] = requiredCriminalNum - 1
        }

        if (requiredNum == 0) {
            emergency.requiredCapacity.remove(CapacityType.CRIMINAL)
            emergency.requiredVehicles.remove(VehicleType.POLICE_CAR)
        } else if (requiredCriminalNum == 1) {
            if (currentCriminalCapacity >= requiredCriminalNum) {
                assignVehicle(vehicle, emergency)
            }
        } else {
            assignVehicle(vehicle, emergency)
        }
    }

    /**
     * implements the logic of assigning an ambulance
     */
    private fun assignAmbulance(vehicle: Ambulance, emergency: Emergency) {
        if (emergency.requiredCapacity.containsKey(CapacityType.PATIENT)) {
            val requiredNum = emergency.requiredVehicles[VehicleType.AMBULANCE]

            if ((requiredNum ?: 0) == 0) {
                emergency.requiredCapacity.remove(CapacityType.PATIENT)
                emergency.requiredVehicles.remove(VehicleType.AMBULANCE)
            } else {
                // only assign the vehicle if it doesn't have a patient
                if (vehicle.hasPatient.not()) {
                    assignVehicle(vehicle, emergency)
                }
            }
        }
    }

    private fun updateEmergencyRequirment(
        vehicle: Vehicle,
        emergency: Emergency
    ) {
        val requirment = emergency.requiredVehicles[vehicle.vehicleType] ?: 0
        emergency.requiredVehicles[vehicle.vehicleType] = requirment - 1

        when (vehicle) {
            is PoliceCar -> {
                val capacity = vehicle.maxCriminalCapacity - vehicle.currentCriminalCapcity
                val req = emergency.requiredCapacity[CapacityType.CRIMINAL] ?: 0
                emergency.requiredCapacity[CapacityType.CRIMINAL] = req - capacity
            }
            is FireTruckWater -> {
                val capacity = vehicle.maxWaterCapacity - vehicle.currentWaterCapacity
                val req = emergency.requiredCapacity[CapacityType.WATER] ?: 0
                emergency.requiredCapacity[CapacityType.WATER] = req - capacity
            }
            is Ambulance -> {
                val req = emergency.requiredCapacity[CapacityType.PATIENT] ?: 0
                emergency.requiredCapacity[CapacityType.PATIENT] = req - 1
            }
        }

        if (requirment == 0) {
            emergency.requiredVehicles.remove(vehicle.vehicleType)
        }
    }

    private fun updateBase(base: Base, vehicle: Vehicle) {
        when (base) {
            is PoliceStation -> if (vehicle.vehicleType == VehicleType.K9_POLICE_CAR) base.dogs -= 1
            is Hospital -> if (vehicle.vehicleType == VehicleType.EMERGENCY_DOCTOR_CAR) base.doctors -= 1
        }
    }

    private fun getTimeToArrive(vehicle: Vehicle, emergency: Emergency): Int {
        val vehiclePosition = vehicle.lastVisitedVertex
        val emergencyPosition = emergency.location
        // calculate time to arrive at emergency at vertex 1
        val timeToArrive1 =
            dataHolder.graph.calculateShortestPath(vehiclePosition, emergencyPosition.first, vehicle.height)
        // calculate time to arrive at emergency at vertex 2
        val timeToArrive2 =
            dataHolder.graph.calculateShortestPath(vehiclePosition, emergencyPosition.second, vehicle.height)

        return if (timeToArrive1 <= timeToArrive2) timeToArrive1 else timeToArrive2
        // return maxOf(0, if (timeToArrive1 <= timeToArrive2) timeToArrive1 else timeToArrive2)
        // above code might fix "-214748364 ticks to arrive." issue but need checking
    }
}
