package de.unisaarland.cs.se.selab.phases

import de.unisaarland.cs.se.selab.dataClasses.Request
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
import de.unisaarland.cs.se.selab.global.Log
import de.unisaarland.cs.se.selab.simulation.DataHolder

/**
 * deals with all requests created in the current tick
 */
class RequestPhase(private val dataHolder: DataHolder) : Phase {
    /**
     * executes all the private functions based on logic
     */

    override fun execute() {
        if (requestExists()) {
            while (requestExists()) {
                val request = dataHolder.requests.first()
                // only go through the list of bases if more vehicles need to be requested
                for (baseID in request.baseIDsToVisit) if (request.requiredVehicles.isNotEmpty()) {
                    val base = dataHolder.bases.first { it.baseID == baseID }
                    val assignableVehicles = getAssignableAssets(base, request.requiredVehicles)
                    val normalVehicles = getNormalVehicles(assignableVehicles)
                    val specialVehicles = getSpecialVehicles(assignableVehicles)

                    assignWithoutCapacity(normalVehicles, request)
                    assignBasedOnCapacity(specialVehicles, request)
                } else {
                    break
                }

                // log request failing
                if (request.requiredVehicles.isNotEmpty()) {
                    Log.displayRequestFailed(request.emergencyID)
                }

                dataHolder.requests.remove(request)
            }
        }
    }

    /**
     * check if there are any requests in this tick
     */
    fun requestExists(): Boolean {
        return dataHolder.requests.isNotEmpty()
    }

    /**
     * find all the vehicles that are in the base
     */
    fun getAssignableAssets(base: Base, requestedVehicles: Map<VehicleType, Int>): List<Vehicle> {
        val requiredVehicle = base.vehicles.filter {
            it.vehicleStatus == VehicleStatus.IN_BASE && it.vehicleType in requestedVehicles && it.isAvailable
        }
        return requiredVehicle
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
    private fun assignWithoutCapacity(vehicles: List<Vehicle>, request: Request) {
        val requiredVehicles = request.requiredVehicles

        // only proceed to the next step if the request still needs this vehicle
        for (vehicle in vehicles) if (requiredVehicles.containsKey(vehicle.vehicleType)) {
            if (canAssignVehicle(vehicle)) assignVehicle(vehicle, request)
        }
    }

    private fun canAssignVehicle(vehicle: Vehicle): Boolean {
        val assignedBase = dataHolder.vehiclesToBase[vehicle.id] ?: Base(1, 1, 1, mutableListOf())

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

    private fun assignVehicle(vehicle: Vehicle, request: Request) {
        val emergency = dataHolder.ongoingEmergencies.first { it.id == request.emergencyID }
        val graph = dataHolder.graph
        val requiredVehicles = request.requiredVehicles
        val emergencyVertex = emergency.location.first
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
                // add this vehicle to the list of active vehicles
                dataHolder.activeVehicles.add(vehicle)
                Log.displayAssetAllocation(vehicle.id, emergency.id, shortestPath)
            }
        }
    }

    /**
     * implements the logic of assigning vehicles that depend on capacity type
     */
    private fun assignBasedOnCapacity(vehicles: List<Vehicle>, request: Request) {
        val requiredVehicles = request.requiredVehicles
        for (vehicle in vehicles) if (requiredVehicles.isNotEmpty()) {
            if (vehicle.vehicleType in request.requiredVehicles) {
                when (vehicle) {
                    is FireTruckWater -> assignFireTruckWater(vehicle, request)
                    is FireTruckWithLadder -> assignFireTruckLadder(vehicle, request)
                    is Ambulance -> assignAmbulance(vehicle, request)
                    is PoliceCar -> assignPoliceCar(vehicle, request)
                }
            } else {
                // if all the vehicles are assigned we don't need to go through the list anymore
                break
            }
        }
    }

    /**
     * implements the logic of assigning a firetruck
     */
    private fun assignFireTruckWater(vehicle: FireTruckWater, request: Request) {
        if (request.requiredCapacity.containsKey(CapacityType.WATER)) {
            val requiredNum = request.requiredVehicles[VehicleType.FIRE_TRUCK_WATER] ?: 0
            val requiredGallons = request.requiredCapacity[CapacityType.WATER] ?: 0
            val fireTruckCapacity = vehicle.maxWaterCapacity - vehicle.currentWaterCapacity

            // if the vehicles aren't need anymore
            if (requiredNum == 0) {
                request.requiredCapacity.remove(CapacityType.WATER)
                request.requiredVehicles.remove(VehicleType.FIRE_TRUCK_WATER)
            } else if (requiredNum == 1) {
                // if one vehicle is required then it needs to have exact capacity
                if (fireTruckCapacity >= requiredGallons) {
                    assignVehicle(vehicle, request)
                }
            } else {
                assignVehicle(vehicle, request)
            }
        }
    }

    /**
     * implements the logic of assigning a firetruck with ladder
     */
    private fun assignFireTruckLadder(vehicle: FireTruckWithLadder, request: Request) {
        if (request.requiredCapacity.containsKey(CapacityType.LADDER_LENGTH)) {
            val requiredNum = request.requiredVehicles[VehicleType.FIRE_TRUCK_LADDER]
            val requiredLadderLen = request.requiredCapacity[CapacityType.LADDER_LENGTH]

            if ((requiredNum ?: 0) == 0) {
                request.requiredCapacity.remove(CapacityType.LADDER_LENGTH)
                request.requiredVehicles.remove(VehicleType.FIRE_TRUCK_LADDER)
            } else {
                // checking if this vehicle has the right length
                if (vehicle.ladderLength >= (requiredLadderLen ?: 0)) {
                    assignVehicle(vehicle, request)
                }
            }
        }
    }

    /**
     * implements the logic of assigning a police car
     */
    private fun assignPoliceCar(vehicle: PoliceCar, request: Request) {
        val requiredNum = request.requiredVehicles[VehicleType.POLICE_CAR] ?: 0
        val requiredCriminalNum = request.requiredCapacity[CapacityType.CRIMINAL] ?: 0
        val currentCriminalCapacity = vehicle.maxCriminalCapacity - vehicle.currentCriminalCapcity

        if (currentCriminalCapacity >= requiredCriminalNum) {
            assignVehicle(vehicle, request)
            request.requiredCapacity[CapacityType.CRIMINAL] = requiredCriminalNum - 1
        }

        if (requiredNum == 0) {
            request.requiredCapacity.remove(CapacityType.CRIMINAL)
            request.requiredVehicles.remove(VehicleType.POLICE_CAR)
        } else if (requiredCriminalNum == 1) {
            if (currentCriminalCapacity >= requiredCriminalNum) {
                assignVehicle(vehicle, request)
            }
        } else {
            assignVehicle(vehicle, request)
        }
    }

    /**
     * implements the logic of assigning an ambulance
     */
    private fun assignAmbulance(vehicle: Ambulance, request: Request) {
        if (request.requiredCapacity.containsKey(CapacityType.PATIENT)) {
            val requiredNum = request.requiredVehicles[VehicleType.AMBULANCE]

            if ((requiredNum ?: 0) == 0) {
                request.requiredCapacity.remove(CapacityType.PATIENT)
                request.requiredVehicles.remove(VehicleType.AMBULANCE)
            } else {
                // only assign the vehicle if it doesn't have a patient
                if (vehicle.hasPatient.not()) {
                    assignVehicle(vehicle, request)
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
