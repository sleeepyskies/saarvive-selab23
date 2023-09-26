package de.unisaarland.cs.se.selab.parser

import FireStation
import Hospital
import de.unisaarland.cs.se.selab.dataClasses.PoliceStation
import de.unisaarland.cs.se.selab.dataClasses.bases.Base
import de.unisaarland.cs.se.selab.dataClasses.vehicles.Vehicle
import org.everit.json.schema.Schema
import org.everit.json.schema.loader.SchemaLoader
import org.json.JSONObject
import java.io.File

class AssetParser(private val schemaFile: String, private val jsonFile: String) {
    private val schema: Schema
    private val json: JSONObject

    init {
        val schemaJson = JSONObject(File(schemaFile).readText())
        schema = SchemaLoader.load(schemaJson)

        val jsonData = File(jsonFile).readText()
        json = JSONObject(jsonData)
    }

    fun parseBases(): List<Base> {
        val parsedBases = mutableListOf<Base>()
        val basesArray = json.getJSONArray("bases")
        for (i in 0 until basesArray.length()) {
            val jsonBase = basesArray.getJSONObject(i)
            schema.validate(jsonBase)

            val id = jsonBase.getInt("id")
            val baseType = jsonBase.getString("baseType")
            val staff = jsonBase.getInt("staff")
            val location = jsonBase.getInt("location")
            val vehicles = listOf<Vehicle>() // Replace with actual logic to populate vehicles

            val base: Base = when (baseType) {
                "FIRE_STATION" -> FireStation(id, staff, location, vehicles)
                "HOSPITAL" -> Hospital(id, staff, location, vehicles, jsonBase.optInt("doctors", 0))
                "POLICE_STATION" -> PoliceStation(id, staff, location, vehicles, jsonBase.optInt("dogs", 0))
                else -> throw ValidationException("Invalid baseType: $baseType")
            }

            parsedBases.add(base)
        }
        return parsedBases
    }

    fun parseVehicles(): List<Vehicle> {
        val parsedVehicles = mutableListOf<Vehicle>()
        val vehiclesArray = json.getJSONArray("vehicles")
        for (i in 0 until vehiclesArray.length()) {
            val jsonVehicle = vehiclesArray.getJSONObject(i)
            schema.validate(jsonVehicle)

            val vehicle = Vehicle(
                id = jsonVehicle.getInt("id"),
                baseID = jsonVehicle.getInt("baseID"),
                vehicleType = jsonVehicle.getString("vehicleType"),
                staffCapacity = jsonVehicle.getInt("staffCapacity"),
                criminalCapacity = jsonVehicle.optInt("criminalCapacity", 0),
                waterCapacity = jsonVehicle.optInt("waterCapacity", 0),
                ladderLength = jsonVehicle.optInt("ladderLength", 0),
                vehicleHeight = jsonVehicle.getInt("vehicleHeight")
            )
            parsedVehicles.add(vehicle)
        }
        return parsedVehicles
    }
}
