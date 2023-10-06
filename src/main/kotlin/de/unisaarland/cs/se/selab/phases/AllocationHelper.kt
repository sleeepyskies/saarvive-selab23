package de.unisaarland.cs.se.selab.phases

import de.unisaarland.cs.se.selab.dataClasses.bases.Base
import de.unisaarland.cs.se.selab.dataClasses.bases.Hospital
import de.unisaarland.cs.se.selab.dataClasses.bases.PoliceStation
import de.unisaarland.cs.se.selab.dataClasses.emergencies.Emergency
import de.unisaarland.cs.se.selab.dataClasses.emergencies.EmergencyStatus
import de.unisaarland.cs.se.selab.dataClasses.emergencies.EmergencyType
import de.unisaarland.cs.se.selab.dataClasses.vehicles.Ambulance
import de.unisaarland.cs.se.selab.dataClasses.vehicles.CapacityType
import de.unisaarland.cs.se.selab.dataClasses.vehicles.FireTruckWater
import de.unisaarland.cs.se.selab.dataClasses.vehicles.FireTruckWithLadder
import de.unisaarland.cs.se.selab.dataClasses.vehicles.PoliceCar
import de.unisaarland.cs.se.selab.dataClasses.vehicles.Vehicle
import de.unisaarland.cs.se.selab.dataClasses.vehicles.VehicleStatus
import de.unisaarland.cs.se.selab.dataClasses.vehicles.VehicleType
import de.unisaarland.cs.se.selab.global.Log
import de.unisaarland.cs.se.selab.global.Number
import de.unisaarland.cs.se.selab.graph.Vertex
import de.unisaarland.cs.se.selab.simulation.DataHolder
import kotlin.math.max

/**
 * has the same logic as the request phase but with emergencies instead of requests
 */
class AllocationHelper(val dataHolder: DataHolder) {

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
     * creates a list of vehicles that does not have a special capacity type
     */
    fun getNormalVehicles(vehicles: List<Vehicle>): List<Vehicle> {
        val requiredVehicle = vehicles.filter { vehicle ->
            vehicle.vehicleType in setOf(
                VehicleType.POLICE_MOTORCYCLE,
                VehicleType.FIREFIGHTER_TRANSPORTER,
                VehicleType.FIRE_TRUCK_TECHNICAL,
                VehicleType.EMERGENCY_DOCTOR_CAR,
                VehicleType.K9_POLICE_CAR
            )
        }
        return requiredVehicle
    }

    /**
     * checks if a vehicle is normal; used in assignment
     */
    fun isNormalVehicle(vehicle: Vehicle): Boolean {
        return vehicle.vehicleType in setOf(
            VehicleType.POLICE_MOTORCYCLE,
            VehicleType.FIREFIGHTER_TRANSPORTER,
            VehicleType.FIRE_TRUCK_TECHNICAL,
            VehicleType.EMERGENCY_DOCTOR_CAR,
            VehicleType.K9_POLICE_CAR
        )
    }

    /**
     * gets a list of vehicles on which special capacity type is applicable
     */
    fun getSpecialVehicles(vehicles: List<Vehicle>): List<Vehicle> {
        val requiredVehicle = vehicles.filter { vehicle ->
            vehicle.vehicleType in setOf(
                VehicleType.POLICE_CAR,
                VehicleType.AMBULANCE,
                VehicleType.FIRE_TRUCK_WATER,
                VehicleType.FIRE_TRUCK_LADDER
            )
        }
        return requiredVehicle
    }

    /**
     * calls the assign function on the vehicles which do not depend on capacity type
     */
    fun assignWithoutCapacity(vehicle: Vehicle, emergency: Emergency, isReallocation: Boolean) {
        val requiredVehicles = emergency.requiredVehicles

        // only proceed to the next step if the request still needs this vehicle
        if (requiredVehicles.containsKey(vehicle.vehicleType)) {
            assignVehicle(vehicle, emergency, isReallocation)
        }
    }

    /**
     * checks if the vehicle can be assigned to the emergency
     */
    fun canAssignVehicle(vehicle: Vehicle): Boolean {
        val assignedBase = dataHolder.vehiclesToBase[vehicle.id] ?: Base(1, 1, 1, mutableListOf())

        if (vehicle.vehicleType == VehicleType.K9_POLICE_CAR ||
            vehicle.vehicleType == VehicleType.EMERGENCY_DOCTOR_CAR
        ) {
            when (assignedBase) {
                is PoliceStation -> return assignedBase.dogs > 0 && assignedBase.staff >= vehicle.staffCapacity
                is Hospital -> return assignedBase.doctors > 0 && assignedBase.staff >= vehicle.staffCapacity
            }
        }

        // can only assign a vehicle if the base has enough staff to fill it
        return assignedBase.staff >= vehicle.staffCapacity
    }

    /**
     * assigns a vehicle to the emergency and updates the corresponding attributes
     */

    private fun assignVehicle(vehicle: Vehicle, emergency: Emergency, isReallocation: Boolean) {
        val graph = dataHolder.graph
        val base = dataHolder.vehiclesToBase[vehicle.id] ?: Base(1, 1, 1, mutableListOf())
        val path = getTimeToArrive(vehicle, emergency)
        val emergencyVertex = path.first
        val pathTicks = path.second

        // check if the car can reach in time and the base has enough vehicles to deal with the emergency
        if (pathTicks < emergency.maxDuration && canAssignVehicle(vehicle)) {
            // update the required vehicles for the request

            reduceEmergencyRequirment(vehicle, emergency)

            if (isReallocation) {
                Log.displayAssetReallocation(emergency.id, vehicle.id)
                val tempE = Emergency(1, EmergencyType.MEDICAL, 1, 1, 1, 1, " ", " ")
                val oldEmergency = dataHolder.vehicleToEmergency[vehicle.id] ?: tempE
                increaseOldEmergencyRequirment(oldEmergency, vehicle)
            } else {
                Log.displayAssetAllocation(vehicle.id, emergency.id, pathTicks)
                updateBase(base, vehicle)
            }
            // update the vehicle since its assigned an emergency
            vehicle.assignedEmergencyID = emergency.id
            vehicle.vehicleStatus = VehicleStatus.ASSIGNED_TO_EMERGENCY
            vehicle.currentRoute = graph.calculateShortestRoute(
                vehicle.lastVisitedVertex,
                emergencyVertex,
                vehicle.height
            ).toMutableList()
            vehicle.remainingRouteWeight = graph.weightOfRoute(
                vehicle.lastVisitedVertex,
                emergencyVertex,
                vehicle.height
            )
            // add the road it's currently on
            vehicle.currentRoad = vehicle.lastVisitedVertex.connectingRoads[vehicle.currentRoute[0].id]
            // add this vehicle to the list of active vehicles if it isn't already there
            if (vehicle !in dataHolder.activeVehicles) dataHolder.activeVehicles.add(vehicle)
            // add to the 'vehicle to emergency' mapping
            dataHolder.vehicleToEmergency[vehicle.id] = emergency

            emergency.emergencyStatus = EmergencyStatus.ONGOING
        }
    }

    /**
     * implements the logic of assigning a vehicle that depends on capacity type
     */
    fun assignBasedOnCapacity(vehicle: Vehicle, emergency: Emergency, isReallocation: Boolean) {
        val requiredVehicles = emergency.requiredVehicles
        if (requiredVehicles.isNotEmpty()) {
            if (vehicle.vehicleType in emergency.requiredVehicles) {
                when (vehicle) {
                    is FireTruckWater -> assignFireTruckWater(vehicle, emergency, isReallocation)
                    is FireTruckWithLadder -> assignFireTruckLadder(vehicle, emergency, isReallocation)
                    is Ambulance -> assignAmbulance(vehicle, emergency, isReallocation)
                    is PoliceCar -> assignPoliceCar(vehicle, emergency, isReallocation)
                }
            }
        }
    }

    /**
     * implements the logic of assigning a firetruck
     */
    private fun assignFireTruckWater(vehicle: FireTruckWater, emergency: Emergency, isReallocation: Boolean) {
        if (emergency.requiredCapacity.containsKey(CapacityType.WATER)) {
            val requiredNum = emergency.requiredVehicles[VehicleType.FIRE_TRUCK_WATER] ?: 0
            val requiredGallons = emergency.requiredCapacity[CapacityType.WATER] ?: 0
            val fireTruckCapacity = vehicle.currentWaterCapacity

            // if the vehicles aren't need anymore
            if (requiredNum == 0) {
                emergency.requiredCapacity.remove(CapacityType.WATER)
                emergency.requiredVehicles.remove(VehicleType.FIRE_TRUCK_WATER)
            } else if (requiredNum == 1) {
                // if one vehicle is required then it needs to have exact capacity
                if (fireTruckCapacity >= requiredGallons) {
                    // if the vehicle is assinged include the amount that was assigned
                    assignVehicle(vehicle, emergency, isReallocation)
                }
            } else {
                assignVehicle(vehicle, emergency, isReallocation)
            }
        }
    }

    /**
     * implements the logic of assigning a firetruck with ladder
     */
    private fun assignFireTruckLadder(vehicle: FireTruckWithLadder, emergency: Emergency, isReallocation: Boolean) {
        if (emergency.requiredCapacity.containsKey(CapacityType.LADDER_LENGTH)) {
            val requiredNum = emergency.requiredVehicles[VehicleType.FIRE_TRUCK_LADDER]
            val requiredLadderLen = emergency.requiredCapacity[CapacityType.LADDER_LENGTH]

            if ((requiredNum ?: 0) == 0) {
                emergency.requiredCapacity.remove(CapacityType.LADDER_LENGTH)
                emergency.requiredVehicles.remove(VehicleType.FIRE_TRUCK_LADDER)
            } else {
                // checking if this vehicle has the right length
                if (vehicle.ladderLength >= (requiredLadderLen ?: 0)) {
                    assignVehicle(vehicle, emergency, isReallocation)
                }
            }
        }
    }

    /**
     * implements the logic of assigning a police car
     */
    private fun assignPoliceCar(vehicle: PoliceCar, emergency: Emergency, isReallocation: Boolean) {
        val requiredNum = emergency.requiredVehicles[VehicleType.POLICE_CAR] ?: 0
        val requiredCriminalNum = emergency.requiredCapacity[CapacityType.CRIMINAL] ?: 0
        val currentCriminalCapacity = vehicle.maxCriminalCapacity - vehicle.currentCriminalCapcity

        if (requiredNum == 0) {
            emergency.requiredCapacity.remove(CapacityType.CRIMINAL)
            emergency.requiredVehicles.remove(VehicleType.POLICE_CAR)
        } else if (requiredCriminalNum == 1) {
            if (currentCriminalCapacity >= requiredCriminalNum) {
                assignVehicle(vehicle, emergency, isReallocation)
            }
        } else {
            assignVehicle(vehicle, emergency, isReallocation)
        }
    }

    /**
     * implements the logic of assigning an ambulance
     */
    private fun assignAmbulance(vehicle: Ambulance, emergency: Emergency, isReallocation: Boolean) {
        val requiredNum = emergency.requiredVehicles[VehicleType.AMBULANCE]

        if ((requiredNum ?: 0) == 0) {
            emergency.requiredCapacity.remove(CapacityType.PATIENT)
            emergency.requiredVehicles.remove(VehicleType.AMBULANCE)
        } else {
            // only assign the vehicle if it doesn't have a patient
            if (vehicle.hasPatient.not()) {
                assignVehicle(vehicle, emergency, isReallocation)
            }
        }
    }

    private fun reduceEmergencyRequirment(
        vehicle: Vehicle,
        emergency: Emergency
    ) {
        val requirment = emergency.requiredVehicles[vehicle.vehicleType] ?: 0
        emergency.requiredVehicles[vehicle.vehicleType] = requirment - 1
        val updatedRequirment = emergency.requiredVehicles[vehicle.vehicleType] ?: 0

        when (vehicle) {
            is PoliceCar -> {
                val capacity = vehicle.maxCriminalCapacity - vehicle.currentCriminalCapcity
                val req = emergency.requiredCapacity[CapacityType.CRIMINAL] ?: 0
                if (emergency.requiredCapacity.containsKey(CapacityType.CRIMINAL)) {
                    vehicle.assignedCriminalAmt = capacity
                    emergency.requiredCapacity[CapacityType.CRIMINAL] = req - capacity
                }
            }
            is FireTruckWater -> {
                val capacity = vehicle.currentWaterCapacity
                val req = emergency.requiredCapacity[CapacityType.WATER] ?: 0
                vehicle.assignedWaterAmt = capacity
                emergency.requiredCapacity[CapacityType.WATER] = req - capacity
            }
            is Ambulance -> {
                val req = emergency.requiredCapacity[CapacityType.PATIENT] ?: 0
                if (emergency.requiredCapacity.containsKey(CapacityType.PATIENT)) {
                    emergency.requiredCapacity[CapacityType.PATIENT] = req - 1
                }
            }
        }

        if (updatedRequirment == 0) {
            emergency.requiredVehicles.remove(vehicle.vehicleType)
        }
    }

    private fun increaseOldEmergencyRequirment(emergency: Emergency, vehicle: Vehicle) {
        // increase the amount by 1
        if (emergency.requiredVehicles.containsKey(vehicle.vehicleType)) {
            emergency.requiredVehicles[vehicle.vehicleType] = (emergency.requiredVehicles[vehicle.vehicleType] ?: 0) + 1
        } else {
            emergency.requiredVehicles[vehicle.vehicleType] = 1
        }
        when (vehicle) {
            is PoliceCar -> {
                if (emergency.emergencyType != EmergencyType.ACCIDENT) {
                    val req = emergency.requiredCapacity[CapacityType.CRIMINAL] ?: 0
                    if (emergency.requiredCapacity.containsKey(CapacityType.CRIMINAL)) {
                        emergency.requiredCapacity[CapacityType.CRIMINAL] = req + vehicle.assignedCriminalAmt
                    } else {
                        emergency.requiredCapacity[CapacityType.CRIMINAL] = vehicle.assignedCriminalAmt
                    }
                }
            }
            is FireTruckWater -> {
                val req = emergency.requiredCapacity[CapacityType.WATER] ?: 0
                if (emergency.requiredCapacity.containsKey(CapacityType.WATER)) {
                    emergency.requiredCapacity[CapacityType.WATER] = req + vehicle.assignedWaterAmt
                } else {
                    emergency.requiredCapacity[CapacityType.WATER] = vehicle.assignedWaterAmt
                }
            }
            is Ambulance -> {
                // capacity isn't increased for ambulance in certain scenarios
                checkAmbulaceIncrease(emergency)
            }
        }
    }

    private fun checkAmbulaceIncrease(emergency: Emergency) {
        val em1Check = emergency.emergencyType == EmergencyType.MEDICAL && emergency.severity == 1
        val em2Check = emergency.emergencyType == EmergencyType.CRIME && emergency.severity == 2
        if (!em1Check && !em2Check) {
            val req = emergency.requiredCapacity[CapacityType.PATIENT] ?: 0
            if (emergency.requiredCapacity.containsKey(CapacityType.PATIENT)) {
                emergency.requiredCapacity[CapacityType.PATIENT] = req + 1
            } else {
                emergency.requiredCapacity[CapacityType.WATER] = 1
            }
        }
    }

    private fun updateBase(base: Base, vehicle: Vehicle) {
        when (base) {
            is PoliceStation -> if (vehicle.vehicleType == VehicleType.K9_POLICE_CAR) base.dogs -= 1
            is Hospital -> if (vehicle.vehicleType == VehicleType.EMERGENCY_DOCTOR_CAR) base.doctors -= 1
        }

        base.staff -= vehicle.staffCapacity
    }

    private fun getTimeToArrive(vehicle: Vehicle, emergency: Emergency): Pair<Vertex, Int> {
        val lastVertex = vehicle.lastVisitedVertex
        val distanceFromLastVertex = vehicle.currentRouteWeightProgress - vehicle.weightTillLastVisitedVertex
        val emergencyPosition = emergency.location
        // calculate time to arrive at emergency at vertex 1
        val timeToArrive1 = weightToTicks(
            dataHolder.graph.weightOfRoute(
                lastVertex,
                emergencyPosition.first,
                vehicle.height
            ) + distanceFromLastVertex
        )
        // calculate time to arrive at emergency at vertex 2
        val timeToArrive2 = weightToTicks(
            dataHolder.graph.weightOfRoute(
                lastVertex,
                emergencyPosition.second,
                vehicle.height
            ) + distanceFromLastVertex
        )
        val pair1 = Pair(emergencyPosition.first, timeToArrive1)
        val pair2 = Pair(emergencyPosition.second, timeToArrive2)
        var resPair: Pair<Vertex, Int>
        resPair = if (timeToArrive1 <= timeToArrive2) pair1 else pair2

        // if we are not on a vertex, we must calculate from two vertices
        if (distanceFromLastVertex > 0 && vehicle.currentRoute.size > 1) {
            // get next vertex
            val nextVertex = vehicle.currentRoute[1]
            val distanceToNextVertex = max((vehicle.currentRoad?.weight ?: 0) - distanceFromLastVertex, 0)
            val timeToArrive3 = weightToTicks(
                dataHolder.graph.weightOfRoute(
                    nextVertex,
                    emergencyPosition.first,
                    vehicle.height
                ) + distanceToNextVertex
            )
            // calculate time to arrive at emergency at vertex 2
            val timeToArrive4 = weightToTicks(
                dataHolder.graph.weightOfRoute(
                    nextVertex,
                    emergencyPosition.second,
                    vehicle.height
                ) + distanceToNextVertex
            )

            val pair3 = Pair(emergencyPosition.first, timeToArrive3)
            val pair4 = Pair(emergencyPosition.second, timeToArrive4)
            var tempPair: Pair<Vertex, Int>
            tempPair = if (timeToArrive3 <= timeToArrive4) pair3 else pair4
            resPair = if (tempPair.second <= resPair.second) tempPair else resPair
        }
        // return maxOf(0, if (timeToArrive1 <= timeToArrive2) timeToArrive1 else timeToArrive2)
        // above code might fix "-214748364 ticks to arrive." issue but need checking
        return resPair
    }

    /**
     * Returns the weight as ticks need to travel
     */
    fun weightToTicks(weight: Int): Int {
        if (weight < Number.TEN) return 1
        return if (weight % Number.TEN == 0) {
            weight / Number.TEN // number is already a multiple of ten
        } else {
            (weight + (Number.TEN - weight % Number.TEN)) / Number.TEN // round up
        }
    }
}
