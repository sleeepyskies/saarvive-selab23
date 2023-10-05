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
import org.json.JSONException
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
            System.err.println("trying 'this.fileName = File(assetJsonFile).name' fails")
            outputInvalidAndFinish()
        }
        assetSchema = getSchema(this.javaClass, assetSchemaFile) ?: throw IllegalArgumentException("Schema not found")

        // Load the JSON data
        val assetJsonData = File(assetJsonFile).readText()
        json = JSONObject(assetJsonData)
        try {
            assetSchema.validate(json)
        } catch (_: Exception) {
            System.err.println("json validation fails")
            outputInvalidAndFinish()
        }
    }

    /**
     * parse Vehicles
     */
    fun parse(): Pair<MutableList<Vehicle>, MutableList<Base>> {
        val baseParser = BaseParser(json, this.fileName)
        try {
            baseParser.parseBases()
        } catch (_: JSONException) {
            System.err.println("JSONException thrown in parseBases()")
            outputInvalidAndFinish()
        }
        val parsedBases = baseParser.parsedBases

        val vehicleParser = VehicleParser(json, this.fileName, baseParser.setBaseId, baseParser.parsedBases)
        try {
            vehicleParser.parseVehicles()
        } catch (_: JSONException) {
            System.err.println("JSONException thrown in parseVehicles()")
            outputInvalidAndFinish()
        }
        val parsedVehicles = vehicleParser.parsedVehicles

        validateAtLeastOneBaseOfEachType(parsedBases, parsedVehicles)
        validateEachBaseHasAtLeastOneVehicle(parsedBases)
        validateVehiclesAtItsCorrectBases(parsedVehicles, parsedBases)

        // Log.displayInitializationInfoValid(this.fileName)
        return Pair(parsedVehicles, parsedBases)
    }

    private fun outputInvalidAndFinish() {
        Log.displayInitializationInfoInvalid(this.fileName)
        throw IllegalArgumentException("Invalid asset")
    }

    private fun validateAtLeastOneBaseOfEachType(parsedBases: MutableList<Base>, parsedVehicles: MutableList<Vehicle>) {
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

        policeStations.forEach { station ->
            if (station.dogs > 0) {
                val k9CarsAtBase = parsedVehicles.filter {
                    it.assignedBaseID == station.baseID && it.vehicleType == VehicleType.K9_POLICE_CAR
                }
                if (k9CarsAtBase.isEmpty()) {
                    System.err.println("PoliceStation with id ${station.baseID} has dogs but no K9_POLICE_CAR.")
                    outputInvalidAndFinish()
                }
            }
        }

        hospitals.forEach { hospital ->
            if (hospital.doctors > 0) {
                val edCarsAtBase = parsedVehicles.filter {
                    it.assignedBaseID == hospital.baseID && it.vehicleType == VehicleType.EMERGENCY_DOCTOR_CAR
                }
                if (edCarsAtBase.isEmpty()) {
                    System.err.println("Hospital with id ${hospital.baseID} has doctors but no EMERGENCY_DOCTOR_CAR.")
                    outputInvalidAndFinish()
                }
            }
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
                VehicleType.POLICE_CAR, VehicleType.POLICE_MOTORCYCLE -> correspondingBase?.let {
                    validatePoliceVehicle(
                        vehicle,
                        it
                    )
                }

                VehicleType.K9_POLICE_CAR -> correspondingBase?.let { validateK9PoliceCar(vehicle, it) }
                VehicleType.FIRE_TRUCK_WATER, VehicleType.FIRE_TRUCK_TECHNICAL,
                VehicleType.FIRE_TRUCK_LADDER, VehicleType.FIREFIGHTER_TRANSPORTER -> correspondingBase?.let {
                    validateFireVehicle(
                        vehicle,
                        it
                    )
                }

                VehicleType.AMBULANCE -> correspondingBase?.let { validateAmbulance(vehicle, it) }
                VehicleType.EMERGENCY_DOCTOR_CAR -> correspondingBase?.let { validateEmergencyDoctorCar(vehicle, it) }
            }
        }
    }

    private fun validatePoliceVehicle(vehicle: Vehicle, base: Base) {
        if (base !is PoliceStation) {
            System.err.println("Vehicle with id ${vehicle.id} should be at a Police Station")
            outputInvalidAndFinish()
        }
    }

    private fun validateK9PoliceCar(vehicle: Vehicle, base: Base) {
        if (base !is PoliceStation || base.dogs <= 0) {
            System.err.println(
                "Vehicle with id ${vehicle.id} is a K9 PC, but the associated Police Station has 0 dogs."
            )
            outputInvalidAndFinish()
        }
    }

    private fun validateFireVehicle(vehicle: Vehicle, base: Base) {
        if (base !is FireStation) {
            System.err.println("Vehicle with id ${vehicle.id} should be at a Fire Station")
            outputInvalidAndFinish()
        }
    }

    private fun validateAmbulance(vehicle: Vehicle, base: Base) {
        if (base !is Hospital) {
            System.err.println("Vehicle with id ${vehicle.id} should be at a Hospital")
            outputInvalidAndFinish()
        }
    }

    private fun validateEmergencyDoctorCar(vehicle: Vehicle, base: Base) {
        if (base !is Hospital || base.doctors <= 0) {
            System.err.println(
                "Vehicle with id ${vehicle.id} is an EDC, but the associated Hospital has 0 doctors."
            )
            outputInvalidAndFinish()
        }
    }
}
