package de.unisaarland.cs.se.selab.parser

import de.unisaarland.cs.se.selab.dataClasses.bases.Base
import de.unisaarland.cs.se.selab.dataClasses.vehicles.FireTruckWater
import de.unisaarland.cs.se.selab.dataClasses.vehicles.FireTruckWithLadder
import de.unisaarland.cs.se.selab.dataClasses.vehicles.PoliceCar
import de.unisaarland.cs.se.selab.dataClasses.vehicles.Vehicle
import de.unisaarland.cs.se.selab.dataClasses.vehicles.VehicleType
import de.unisaarland.cs.se.selab.global.Log
import de.unisaarland.cs.se.selab.global.Number
import org.json.JSONObject

/**
 *
 */
class VehicleParser(
    private val json: JSONObject,
    private val fileName: String,
    private val setBaseId: Set<Int>,
    private val parsedBases: MutableList<Base>
) {

    private val vehicleIDSet = mutableSetOf<Int>()
    val parsedVehicles = mutableListOf<Vehicle>()

    companion object {
        const val WATER_CAPACITY = "waterCapacity"
        const val LADDER_LENGTH = "ladderLength"
        const val CRIMINAL_CAPACITY = "criminalCapacity"
        const val POLICE_CAR_ERROR = "Police Car should not have water capacity or ladder length properties"
        const val FIRE_TRUCK_WATER_ERROR = "Fire Truck Water should not have ladder length or criminal cap properties"
        const val FIRE_TRUCK_LADDER_ERROR = "Fire Truck Ladder should not have water capacity or criminal cap property"
    }

    /**
     *
     */
    fun parseVehicles() {
        val vehiclesArray = json.getJSONArray("vehicles")
        if (vehiclesArray.length() == 0) {
            System.err.println("No vehicles found")
            outputInvalidAndFinish()
        }
        for (i in 0 until vehiclesArray.length()) {
            val jsonVehicle = vehiclesArray.getJSONObject(i)
            val vehicle = createVehicleFromJson(jsonVehicle)
            parsedVehicles.add(vehicle)
            val correspondingBase = parsedBases.find { it.baseID == vehicle.assignedBaseID }
            correspondingBase?.vehicles?.add(vehicle)
        }
    }

    private fun createVehicleFromJson(jsonVehicle: JSONObject): Vehicle {
        val id = validateVehicleId(jsonVehicle.getInt("id"))
        val baseID = jsonVehicle.getInt("baseID")
        if (baseID !in setBaseId) {
            System.err.println("Vehicle's baseID must reference a valid Base ID")
            outputInvalidAndFinish()
        }
        val vehicleTypeString = validateVehicleType(jsonVehicle.getString("vehicleType"))
        val vehicleType = VehicleType.valueOf(vehicleTypeString)
        val vehicleHeight = validateVehicleHeight(jsonVehicle.getInt("vehicleHeight"))
        val staffCapacity = validateStaffCapacity(jsonVehicle.getInt("staffCapacity"))

        return when (vehicleType) {
            VehicleType.POLICE_CAR -> createPoliceCar(
                jsonVehicle,
                vehicleType,
                id,
                staffCapacity,
                vehicleHeight,
                baseID
            )
            VehicleType.FIRE_TRUCK_WATER -> createFireTruckWater(
                jsonVehicle,
                vehicleType,
                id,
                staffCapacity,
                vehicleHeight,
                baseID
            )
            VehicleType.FIRE_TRUCK_LADDER -> createFireTruckLadder(
                jsonVehicle,
                vehicleType,
                id,
                staffCapacity,
                vehicleHeight,
                baseID
            )
            VehicleType.K9_POLICE_CAR,
            VehicleType.POLICE_MOTORCYCLE,
            VehicleType.FIRE_TRUCK_TECHNICAL,
            VehicleType.FIREFIGHTER_TRANSPORTER,
            VehicleType.EMERGENCY_DOCTOR_CAR,
            VehicleType.AMBULANCE -> {
                validateNormalVehicleProperties(jsonVehicle, vehicleType)
                Vehicle(vehicleType, id, staffCapacity, vehicleHeight, baseID)
            }
        }
    }

    private fun createPoliceCar(
        jsonVehicle: JSONObject,
        vehicleType: VehicleType,
        id: Int,
        staffCapacity: Int,
        vehicleHeight: Int,
        baseID: Int
    ): PoliceCar {
        validatePoliceCarProperties(jsonVehicle)
        return PoliceCar(
            vehicleType,
            id,
            staffCapacity,
            vehicleHeight,
            baseID,
            maxCriminalCapacity = validateCriminalCapacity(jsonVehicle.getInt("criminalCapacity"))
        )
    }

    private fun validatePoliceCarProperties(jsonVehicle: JSONObject) {
        if (jsonVehicle.has(WATER_CAPACITY) || jsonVehicle.has(LADDER_LENGTH)) {
            System.err.println(POLICE_CAR_ERROR)
            outputInvalidAndFinish()
        }
    }

    private fun createFireTruckWater(
        jsonVehicle: JSONObject,
        vehicleType: VehicleType,
        id: Int,
        staffCapacity: Int,
        vehicleHeight: Int,
        baseID: Int
    ): FireTruckWater {
        validateFireTruckWaterProperties(jsonVehicle)
        return FireTruckWater(
            vehicleType,
            id,
            staffCapacity,
            vehicleHeight,
            baseID,
            maxWaterCapacity = validateWaterCapacity(jsonVehicle.getInt(WATER_CAPACITY))
        )
    }

    private fun validateFireTruckWaterProperties(jsonVehicle: JSONObject) {
        if (jsonVehicle.has(LADDER_LENGTH) || jsonVehicle.has(CRIMINAL_CAPACITY)) {
            System.err.println(FIRE_TRUCK_WATER_ERROR)
            outputInvalidAndFinish()
        }
    }

    private fun createFireTruckLadder(
        jsonVehicle: JSONObject,
        vehicleType: VehicleType,
        id: Int,
        staffCapacity: Int,
        vehicleHeight: Int,
        baseID: Int
    ): FireTruckWithLadder {
        validateFireTruckLadderProperties(jsonVehicle)
        return FireTruckWithLadder(
            vehicleType,
            id,
            staffCapacity,
            vehicleHeight,
            baseID,
            ladderLength = validateLadderLength(jsonVehicle.getInt(LADDER_LENGTH))
        )
    }

    private fun validateFireTruckLadderProperties(jsonVehicle: JSONObject) {
        if (jsonVehicle.has(WATER_CAPACITY) || jsonVehicle.has(CRIMINAL_CAPACITY)) {
            System.err.println(FIRE_TRUCK_LADDER_ERROR)
            outputInvalidAndFinish()
        }
    }

    private fun validateNormalVehicleProperties(jsonVehicle: JSONObject, vehicleType: VehicleType) {
        val invalidAttributes = listOf(WATER_CAPACITY, LADDER_LENGTH, CRIMINAL_CAPACITY)
        for (attribute in invalidAttributes) {
            if (jsonVehicle.has(attribute)) {
                System.err.println("$vehicleType should not have $attribute property")
                outputInvalidAndFinish()
            }
        }
    }

    private fun outputInvalidAndFinish() {
        Log.displayInitializationInfoInvalid(this.fileName)
        throw IllegalArgumentException("Invalid asset")
    }

    private fun validateVehicleId(id: Int): Int {
        if (id < 0 || id > Number.TOO_BIG) {
            System.err.println("Vehicle ID must be positive")
            outputInvalidAndFinish()
        } else if (id in vehicleIDSet) {
            System.err.println("Vehicle ID must be unique")
            outputInvalidAndFinish()
        } else {
            vehicleIDSet.add(id)
        }
        return id
    }

    private fun validateVehicleType(vehicleType: String): String {
        val validVehicleTypes = listOf(
            "POLICE_CAR", "K9_POLICE_CAR", "POLICE_MOTORCYCLE",
            "FIRE_TRUCK_WATER", "FIRE_TRUCK_TECHNICAL", "FIRE_TRUCK_LADDER", "FIREFIGHTER_TRANSPORTER",
            "AMBULANCE", "EMERGENCY_DOCTOR_CAR"
        )
        if (vehicleType !in validVehicleTypes) {
            System.err.println("Invalid vehicle type")
            outputInvalidAndFinish()
        }
        return vehicleType
    }

    private fun validateVehicleHeight(height: Int): Int {
        if (height !in 1..Number.FIVE) {
            System.err.println("Vehicle height must be between 1 and 5")
            outputInvalidAndFinish()
        }
        return height
    }

    private fun validateStaffCapacity(capacity: Int): Int {
        if (capacity !in 1..Number.TWELVE) {
            System.err.println("Staff capacity must be between 1 and 12")
            outputInvalidAndFinish()
        }
        return capacity
    }

    private fun validateCriminalCapacity(capacity: Int): Int {
        if (capacity !in 1..Number.FOUR) {
            System.err.println("Criminal capacity must be between 1 and 4")
            outputInvalidAndFinish()
        }
        return capacity
    }

    private fun validateWaterCapacity(capacity: Int): Int {
        val validWaterCapacities =
            listOf(Number.SIX_HUNDRED, Number.ONE_THOUSAND_TWO_HUNDRED, Number.TWO_THOUSAND_FOUR_HUNDRED)
        if (capacity !in validWaterCapacities) {
            System.err.println("Water capacity must be one of 600, 1200, 2400")
            outputInvalidAndFinish()
        }
        return capacity
    }

    private fun validateLadderLength(length: Int): Int {
        if (length !in Number.THIRTY..Number.SEVENTY) {
            System.err.println("Ladder length must be between 30 and 70")
            outputInvalidAndFinish()
        }
        return length
    }
}
