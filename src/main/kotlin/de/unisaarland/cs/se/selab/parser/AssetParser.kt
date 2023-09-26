package de.unisaarland.cs.se.selab.parser

import FireStation
import Hospital
import PoliceStation
import de.unisaarland.cs.se.selab.dataClasses.VehicleType
import de.unisaarland.cs.se.selab.dataClasses.bases.Base
import de.unisaarland.cs.se.selab.dataClasses.vehicles.Ambulance
import de.unisaarland.cs.se.selab.dataClasses.vehicles.FireTruckWater
import de.unisaarland.cs.se.selab.dataClasses.vehicles.FireTruckWithLadder
import de.unisaarland.cs.se.selab.dataClasses.vehicles.PoliceCar
import de.unisaarland.cs.se.selab.dataClasses.vehicles.Vehicle
import org.everit.json.schema.Schema
import org.everit.json.schema.loader.SchemaLoader
import org.json.JSONObject
import java.io.File
import kotlin.system.exitProcess

/**
* asset parser parses assets
*/
class AssetParser(private val baseFile: String, private val vehicleFile: String, private val jsonFile: String) {
    private val baseSchema: Schema
    private val vehicleSchema: Schema
    private val json: JSONObject
    private lateinit var allVehicles: List<Vehicle> // declare allVehicles as an member variable

    init {
        val baseSchemaJson = JSONObject(File(baseFile).readText())
        baseSchema = SchemaLoader.load(baseSchemaJson)

        val vehicleSchemaJson = JSONObject(File(vehicleFile).readText())
        vehicleSchema = SchemaLoader.load(vehicleSchemaJson)

        val assetJsonData = File(jsonFile).readText()
        json = JSONObject(assetJsonData)
    }

    /**
     * parse method returns a pair of list of bases and list of vehicles
     */
    fun parse(): Pair<List<Base>, List<Vehicle>> {
        allVehicles = parseVehicles()
        val allBases = parseBases()
        return Pair(allBases, allVehicles)
    }

    private fun parseBases(): List<Base> {
        val parsedBases = mutableListOf<Base>()
        val basesArray = json.getJSONArray("bases")
        for (i in 0 until basesArray.length()) {
            val jsonBase = basesArray.getJSONObject(i)
            baseSchema.validate(jsonBase)

            val id = validateBaseId(jsonBase.getInt("id"))
            val baseType = validateBaseType(jsonBase.getString("baseType"))
            val location = validateLocation(jsonBase.getInt("location"))
            val staff = validateStaff(jsonBase.getInt("staff"))
            val vehicles = parseVehicles().filter { it.assignedBaseID == id } // might be inefficient code

            val base: Base = when (baseType) {
                "FIRE_STATION" -> FireStation(id, staff, location, vehicles)
                "HOSPITAL" -> Hospital(id, staff, location, jsonBase.getInt("doctors"), vehicles)
                "POLICE_STATION" -> PoliceStation(id, staff, location, jsonBase.getInt("dogs"), vehicles)
                else -> throw IllegalArgumentException("Invalid baseType: $baseType")
            }

            parsedBases.add(base)
        }
        return parsedBases
    }

    private fun parseVehicles(): List<Vehicle> {
        val parsedVehicles = mutableListOf<Vehicle>()
        val vehiclesArray = json.getJSONArray("vehicles")
        for (i in 0 until vehiclesArray.length()) {
            val jsonVehicle = vehiclesArray.getJSONObject(i)
            vehicleSchema.validate(jsonVehicle)

            val id = validateVehicleId(jsonVehicle.getInt("id"))
            val baseID = validateBaseId(jsonVehicle.getInt("baseID"))
            val vehicleType = VehicleType.valueOf(jsonVehicle.getString("vehicleType"))
            val vehicleHeight = validateVehicleHeight(jsonVehicle.getInt("vehicleHeight"))
            val staffCapacity = validateStaffCapacity(jsonVehicle.getInt("staffCapacity"))

            val vehicle: Vehicle = when (vehicleType) {
                VehicleType.POLICE_CAR -> PoliceCar(
                    vehicleType,
                    id,
                    staffCapacity,
                    vehicleHeight,
                    baseID,
                    jsonVehicle.getInt("criminalCapacity")
                )
                VehicleType.K9_POLICE_CAR -> Vehicle(vehicleType, id, staffCapacity, vehicleHeight, baseID)
                VehicleType.POLICE_MOTORCYCLE -> Vehicle(vehicleType, id, staffCapacity, vehicleHeight, baseID)
                VehicleType.FIRE_TRUCK_WATER -> FireTruckWater(
                    vehicleType,
                    id,
                    staffCapacity,
                    vehicleHeight,
                    baseID,
                    jsonVehicle.getInt("waterCapacity")
                )
                VehicleType.FIRE_TRUCK_TECHNICAL -> Vehicle(vehicleType, id, staffCapacity, vehicleHeight, baseID)
                VehicleType.FIRE_TRUCK_LADDER -> FireTruckWithLadder(
                    vehicleType,
                    id,
                    staffCapacity,
                    vehicleHeight,
                    baseID,
                    jsonVehicle.getInt("ladderLength")
                )
                VehicleType.FIREFIGHTER_TRANSPORTER -> Vehicle(vehicleType, id, staffCapacity, vehicleHeight, baseID)
                VehicleType.AMBULANCE -> Ambulance(vehicleType, id, staffCapacity, vehicleHeight, baseID)
                VehicleType.EMERGENCY_DOCTOR_CAR -> Vehicle(vehicleType, id, staffCapacity, vehicleHeight, baseID)
            }

            parsedVehicles.add(vehicle)
        }
        return parsedVehicles
    }

    private fun validateBaseId(id: Int): Int {
        if (id <= 0) {
            System.err.println("Base ID must be positive")
            exitProcess(1)
        }
        return id
    }

    private fun validateBaseType(baseType: String): String {
        val validBaseTypes = listOf("FIRE_STATION", "HOSPITAL", "POLICE_STATION")
        if (baseType !in validBaseTypes) {
            System.err.println("Invalid base type")
            exitProcess(1)
        }
        return baseType
    }

    private fun validateLocation(location: Int): Int {
        if (location < 0) {
            System.err.println("Location must be non-negative")
            exitProcess(1)
        }
        return location
    }

    private fun validateStaff(staff: Int): Int {
        if (staff < 0) {
            System.err.println("Staff must be non-negative")
            exitProcess(1)
        }
        return staff
    }

    private fun validateVehicleId(id: Int): Int {
        if (id <= 0) {
            System.err.println("Vehicle ID must be positive")
            exitProcess(1)
        }
        return id
    }

    private fun validateVehicleHeight(height: Int): Int {
        if (height <= 0) {
            System.err.println("Vehicle height must be positive")
            exitProcess(1)
        }
        return height
    }

    private fun validateStaffCapacity(capacity: Int): Int {
        if (capacity <= 0) {
            System.err.println("Staff capacity must be positive")
            exitProcess(1)
        }
        return capacity
    }
}
