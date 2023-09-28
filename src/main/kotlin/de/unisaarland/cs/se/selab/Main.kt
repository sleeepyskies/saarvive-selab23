package de.unisaarland.cs.se.selab

import de.unisaarland.cs.se.selab.global.Number


/**
 * This is the entry point of the simulation.
 */
fun main(args: Array<String>) {

    var mapFile = ""
    var assetsFile = ""
    var scenarioFile = ""
    var ticks = Number.ONE_HUNDRED // default value
    var outFile = "stdout" // default value
    var help = false

    var i = 0
    val argsize = args.size
    while (i < argsize){
        when (args[i]) {
            "--map" ->{
                i++
                if (i < argsize){
                    mapFile = args[i]
                } else {
                    println("Error: --map requires an argument")
                }
            }
            "--assets" ->{
                i++
                if (i < argsize){
                    assetsFile = args[i]
                } else {
                    println("Error: --assets requires an argument")
                }
            }
            "--scenario" ->{
                i++
                if (i < argsize){
                    scenarioFile = args[i]
                } else {
                    println("Error: --scenario requires an argument")
                }
            }
            "--ticks" ->{
                i++
                ticks = args[i].toIntOrNull() ?: 1000
            }
            "--out" ->{
                i++
                if (i < argsize){
                    outFile = args[i]
                } else {
                    println("Error: --out requires an argument")
                }
            }
            "--help" ->{
                help = true
            }
            else ->{
                println("Error: Unknown argument ${args[i]}")
            }
        }
        i++
    }

    // if "help" is asked from the user, print the usage of the program
    if (help){
        println("Usage: Saarvive & Thrive " +
                "\n --map <mapFile> " +
                "\n --assets <assetsFile> " +
                "\n --scenario <scenarioFile> " +
                "\n --ticks <ticks> " +
                "\n --out <outFile> " +
                "\n --help")
    }

    // check if the files have the required format, if yes -> parse them




}
