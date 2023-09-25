package de.unisaarland.cs.se.selab.graph

import de.unisaarland.cs.se.selab.graph.PrimaryType
import de.unisaarland.cs.se.selab.graph.SecondaryType


class Road(
    private val pType: PrimaryType, private val sType: SecondaryType,
    private val villageName: String, private val roadName: String, private val
    weight: Int, private val heightLimit: Int
) {
}