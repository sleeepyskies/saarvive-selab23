package de.unisaarland.cs.se.selab.graph

class Road(
    private val pType: PrimaryType,
    private val sType: SecondaryType,
    private val villageName: String,
    private val roadName: String,
    internal var weight: Int,
    internal val heightLimit: Int
)
