package de.unisaarland.cs.se.selab.parser

import de.unisaarland.cs.se.selab.dataClasses.Base
import de.unisaarland.cs.se.selab.dataClasses.Vehicle

class AssetParser {
    public fun parseAsset():List<Base> {
        return emptyList()
    }

    protected fun createBluePrint(): Map <String, String> {
        val blueprint = mutableMapOf<String, String>()
        return blueprint
    }

    protected fun validateBlueprint(blueprint: Map<String, String>): Boolean {
        return true
    }

    protected fun createVehicle (blueprint: Map<String, String>): Vehicle {
        return Vehicle()
    }

    protected fun createBase (blueprint: Map<String, String>, vehicles: List<Vehicle>): Base {
        return Base()
    }

    protected fun createBaseList (bases: List<Base>): List<Base> {
        val bases = mutableListOf<Base>()
        return bases
    }

    // here it should take a blueprint as an argument, but it doesn't make sense
    protected fun createVehicleList (vehicles: List<Vehicle>): List<Vehicle> {
        val vehicles = mutableListOf<Vehicle>()
        return vehicles
    }

}