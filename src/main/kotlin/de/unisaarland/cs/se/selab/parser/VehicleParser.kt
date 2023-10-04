package de.unisaarland.cs.se.selab.parser

import de.unisaarland.cs.se.selab.dataClasses.bases.Base
import de.unisaarland.cs.se.selab.dataClasses.vehicles.Ambulance
import de.unisaarland.cs.se.selab.dataClasses.vehicles.FireTruckWater
import de.unisaarland.cs.se.selab.dataClasses.vehicles.FireTruckWithLadder
import de.unisaarland.cs.se.selab.dataClasses.vehicles.PoliceCar
import de.unisaarland.cs.se.selab.dataClasses.vehicles.Vehicle
import de.unisaarland.cs.se.selab.dataClasses.vehicles.VehicleType
import de.unisaarland.cs.se.selab.global.Number
import org.json.JSONObject

/**
 *
 */
class VehicleParser(
    private val json: JSONObject,
    private val setBaseId: Set<Int>,
    private val parsedBases: MutableList<Base>
) {

    private val vehicleIDSet = mutableSetOf<Int>()
    val parsedVehicles = mutableListOf<Vehicle>()

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

            val id = validateVehicleId(jsonVehicle.getInt("id"))
            val baseID = jsonVehicle.getInt("baseID")
            if (baseID !in setBaseId) {
                System.err.println("Vehicle's baseID must reference a valid Base ID")
                outputInvalidAndFinish()
            }
            val vehicleTypeString = jsonVehicle.getString("vehicleType") // just for validation
            val validatedVehicleType = validateVehicleType(vehicleTypeString) // just for validation
            val vehicleType = VehicleType.valueOf(validatedVehicleType)
            val vehicleHeight = validateVehicleHeight(jsonVehicle.getInt("vehicleHeight"))
            val staffCapacity = validateStaffCapacity(jsonVehicle.getInt("staffCapacity"))

            val vehicle: Vehicle = when (vehicleType) {
                VehicleType.POLICE_CAR -> PoliceCar(
                    vehicleType,
                    id,
                    staffCapacity,
                    vehicleHeight,
                    baseID,
                    maxCriminalCapacity = validateCriminalCapacity(jsonVehicle.getInt("criminalCapacity"))
                )

                VehicleType.K9_POLICE_CAR -> Vehicle(vehicleType, id, staffCapacity, vehicleHeight, baseID)
                VehicleType.POLICE_MOTORCYCLE -> Vehicle(vehicleType, id, staffCapacity, vehicleHeight, baseID)
                VehicleType.FIRE_TRUCK_WATER -> FireTruckWater(
                    vehicleType,
                    id,
                    staffCapacity,
                    vehicleHeight,
                    baseID,
                    maxWaterCapacity = validateWaterCapacity(jsonVehicle.getInt("waterCapacity"))
                )

                VehicleType.FIRE_TRUCK_TECHNICAL -> Vehicle(vehicleType, id, staffCapacity, vehicleHeight, baseID)
                VehicleType.FIRE_TRUCK_LADDER -> FireTruckWithLadder(
                    vehicleType,
                    id,
                    staffCapacity,
                    vehicleHeight,
                    baseID,
                    ladderLength = validateLadderLength(jsonVehicle.getInt("ladderLength"))
                )

                VehicleType.FIREFIGHTER_TRANSPORTER -> Vehicle(vehicleType, id, staffCapacity, vehicleHeight, baseID)
                VehicleType.AMBULANCE -> Ambulance(vehicleType, id, staffCapacity, vehicleHeight, baseID)
                VehicleType.EMERGENCY_DOCTOR_CAR -> Vehicle(vehicleType, id, staffCapacity, vehicleHeight, baseID)
            }

            parsedVehicles.add(vehicle)

            val correspondingBase = parsedBases.find { it.baseID == baseID }
            correspondingBase?.vehicles?.add(vehicle)
        }
    }

    private fun outputInvalidAndFinish() {
        // Log.displayInitializationInfoInvalid("VehicleParser")
        throw IllegalArgumentException("Invalid asset")
    }

    private fun validateVehicleId(id: Int): Int {
        if (id < 0) {
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
