package de.unisaarland.cs.se.selab.graph

/**
 * an edge on the graph
 * it stores the weight and other identifying attributes
 */
data class Road(
    private val pType: PrimaryType,
    private val sType: SecondaryType,
    private val villageName: String,
    private val roadName: String,
    private val weight: Int,
    private val heightLimit: Int
)
