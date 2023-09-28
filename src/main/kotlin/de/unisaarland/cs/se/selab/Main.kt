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

// parse the command line arguments
fun parseCommandLineArguments(args: Array<String>): CommandLineArguments {
    var mapFile = ""
    var assetsFile = ""
    var scenarioFile = ""
    var ticks: Int? = null // default value
    var outFile = "stdout" // default value
    var help = false
        var i = 0
        val argsize = args.size
        while (i < argsize) {
            when (args[i]) {
                "--map" -> {
                    i++
                    if (i < argsize) {
                        mapFile = args[i]
                    } else {
                        println("Error: --map requires an argument")
                    }
                }
                "--assets" -> {
                    i++
                    if (i < argsize) {
                        assetsFile = args[i]
                    } else {
                        println("Error: --assets requires an argument")
                    }
                }
                "--scenario" -> {
                    i++
                    if (i < argsize) {
                        scenarioFile = args[i]
                    } else {
                        println("Error: --scenario requires an argument")
                    }
                }
                "--ticks" -> {
                    i++
                    ticks = args[i].toIntOrNull() ?: Number.ONE_HUNDRED
                }
                "--out" -> {
                    i++
                    if (i < argsize) {
                        outFile = args[i]
                    } else {
                        println("Error: --out requires an argument")
                    }
                }
                "--help" -> {
                    help = true
                }
                else -> {
                    println("Error: Unknown argument ${args[i]}")
                }
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

        // check if the files have the required format, if yes -> parse them




