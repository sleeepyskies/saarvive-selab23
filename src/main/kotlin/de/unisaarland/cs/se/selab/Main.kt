package de.unisaarland.cs.se.selab

import de.unisaarland.cs.se.selab.global.Number
import de.unisaarland.cs.se.selab.simulation.SimulationObjectConstructor


/**
 * This is the entry point of the simulation.
 */
fun main(args: Array<String>) {
    val arguments = parseCommandLineArguments(args)

    if (arguments.help) {
        printUsage()
    }

    if (!validateFilePath(arguments.mapFile, ".dot") ||
        !validateFilePath(arguments.assetsFile, ".json") ||
        !validateFilePath(arguments.scenarioFile, ".json")) {
        println("Error: Invalid file format")
    }

    val simulationObjConstructor =
        SimulationObjectConstructor(arguments.mapFile, arguments.assetsFile, arguments.scenarioFile, arguments.maxTicks)
    val simulation = simulationObjConstructor.createSimulation()
    simulation.start()

}

/** Data class for command line arguments */
data class CommandLineArguments(
    val mapFile: String,
    val assetsFile: String,
    val scenarioFile: String,
    val maxTicks: Int?,
    val outFile: String,
    val help: Boolean
)

/**
 * Parses the command line arguments and returns a [CommandLineArguments] object
 */
fun parseCommandLineArguments(args: Array<String>): CommandLineArguments {
    var mapFile = ""
    var assetsFile = ""
    var scenarioFile = ""
    var ticks: Int? = null // default value
    var outFile = "stdout" // default value
    var help = false
    var i = 0

    fun requireArgument(errorMessage: String) {
        if (i >= args.size) {
            println("Error: $errorMessage")
            return
        }
    }

    while (i < args.size) {
        when (args[i]) {
            "--map" -> {
                i++
                requireArgument("Missing value for --map")
                mapFile = args[i]
            }
            "--assets" -> {
                i++
                requireArgument("Missing value for --assets")
                assetsFile = args[i]
            }
            "--scenario" -> {
                i++
                requireArgument("Missing value for --scenario")
                scenarioFile = args[i]
            }
            "--ticks" -> {
                i++
                requireArgument("Missing value for --ticks")
                ticks = args[i].toIntOrNull() ?: Number.ONE_HUNDRED
            }
            "--out" -> {
                i++
                requireArgument("Missing value for --out")
                outFile = args[i]
            }
            "--help" -> help = true
            else -> println("Error: Unknown argument ${args[i]}")
        }
        i++
    }

    return CommandLineArguments(mapFile, assetsFile, scenarioFile, ticks, outFile, help)
}


/**
 * Prints the usage of the program
 */
fun printUsage() {
    println(
        "Usage: Saarvive & Thrive " +
                "\n --map <mapFile> " +
                "\n --assets <assetsFile> " +
                "\n --scenario <scenarioFile> " +
                "\n --ticks <ticks> " +
                "\n --out <outFile> " +
                "\n --help")
}

/**
 * Checks if the files have the required format, if yes -> parse them
 */
fun validateFilePath(filePath: String, requiredExtension: String): Boolean {
    return filePath.endsWith(requiredExtension)
}





