package de.unisaarland.cs.se.selab.parser

import de.unisaarland.cs.se.selab.dataClasses.bases.Base
import de.unisaarland.cs.se.selab.dataClasses.bases.FireStation
import de.unisaarland.cs.se.selab.dataClasses.bases.Hospital
import de.unisaarland.cs.se.selab.dataClasses.bases.PoliceStation
import de.unisaarland.cs.se.selab.dataClasses.vehicles.Vehicle
import de.unisaarland.cs.se.selab.global.Log
import org.json.JSONObject

/**
 *
 */
class BaseParser(private val json: JSONObject) {

    val setBaseId = mutableSetOf<Int>()
    private val setBaseLocation = mutableSetOf<Int>()
    val parsedBases = mutableListOf<Base>()

    companion object {
        private const val DOGS = "dogs"
        private const val DOCTORS = "doctors"
    }

    /**
     *
     */
    fun parseBases() {
        val basesArray = json.getJSONArray("bases")
        if (basesArray.length() == 0) {
            System.err.println("No bases found")
            outputInvalidAndFinish()
        }
        for (i in 0 until basesArray.length()) {
            val jsonBase = basesArray.getJSONObject(i)

            val id = validateBaseId(jsonBase.getInt("id"))
            val baseType = validateBaseType(jsonBase.getString("baseType"))
            val location = validateLocation(jsonBase.getInt("location"))
            val staff = validateStaff(jsonBase.getInt("staff"))
            val vehicles = mutableListOf<Vehicle>() // Initialize as an empty mutable list

            val base: Base = when (baseType) {
                "FIRE_STATION" -> {
                    if (jsonBase.has(DOGS) || jsonBase.has(DOCTORS)) {
                        System.err.println("FIRE STATION should not have $DOGS or $DOCTORS properties")
                        outputInvalidAndFinish()
                    }
                    FireStation(id, location, staff, vehicles)
                }
                "HOSPITAL" -> {
                    if (jsonBase.has(DOGS)) {
                        System.err.println("HOSPITAL should not have $DOGS property")
                        outputInvalidAndFinish()
                    }
                    val doctors = validateNonNegative(jsonBase.getInt(DOCTORS), DOCTORS)
                    Hospital(id, location, staff, doctors, vehicles)
                }
                "POLICE_STATION" -> {
                    if (jsonBase.has(DOCTORS)) {
                        System.err.println("POLICE STATION should not have $DOCTORS property")
                        outputInvalidAndFinish()
                    }
                    val dogs = validateNonNegative(jsonBase.getInt(DOGS), DOGS)
                    PoliceStation(id, location, staff, dogs, vehicles)
                }
                else -> throw IllegalArgumentException("Invalid baseType: $baseType")
            }
            parsedBases.add(base)
        }
    }

    /**
     * Outputs invalidity log, terminates the program
     */
    private fun outputInvalidAndFinish() {
        Log.displayInitializationInfoInvalid("BaseParser")
        throw IllegalArgumentException("Invalid asset")
    }

    /**
     * Validates base Id
     */
    fun validateBaseId(id: Int): Int {
        if (id < 0) {
            System.err.println("Base ID must be positive")
            outputInvalidAndFinish()
        } else if (id in setBaseId) {
            System.err.println("Base ID must be unique")
            outputInvalidAndFinish()
        } else {
            setBaseId.add(id)
        }
        return id
    }

    /**
     * Validates base type
     */
    fun validateBaseType(baseType: String): String {
        val validBaseTypes = listOf("FIRE_STATION", "HOSPITAL", "POLICE_STATION")
        if (baseType !in validBaseTypes) {
            System.err.println("Invalid base type")
            outputInvalidAndFinish()
        } else if (baseType == "") {
            System.err.println("Base type must not be empty")
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
            System.err.println("Staff must be positive")
            outputInvalidAndFinish()
        }
        return staff
    }

    /**
     * makes sure dogs and doctors are non-negative
     */
    private fun validateNonNegative(value: Int, propertyName: String): Int {
        if (value < 0) {
            System.err.println("$propertyName must be non-negative")
            outputInvalidAndFinish()
        }
        return value
    }
}
