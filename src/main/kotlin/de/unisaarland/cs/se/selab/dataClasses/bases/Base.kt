package de.unisaarland.cs.se.selab.dataClasses.bases
import de.unisaarland.cs.se.selab.dataClasses.vehicles.Vehicle

abstract class Base( // a
    private val baseID: Int,
    private var staff: Int,
    private val vertexID: String,
    private val vehicles: List<Vehicle>
)
