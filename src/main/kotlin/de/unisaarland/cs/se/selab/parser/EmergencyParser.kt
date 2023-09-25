package de.unisaarland.cs.se.selab.parser

import de.unisaarland.cs.se.selab.dataClasses.emergencies.Emergency

class EmergencyParser {
    public fun parse(): Emergency? {
//        return Emergency()
        return null
    }

    private fun createBlueprint(): List<String> {
        return listOf()
    }

    private fun validateBlueprint(blueprint: List<String>): Boolean {
        return true
    }

    private fun createEmergency(emergency: String): Emergency? {
        return null
    }

    private fun createEmergencyList(emergencies: List<String>): List<Emergency> {
        val emergencies = mutableListOf<Emergency>()
        return emergencies
    }

}