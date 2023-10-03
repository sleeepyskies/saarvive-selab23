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
import de.unisaarland.cs.se.selab.global.Log
import de.unisaarland.cs.se.selab.global.Number
import org.everit.json.schema.Schema
import org.json.JSONObject
import java.io.File
import java.util.logging.Logger

/**
* asset parser parses assets
*/
class AssetParser(assetSchemaFile: String, assetJsonFile: String) {
    private val assetSchema: Schema
    private val json: JSONObject
    private var fileName = "" // for logging
    private val setBaseId = mutableSetOf<Int>()
    private val setBaseLocation = mutableSetOf<Int>()
    val parsedVehicles = mutableListOf<Vehicle>()
    val parsedBases = mutableListOf<Base>()

    // for validation of unique IDs
    // private val baseIDSet = mutableSetOf<Int>()
    private val vehicleIDSet = mutableSetOf<Int>()

    // T0D0 DONE Parse bases first and then vehicles, in a way that bases are parsed first
    //       without adding in vehicles yet, then when parse vehicles, we add in the vehicles
    //       into the base according to their base ID. this way we can validate in another way too
    // T0D0 Return something when something invalid is parsed

    init {
        // Load the asset schema only
        // val assetSchemaJson = JSONObject(File(assetSchemaFile).readText())
        try {
            this.fileName = File(assetJsonFile).name
        } catch (_: Exception) {
            outputInvalidAndFinish()
        }
        assetSchema = getSchema(this.javaClass, assetSchemaFile) ?: throw IllegalArgumentException("Schema not found")

        // Load the JSON data
        val assetJsonData = File(assetJsonFile).readText()
        json = JSONObject(assetJsonData)
        try {
            assetSchema.validate(json)
        } catch (_: Exception) {
            outputInvalidAndFinish()
        }
    }

    /**
     * parse Vehicles
     */
    fun parse(): Pair<MutableList<Vehicle>, MutableList<Base>> {
        parseBases() // Parse bases first
        parseVehiclesInternal() // Then parse vehicles
        validateAtLeastOneBaseOfEachType() // Validate base types
        validateEachBaseHasAtLeastOneVehicle() // Validate vehicles per base
        validateVehiclesAtItsCorrectBases(parsedBases, parsedVehicles)
        Log.displayInitializationInfoValid(this.fileName)
        return Pair(parsedVehicles, parsedBases)
    }

    /**
     * parse Vehicles helper function
     */
    private fun parseVehiclesInternal() {
        val vehiclesArray = json.getJSONArray("vehicles")
        if (vehiclesArray.length() == 0) {
            Logger.getLogger("No vehicles found")
            outputInvalidAndFinish()
        }
        // val parsedVehicles = mutableListOf<Vehicle>()
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

            val correspondingBase = parsedBases.find { it.baseID == baseID }
            correspondingBase?.vehicles?.add(vehicle)
        }
    }

    /**
     * parse Bases
     */
    fun parseBases() {
        val basesArray = json.getJSONArray("bases")
        if (basesArray.length() == 0) {
            Logger.getLogger("No bases found")
            outputInvalidAndFinish()
        }
        // val parsedBases = mutableListOf<Base>()
        for (i in 0 until basesArray.length()) {
            val jsonBase = basesArray.getJSONObject(i)
            // commented out to build
            // assetSchema.validate(jsonBase)

            val id = validateBaseId(jsonBase.getInt("id"))
            val baseType = validateBaseType(jsonBase.getString("baseType"))
            val location = validateLocation(jsonBase.getInt("location"))
            val staff = validateStaff(jsonBase.getInt("staff"))
            val vehicles = mutableListOf<Vehicle>() // Initialize as an empty mutable list

            val base: Base = when (baseType) {
                "FIRE_STATION" -> FireStation(id, location, staff, vehicles)
                "HOSPITAL" -> Hospital(id, location, staff, jsonBase.getInt("doctors"), vehicles)
                "POLICE_STATION" -> PoliceStation(id, location, staff, jsonBase.getInt("dogs"), vehicles)
                else -> throw IllegalArgumentException("Invalid baseType: $baseType")
            }
            parsedBases.add(base)
        }
    }

    /**
     * Outputs invalidity log, terminates the program
     */
    private fun outputInvalidAndFinish() {
        Log.displayInitializationInfoInvalid(this.fileName)
        throw IllegalArgumentException("Invalid asset")
    }

    private fun validateBaseId(id: Int): Int {
        if (id < 0) {
            Logger.getLogger("Base ID must be positive")
            outputInvalidAndFinish()
        } else if (id in setBaseId) {
            Logger.getLogger("Base ID must be unique")
            outputInvalidAndFinish()
        } else {
            setBaseId.add(id)
        }
        return id
    }

    private fun validateBaseType(baseType: String): String {
        val validBaseTypes = listOf("FIRE_STATION", "HOSPITAL", "POLICE_STATION")
        if (baseType !in validBaseTypes) {
            Logger.getLogger("Invalid base type")
            outputInvalidAndFinish()
        }
        return baseType
    }

    private fun validateLocation(location: Int): Int {
        if (location < 0) {
            System.err.println("Location must be non-negative")
            outputInvalidAndFinish()
        } else if (location in setBaseLocation) {
            System.err.println("Location must be unique")
            outputInvalidAndFinish()
        } else {
            setBaseLocation.add(location)
        }
        return location
    }

    private fun validateStaff(staff: Int): Int {
        if (staff <= 0) {
            Logger.getLogger("Staff must be positive")
            outputInvalidAndFinish()
        }
        return staff
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
            Logger.getLogger("Criminal capacity must be between 1 and 4")
            outputInvalidAndFinish()
        }
        return capacity
    }

    private fun validateWaterCapacity(capacity: Int): Int {
        val validWaterCapacities =
            listOf(Number.SIX_HUNDRED, Number.ONE_THOUSAND_TWO_HUNDRED, Number.TWO_THOUSAND_FOUR_HUNDRED)
        if (capacity !in validWaterCapacities) {
            Logger.getLogger("Water capacity must be one of 600, 1200, 2400")
            outputInvalidAndFinish()
        }
        return capacity
    }

    private fun validateLadderLength(length: Int): Int {
        if (length !in Number.THIRTY..Number.SEVENTY) {
            Logger.getLogger("Ladder length must be between 30 and 70")
            outputInvalidAndFinish()
        }
        return length
    }

    private fun validateAtLeastOneBaseOfEachType() {
        val fireStations = parsedBases.filterIsInstance<FireStation>()
        val hospitals = parsedBases.filterIsInstance<Hospital>()
        val policeStations = parsedBases.filterIsInstance<PoliceStation>()

        if (fireStations.isEmpty() || hospitals.isEmpty() || policeStations.isEmpty()) {
            Logger.getLogger("Not all base types are present in the assets.")
            outputInvalidAndFinish()
        }
    }

    private fun validateEachBaseHasAtLeastOneVehicle() {
        val basesWithoutVehicles = parsedBases.filter { it.vehicles.isEmpty() }

        if (basesWithoutVehicles.isNotEmpty()) {
            Logger.getLogger("There are bases without any vehicles assigned.")
            outputInvalidAndFinish()
        }
    }

    private fun validateVehiclesAtItsCorrectBases(allBases: List<Base>, allVehicles: List<Vehicle>) {
        allVehicles.forEach { vehicle ->
            val correspondingBase = allBases.find { it.baseID == vehicle.assignedBaseID }
            requireNotNull(correspondingBase != null) { "No base found for vehicle with id ${vehicle.id}" }

            when (vehicle.vehicleType) {
                VehicleType.POLICE_CAR, VehicleType.POLICE_MOTORCYCLE, VehicleType.K9_POLICE_CAR -> {
                    require(
                        correspondingBase is PoliceStation
                    ) { "Vehicle with id ${vehicle.id} should be at a Police Station" }
                }

                VehicleType.FIRE_TRUCK_WATER, VehicleType.FIRE_TRUCK_TECHNICAL,
                VehicleType.FIRE_TRUCK_LADDER, VehicleType.FIREFIGHTER_TRANSPORTER -> {
                    require(
                        correspondingBase is FireStation
                    ) { "Vehicle with id ${vehicle.id} should be at a Fire Station" }
                }

                VehicleType.AMBULANCE, VehicleType.EMERGENCY_DOCTOR_CAR -> {
                    require(correspondingBase is Hospital) { "Vehicle with id ${vehicle.id} should be at a Hospital" }
                }
            }
        }
    }
}
