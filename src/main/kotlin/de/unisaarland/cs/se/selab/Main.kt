package de.unisaarland.cs.se.selab

import de.unisaarland.cs.se.selab.global.Number
import de.unisaarland.cs.se.selab.simulation.SimulationObjectConstructor
import io.github.oshai.kotlinlogging.KotlinLogging.logger
/**
 * This is the entry point of the simulation.
 */
fun main(args: Array<String>) {
    val arguments = parseCommandLineArguments(args)

    if (arguments.help) {
        printUsage()
    }

    val simulationObjConstructor =
        SimulationObjectConstructor(arguments.mapFile, arguments.assetsFile, arguments.scenarioFile, arguments.maxTicks)
    val simulation = simulationObjConstructor.createSimulation()
    if (simulation != null) {
        simulation.start()
    }
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

    fun requireArgument() {
        if (i >= args.size) {
            System.err.println("Missing argument for ${args[i - 1]}")
        }
    }

    while (i < args.size) {
        when (args[i]) {
            "--map", "-m" -> {
                i++
                requireArgument()
                mapFile = args[i]
            }
            "--assets", "-a" -> {
                i++
                requireArgument()
                assetsFile = args[i]
            }
            "--scenario", "-s" -> {
                i++
                requireArgument()
                scenarioFile = args[i]
            }
            "--ticks", "-t" -> {
                i++
                requireArgument()
                ticks = args[i].toIntOrNull() ?: Number.ONE_HUNDRED
            }
            "--out", "-o" -> {
                i++
                requireArgument()
                outFile = args[i]
            }
            "--help", "-h" -> help = true
            else -> System.err.println("Unknown argument: ${args[i]}")
        }
        i++
    }
    return CommandLineArguments(mapFile, assetsFile, scenarioFile, ticks, outFile, help)
}

/** Prints the usage of the program
 */
fun printUsage() {
    logger(
        "Usage: Saarvive & Thrive"
    )
    logger("--map/-m <mapFile>")
    logger("--assets/-a <assetsFile>")
    logger("--scenario/-s <scenarioFile>")
    logger("--ticks/-t <ticks>")
    logger("--out/-o <outFile>")
    logger("--help/-h")
    System.err.println("Exiting...")
}
