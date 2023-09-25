package de.unisaarland.cs.se.selab.dataClasses

import de.unisaarland.cs.se.selab.dataClasses.bases.Base
import de.unisaarland.cs.se.selab.dataClasses.vehicles.Vehicle


class PoliceStation (private val baseID:Int, private  val staff : Int, private val vertexID : String, private val vehicles : List<Vehicle>, private val dogs : Int): Base(baseID, staff, vertexID, vehicles){
}
