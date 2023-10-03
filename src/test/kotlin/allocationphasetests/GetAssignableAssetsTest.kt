package allocationphasetests

import de.unisaarland.cs.se.selab.dataClasses.bases.FireStation
import de.unisaarland.cs.se.selab.dataClasses.bases.Hospital
import de.unisaarland.cs.se.selab.dataClasses.bases.PoliceStation

class GetAssignableAssetsTest {

    // bases
    val fireStation1 = FireStation(0, 0, 1, mutableListOf())
    val hospital1 = Hospital(1, 2, 2, 3, mutableListOf())
    val policeStation1 = PoliceStation(2, 8, 3, 5, mutableListOf())
    val fireStation2 = FireStation(3, 3, 4, mutableListOf())
    val hospital2 = Hospital(4, 5, 5, 6, mutableListOf())
    val policeStation2 = PoliceStation(5, 7, 6, 8, mutableListOf())
}
