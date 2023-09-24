package de.unisaarland.cs.se.selab.graph

enum class PrimaryType{
    MAIN_STREET,
    SIDE_STREET,
    COUNTY_ROAD
}

enum class SecondaryType{
    ONE_WAY_STREET,
    TUNNEL,
    NONE
}
class Road (private val pType: PrimaryType, private val sType: SecondaryType,
            private val villageName: String, private val roadName: String, private val
            weight: Int,private val heightLimit: Int) {
}