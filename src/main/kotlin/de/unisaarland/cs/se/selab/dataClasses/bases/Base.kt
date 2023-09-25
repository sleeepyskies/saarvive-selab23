package de.unisaarland.cs.se.selab.dataClasses

abstract class Base
    (private val baseID: Int,
     private var staff: Int,
     private val vertexID: String,
     private val vehicles: List<Vehicle>)

class PoliceStation(
    private var dogs: Int,
    private val baseID: Int,
    private var staff: Int,
    private val vertexID: String,
    private val vehicles: List<Vehicle>
    ): Base(baseID, staff, vertexID, vehicles)

class Hospital(
    private var doctors: Int,
    private val baseID: Int,
    private var staff: Int,
    private val vertexID: String,
    private val vehicles: List<Vehicle>
): Base(baseID, staff, vertexID, vehicles)

class FireStation(
    private val baseID: Int,
    private var staff: Int,
    private val vertexID: String,
    private val vehicles: List<Vehicle>
): Base(baseID, staff, vertexID, vehicles)