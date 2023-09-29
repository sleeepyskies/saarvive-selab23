package de.unisaarland.cs.se.selab.parser

import de.unisaarland.cs.se.selab.dataClasses.bases.Base
import de.unisaarland.cs.se.selab.dataClasses.bases.FireStation
import de.unisaarland.cs.se.selab.dataClasses.bases.Hospital
import de.unisaarland.cs.se.selab.dataClasses.bases.PoliceStation
import de.unisaarland.cs.se.selab.dataClasses.vehicles.Ambulance
import de.unisaarland.cs.se.selab.dataClasses.vehicles.FireTruckWater
import de.unisaarland.cs.se.selab.dataClasses.vehicles.FireTruckWithLadder
import de.unisaarland.cs.se.selab.dataClasses.vehicles.PoliceCar
import de.unisaarland.cs.se.selab.dataClasses.vehicles.Vehicle
import de.unisaarland.cs.se.selab.dataClasses.vehicles.VehicleType
import de.unisaarland.cs.se.selab.getSchema
import de.unisaarland.cs.se.selab.global.Number
import org.everit.json.schema.Schema
import org.json.JSONObject
import java.io.File

/**
* asset parser parses assets
*/
class AssetParser(private val assetSchemaFile: String, private val jsonFile: String) {
    private val assetSchema: Schema
    private val json: JSONObject
    public var allVehicles: List<Vehicle>

    init {
        // Load the asset schema only
        // val assetSchemaJson = JSONObject(File(assetSchemaFile).readText())
        assetSchema = getSchema(this.javaClass, assetSchemaFile) ?: throw IllegalArgumentException("Schema not found")

        // Load the JSON data
        val assetJsonData = File(jsonFile).readText()
        json = JSONObject(assetJsonData)

        assetSchema.validate(json)

        allVehicles = parseVehicles()
    }

    /**
     * parse Vehicles
     */
    private fun parseVehicles(): List<Vehicle> {
        val vehiclesArray = json.getJSONArray("vehicles")
        val parsedVehicles = mutableListOf<Vehicle>()
        for (i in 0 until vehiclesArray.length()) {
            val jsonVehicle = vehiclesArray.getJSONObject(i)
            // assetSchema.validate(jsonVehicle)

            val id = validateVehicleId(jsonVehicle.getInt("id"))
            val baseID = validateBaseId(jsonVehicle.getInt("baseID"))
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
        }
        return parsedVehicles
    }

    /**
     * parse Bases
     */
    fun parseBases(): List<Base> {
        val basesArray = json.getJSONArray("bases")
        val parsedBases = mutableListOf<Base>()
        for (i in 0 until basesArray.length()) {
            val jsonBase = basesArray.getJSONObject(i)
            // commented out to build
            // assetSchema.validate(jsonBase)

            val id = validateBaseId(jsonBase.getInt("id"))
            val baseType = validateBaseType(jsonBase.getString("baseType"))
            val location = validateLocation(jsonBase.getInt("location"))
            val staff = validateStaff(jsonBase.getInt("staff"))
            val vehicles = allVehicles.filter { it.assignedBaseID == id }

            val base: Base = when (baseType) {
                "FIRE_STATION" -> FireStation(id, location, staff, vehicles)
                "HOSPITAL" -> Hospital(id, location, staff, jsonBase.getInt("doctors"), vehicles)
                "POLICE_STATION" -> PoliceStation(id, location, staff, jsonBase.getInt("dogs"), vehicles)
                else -> throw IllegalArgumentException("Invalid baseType: $baseType")
            }

            parsedBases.add(base)
        }
        return parsedBases
    }

    private fun validateBaseId(id: Int): Int {
        require(id >= 0) { "Base ID must be positive" }
        return id
    }

    private fun validateBaseType(baseType: String): String {
        val validBaseTypes = listOf("FIRE_STATION", "HOSPITAL", "POLICE_STATION")
        require(baseType in validBaseTypes) { "Invalid base type" }
        return baseType
    }

    private fun validateLocation(location: Int): Int {
        require(location >= 0) { "Location must be non-negative" }
        return location
    }

    private fun validateStaff(staff: Int): Int {
        require(staff > 0) { "Staff must be non-negative and non-zero" }
        return staff
    }

    private fun validateVehicleId(id: Int): Int {
        require(id >= 0) { "Vehicle ID must be positive" }
        return id
    }

    private fun validateVehicleType(vehicleType: String): String {
        val validVehicleTypes = listOf(
            "POLICE_CAR", "K9_POLICE_CAR", "POLICE_MOTORCYCLE",
            "FIRE_TRUCK_WATER", "FIRE_TRUCK_TECHNICAL", "FIRE_TRUCK_LADDER", "FIREFIGHTER_TRANSPORTER",
            "AMBULANCE", "EMERGENCY_DOCTOR_CAR"
        )
        require(vehicleType in validVehicleTypes) { "Invalid vehicle type" }
        return vehicleType
    }

    private fun validateVehicleHeight(height: Int): Int {
        require(height in 1..Number.FIVE) { "Vehicle height must be between 1 and 5" }
        return height
    }

    private fun validateStaffCapacity(capacity: Int): Int {
        require(capacity in 1..Number.TWELVE) { "Staff capacity must be in bte 1 and 12" }
        return capacity
    }

    private fun validateCriminalCapacity(capacity: Int): Int {
        require(capacity in 1..Number.FOUR) { "Criminal capacity must be between 1 and 4" }
        return capacity
    }

    private fun validateWaterCapacity(capacity: Int): Int {
        require(
            capacity in listOf(Number.SIX_HUNDRED, Number.ONE_THOUSAND_TWO_HUNDRED, Number.TWO_THOUSAND_FOUR_HUNDRED)
        ) { "Water capacity must be one of 600, 1200, 2400" }
        return capacity
    }

    private fun validateLadderLength(length: Int): Int {
        require(length in Number.THIRTY..Number.SEVENTY) { "Ladder length must be between 30 and 70" }
        return length
    }
}
