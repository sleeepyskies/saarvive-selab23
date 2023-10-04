package de.unisaarland.cs.se.selab.parser

import de.unisaarland.cs.se.selab.dataClasses.bases.Base
import de.unisaarland.cs.se.selab.dataClasses.bases.FireStation
import de.unisaarland.cs.se.selab.dataClasses.bases.Hospital
import de.unisaarland.cs.se.selab.dataClasses.bases.PoliceStation
import de.unisaarland.cs.se.selab.dataClasses.vehicles.Vehicle
import de.unisaarland.cs.se.selab.dataClasses.vehicles.VehicleType
import de.unisaarland.cs.se.selab.getSchema
import de.unisaarland.cs.se.selab.global.Log
import org.everit.json.schema.Schema
import org.json.JSONObject
import java.io.File

/**
* asset parser parses assets
*/
class AssetParser(private val assetSchemaFile: String, private val assetJsonFile: String) {
    private val assetSchema: Schema
    val json: JSONObject
    var fileName = "" // for logging

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
        val baseParser = BaseParser(json, this.fileName)
        baseParser.parseBases()
        val parsedBases = baseParser.parsedBases

        val vehicleParser = VehicleParser(json, this.fileName, baseParser.setBaseId, baseParser.parsedBases)
        vehicleParser.parseVehicles()
        val parsedVehicles = vehicleParser.parsedVehicles

        validateAtLeastOneBaseOfEachType(parsedBases)
        validateEachBaseHasAtLeastOneVehicle(parsedBases)
        validateVehiclesAtItsCorrectBases(parsedVehicles, parsedBases)

        // Log.displayInitializationInfoValid(this.fileName)
        return Pair(parsedVehicles, parsedBases)
    }
    private fun outputInvalidAndFinish() {
        Log.displayInitializationInfoInvalid(this.fileName)
        throw IllegalArgumentException("Invalid asset")
    }

    private fun validateAtLeastOneBaseOfEachType(parsedBases: MutableList<Base>) {
        val fireStations = parsedBases.filterIsInstance<FireStation>()
        val hospitals = parsedBases.filterIsInstance<Hospital>()
        val policeStations = parsedBases.filterIsInstance<PoliceStation>()

        if (fireStations.isEmpty() || hospitals.isEmpty() || policeStations.isEmpty()) {
            System.err.println("Not all base types are present in the assets.")
            System.err.println("Number of FireStations: ${fireStations.size}")
            System.err.println("Number of Hospitals: ${hospitals.size}")
            System.err.println("Number of PoliceStations: ${policeStations.size}")
            outputInvalidAndFinish()
        }
    }

    private fun validateEachBaseHasAtLeastOneVehicle(parsedBases: MutableList<Base>) {
        val basesWithoutVehicles = parsedBases.filter { it.vehicles.isEmpty() }

        if (basesWithoutVehicles.isNotEmpty()) {
            System.err.println("There are bases without any vehicles assigned.")
            outputInvalidAndFinish()
        }
    }

    private fun validateVehiclesAtItsCorrectBases(
        parsedVehicles: MutableList<Vehicle>,
        parsedBases: MutableList<Base>
    ) {
        parsedVehicles.forEach { vehicle ->
            val correspondingBase = parsedBases.find { it.baseID == vehicle.assignedBaseID }
            if (correspondingBase == null) {
                System.err.println("No base found for vehicle with id ${vehicle.id}")
                outputInvalidAndFinish()
            }

            when (vehicle.vehicleType) {
                VehicleType.POLICE_CAR, VehicleType.POLICE_MOTORCYCLE, VehicleType.K9_POLICE_CAR -> {
                    if (correspondingBase !is PoliceStation) {
                        System.err.println("Vehicle with id ${vehicle.id} should be at a Police Station")
                        outputInvalidAndFinish()
                    }
                }

                VehicleType.FIRE_TRUCK_WATER, VehicleType.FIRE_TRUCK_TECHNICAL,
                VehicleType.FIRE_TRUCK_LADDER, VehicleType.FIREFIGHTER_TRANSPORTER -> {
                    if (correspondingBase !is FireStation) {
                        System.err.println("Vehicle with id ${vehicle.id} should be at a Fire Station")
                        outputInvalidAndFinish()
                    }
                }

                VehicleType.AMBULANCE, VehicleType.EMERGENCY_DOCTOR_CAR -> {
                    if (correspondingBase !is Hospital) {
                        System.err.println("Vehicle with id ${vehicle.id} should be at a Hospital")
                        outputInvalidAndFinish()
                    }
                }
            }
        }
    }
}
