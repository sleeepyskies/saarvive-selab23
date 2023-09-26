package de.unisaarland.cs.se.selab.parser

import FireStation
import Hospital
import PoliceStation
import de.unisaarland.cs.se.selab.dataClasses.*
import de.unisaarland.cs.se.selab.dataClasses.bases.Base
import de.unisaarland.cs.se.selab.dataClasses.vehicles.*
import org.everit.json.schema.Schema
import org.everit.json.schema.loader.SchemaLoader
import org.json.JSONObject
import java.io.File

class AssetParser(private val baseFile: String, private val vehicleFile: String, private val jsonFile: String) {
    private val baseSchema: Schema
    private val vehicleSchema: Schema
    private val json: JSONObject

    init {
        val baseSchemaJson = JSONObject(File(baseFile).readText())
        baseSchema = SchemaLoader.load(baseSchemaJson)

        val vehicleSchemaJson = JSONObject(File(vehicleFile).readText())
        vehicleSchema = SchemaLoader.load(vehicleSchemaJson)

        val assetJsonData = File(jsonFile).readText()
        json = JSONObject(assetJsonData)
    }

    fun parse(): Pair<List<Base>, List<Vehicle>> {
        return Pair(parseBases(), parseVehicles())
    }

    private fun parseBases(): List<Base> {
        val parsedBases = mutableListOf<Base>()
        val basesArray = json.getJSONArray("bases")
        for (i in 0 until basesArray.length()) {
            val jsonBase = basesArray.getJSONObject(i)
            baseSchema.validate(jsonBase)

            val id = jsonBase.getInt("id")
            val baseType = jsonBase.getString("baseType")
            val location = jsonBase.getInt("location")
            val staff = jsonBase.getInt("staff")

            val base: Base = when (baseType) {
                "FIRE_STATION" -> FireStation(id, staff, location)
                "HOSPITAL" -> Hospital(id, staff, location, jsonBase.optInt("doctors", 0))
                "POLICE_STATION" -> PoliceStation(id, staff, location, jsonBase.optInt("dogs", 0))
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

            val id = jsonVehicle.getInt("id")
            val baseID = jsonVehicle.getInt("baseID")
            val vehicleType = VehicleType.valueOf(jsonVehicle.getString("vehicleType"))
            val vehicleHeight = jsonVehicle.getInt("vehicleHeight")
            val staffCapacity = jsonVehicle.getInt("staffCapacity")

            val vehicle: Vehicle = when (vehicleType) {
                VehicleType.POLICE_CAR -> PoliceCar(
                    vehicleType,
                    id,
                    staffCapacity,
                    vehicleHeight,
                    baseID,
                    jsonVehicle.getInt("criminalCapacity")
                )
                VehicleType.K9_POLICE_CAR -> K9PoliceCar(
                    vehicleType,
                    id,
                    staffCapacity,
                    vehicleHeight,
                    baseID
                )
                VehicleType.POLICE_MOTORCYCLE -> PoliceMotorcycle(
                    vehicleType,
                    id,
                    staffCapacity,
                    vehicleHeight,
                    baseID
                )
                VehicleType.FIRE_TRUCK_WATER -> FireTruckWater(
                    vehicleType,
                    id,
                    staffCapacity,
                    vehicleHeight,
                    baseID,
                    jsonVehicle.getInt("waterCapacity")
                )
                VehicleType.FIRE_TRUCK_TECHNICAL -> FireTruckTechnical(
                    vehicleType,
                    id,
                    staffCapacity,
                    vehicleHeight,
                    baseID
                )
                VehicleType.FIRE_TRUCK_LADDER -> FireTruckLadder(
                    vehicleType,
                    id,
                    staffCapacity,
                    vehicleHeight,
                    baseID,
                    jsonVehicle.getInt("ladderLength")
                )
                VehicleType.FIREFIGHTER_TRANSPORTER -> FirefighterTransporter(
                    vehicleType,
                    id,
                    staffCapacity,
                    vehicleHeight,
                    baseID
                )
                VehicleType.AMBULANCE -> Ambulance(
                    vehicleType,
                    id,
                    staffCapacity,
                    vehicleHeight,
                    baseID
                )
                VehicleType.EMERGENCY_DOCTOR_CAR -> EmergencyDoctorCar(
                    vehicleType,
                    id,
                    staffCapacity,
                    vehicleHeight,
                    baseID
                )
                else -> throw IllegalArgumentException("Invalid vehicleType: $vehicleType")
            }

            parsedVehicles.add(vehicle)
        }
        return parsedVehicles
    }
}
