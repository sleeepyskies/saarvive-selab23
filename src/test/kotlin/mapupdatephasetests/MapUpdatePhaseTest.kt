package mapupdatephasetests

import de.unisaarland.cs.se.selab.dataClasses.bases.Base
import de.unisaarland.cs.se.selab.dataClasses.bases.FireStation
import de.unisaarland.cs.se.selab.dataClasses.bases.Hospital
import de.unisaarland.cs.se.selab.dataClasses.bases.PoliceStation
import de.unisaarland.cs.se.selab.dataClasses.events.*
import de.unisaarland.cs.se.selab.dataClasses.vehicles.*
import de.unisaarland.cs.se.selab.graph.*
import de.unisaarland.cs.se.selab.phases.MapUpdatePhase
import de.unisaarland.cs.se.selab.simulation.DataHolder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.w3c.dom.events.EventTarget

class MapUpdatePhaseTest {
    private lateinit var graph: Graph
    private lateinit var roads: List<Road>
    private lateinit var vertices: List<Vertex>
    private lateinit var bases: List<Base>
    private var construction: Construction = Construction(0, 1, 0, 2, 0, 1, true)
    private var roadClosure: RoadClosure = RoadClosure(0, 1, 0, 0, 1)
    private var rushHour: RushHour = RushHour(0, 1, 1, listOf(PrimaryType.MAIN_STREET), 2)
    private var trafficJam: TrafficJam = TrafficJam(0, 1, 0, 2, 0,  1)
    private var vehicleUnavailable: VehicleUnavailable = VehicleUnavailable(0, 1,  0, 0)

    @BeforeEach
    fun setUp() {
        val vertexA = Vertex(0, mutableMapOf())
        val vertexB = Vertex(1, mutableMapOf())
        val vertexC = Vertex(2, mutableMapOf())
        val roadAB = Road(PrimaryType.MAIN_STREET, SecondaryType.NONE, "Village", "roadAB", 10, 5)
        val roadBC = Road(PrimaryType.SIDE_STREET, SecondaryType.NONE, "Village", "roadBC", 10, 5)

        vertexA.connectingRoads[1] = roadAB
        vertexB.connectingRoads[0] = roadAB
        vertexB.connectingRoads[2] = roadBC
        vertexC.connectingRoads[1] = roadBC

        this.vertices = listOf(vertexA, vertexB)
        this.roads = listOf(roadAB)
        this.graph = Graph(vertices, roads)

        val fireVehicleA = FireTruckWater(VehicleType.FIRE_TRUCK_WATER, 0, 1, 2, 0, 600)
        val fireVehicleB = FireTruckWater(VehicleType.FIRE_TRUCK_WATER, 1, 1, 2, 0, 600)
        val fireVehicles: MutableList<Vehicle> = mutableListOf(fireVehicleA, fireVehicleB)

        val policeVehicleA = PoliceCar(VehicleType.POLICE_CAR, 2, 2, 2, 1 ,3)
        val policeVehicleB = PoliceCar(VehicleType.POLICE_CAR, 3, 2, 2, 1 ,3)
        val policeVehicles: MutableList<Vehicle> = mutableListOf(policeVehicleA, policeVehicleB)

        val medicalVehicleA = Ambulance(VehicleType.AMBULANCE, 4, 1, 2, 2)
        val medicalVehicleB = Ambulance(VehicleType.AMBULANCE, 5, 1, 2, 2)
        val medicalVehicles: MutableList<Vehicle> = mutableListOf(medicalVehicleA, medicalVehicleB)

        val fireStation = FireStation(0, 0, 5, fireVehicles)
        val policeStation = PoliceStation(1, 1, 10, 5, policeVehicles)
        val hospital = Hospital(2, 2, 10, 5, medicalVehicles)

        this.bases = listOf(fireStation, policeStation, hospital)
    }

    @Test
    fun noEventsTest() {
        val emptyGraph = Graph(emptyList(), emptyList())
        val dataHolder = DataHolder(emptyGraph, emptyList(), mutableListOf(), mutableListOf())
        val mup = MapUpdatePhase(dataHolder)
        mup.execute()
        assert(mup.currentTick == 1)
        assert(!mup.shouldReroute)
        assert(mup.events.isEmpty())
    }
}
