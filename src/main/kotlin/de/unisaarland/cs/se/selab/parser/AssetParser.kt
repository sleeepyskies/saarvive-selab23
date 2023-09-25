package de.unisaarland.cs.se.selab.parser

import de.unisaarland.cs.se.selab.dataClasses.bases.Base
import de.unisaarland.cs.se.selab.dataClasses.vehicles.Vehicle
import org.json.JSONObject
import java.io.File

/**
 * AssetParser is responsible for parsing and validating asset config files,
 * and also to create bases and vehicles.
 */
class AssetParser {
    /**
     * parse method parses an asset file to create a list of bases.
     *
     * @param file The asset configuration file.
     * @return A list of bases created from the asset configuration file.
     * @throws IllegalArgumentException if the asset configuration is invalid.
     */
    public fun parse(file: File): List<Base> {
        var blueprint: Map<String, String> = mutableMapOf() // comment this out later
        val jsonAssetObject = JSONObject(file.readText())
        val assetBlueprint = createBlueprint(jsonAssetObject)
        require(validateBlueprint(assetBlueprint)) { "Blueprint is invalid" }
        return createBaseList(blueprint)
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
