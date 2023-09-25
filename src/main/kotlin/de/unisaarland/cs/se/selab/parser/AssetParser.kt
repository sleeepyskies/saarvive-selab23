package de.unisaarland.cs.se.selab.parser

import de.unisaarland.cs.se.selab.dataClasses.Base
import de.unisaarland.cs.se.selab.dataClasses.Vehicle
import java.io.File

class AssetParser {
    public fun parse(file: File): List<Base> {
        val jsonObject = JSONObject(file.readText())
        val blueprint = createBlueprint(jsonObject)
        if (validateBlueprint(blueprint)) {
            return createBaseList(blueprint)
        } else {
            throw IllegalArgumentException("blueprint is invalid")
        }
    }

    private fun createBlueprint(jsonObject: JSONObject): Map<String, String> {
        val blueprint = mutableMapOf<String, String>()
        return blueprint
    }

    private fun validateBlueprint(blueprint: Map<String, String>): Boolean {
        return true
    }

    private fun createVehicle(blueprint: Map<String, String>): Vehicle? {
        return null
    }

    private fun createBase(blueprint: Map<String, String>, vehicles: List<Vehicle>): Base? {
        return null
    }

    private fun createBaseList(blueprint: Map<String, String>): List<Base> {
        val bases = mutableListOf<Base>()
        return bases
    }

    // here it should take a blueprint as an argument, but it doesn't make sense
    private fun createVehicleList(vehicles: List<Vehicle>): List<Vehicle> {
        val vehicles = mutableListOf<Vehicle>()
        return vehicles
    }
}
