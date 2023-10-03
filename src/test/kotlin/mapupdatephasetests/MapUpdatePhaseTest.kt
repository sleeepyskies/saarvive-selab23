package mapupdatephasetests

import de.unisaarland.cs.se.selab.dataClasses.bases.Base
import de.unisaarland.cs.se.selab.dataClasses.bases.FireStation
import de.unisaarland.cs.se.selab.dataClasses.bases.Hospital
import de.unisaarland.cs.se.selab.dataClasses.bases.PoliceStation
import de.unisaarland.cs.se.selab.dataClasses.emergencies.Emergency
import de.unisaarland.cs.se.selab.dataClasses.events.Event
import de.unisaarland.cs.se.selab.dataClasses.events.RushHour
import de.unisaarland.cs.se.selab.dataClasses.events.TrafficJam
import de.unisaarland.cs.se.selab.dataClasses.events.VehicleUnavailable
import de.unisaarland.cs.se.selab.dataClasses.events.RoadClosure
import de.unisaarland.cs.se.selab.dataClasses.events.Construction
import de.unisaarland.cs.se.selab.dataClasses.vehicles.Ambulance
import de.unisaarland.cs.se.selab.dataClasses.vehicles.FireTruckWater
import de.unisaarland.cs.se.selab.dataClasses.vehicles.PoliceCar
import de.unisaarland.cs.se.selab.dataClasses.vehicles.Vehicle
import de.unisaarland.cs.se.selab.dataClasses.vehicles.VehicleType
import de.unisaarland.cs.se.selab.graph.Graph
import de.unisaarland.cs.se.selab.graph.PrimaryType
import de.unisaarland.cs.se.selab.graph.Road
import de.unisaarland.cs.se.selab.graph.SecondaryType
import de.unisaarland.cs.se.selab.graph.Vertex
import de.unisaarland.cs.se.selab.phases.MapUpdatePhase
import de.unisaarland.cs.se.selab.simulation.DataHolder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MapUpdatePhaseTest {
    private lateinit var graph: Graph
    private lateinit var roads: List<Road>
    private lateinit var vertices: List<Vertex>
    private lateinit var bases: List<Base>

    private var construction1: Construction = Construction(0, 1, 0, 2, 0, 1, true)
    private var construction2: Construction = Construction(0, 1, 0, 2, 0, 1, false)
    private var roadClosure: RoadClosure = RoadClosure(1, 1, 0, 0, 1)
    private var rushHour: RushHour = RushHour(2, 1, 0, listOf(PrimaryType.MAIN_STREET), 2)
    private val trafficJam: TrafficJam = TrafficJam(3, 1, 0, 2, 0, 1)
    private val vehicleUnavailable: VehicleUnavailable = VehicleUnavailable(4, 1, 0, 0)
    private val events: MutableList<Event> = mutableListOf()
    private val emergencies: MutableList<Emergency> = mutableListOf()
    private lateinit var dataHolder: DataHolder
    private lateinit var mapUpdatePhase: MapUpdatePhase

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

        this.vertices = listOf(vertexA, vertexB, vertexC)
        this.roads = listOf(roadAB, roadBC)
        this.graph = Graph(vertices, roads)

        val fireVehicleA = FireTruckWater(VehicleType.FIRE_TRUCK_WATER, 0, 1, 2, 0, 600)
        val fireVehicleB = FireTruckWater(VehicleType.FIRE_TRUCK_WATER, 1, 1, 2, 0, 600)
        val fireVehicles: MutableList<Vehicle> = mutableListOf(fireVehicleA, fireVehicleB)

        val policeVehicleA = PoliceCar(VehicleType.POLICE_CAR, 2, 2, 2, 1, 3)
        val policeVehicleB = PoliceCar(VehicleType.POLICE_CAR, 3, 2, 2, 1, 3)
        val policeVehicles: MutableList<Vehicle> = mutableListOf(policeVehicleA, policeVehicleB)

        val medicalVehicleA = Ambulance(VehicleType.AMBULANCE, 4, 1, 2, 2)
        val medicalVehicleB = Ambulance(VehicleType.AMBULANCE, 5, 1, 2, 2)
        val medicalVehicles: MutableList<Vehicle> = mutableListOf(medicalVehicleA, medicalVehicleB)

        val fireStation = FireStation(0, 0, 5, fireVehicles)
        val policeStation = PoliceStation(1, 1, 10, 5, policeVehicles)
        val hospital = Hospital(2, 2, 10, 5, medicalVehicles)

        this.bases = listOf(fireStation, policeStation, hospital)

        this.dataHolder = DataHolder(this.graph, this.bases, this.events, this.emergencies)
        this.mapUpdatePhase = MapUpdatePhase(dataHolder)
    }

    @Test
    fun noEventsTest() {
        val emptyGraph = Graph(emptyList(), emptyList())
        val emptyDataHolder = DataHolder(emptyGraph, emptyList(), mutableListOf(), mutableListOf())
        val mup = MapUpdatePhase(emptyDataHolder)
        mup.execute()
        assert(mup.currentTick == 1)
        assert(!mup.shouldReroute)
    }

    @Test
    fun testVehicleUnavailable() {
        events.add(this.vehicleUnavailable)
        assert(dataHolder.events.isNotEmpty())
        assert(!mapUpdatePhase.shouldReroute)
        assert(mapUpdatePhase.currentTick == 0)
        assert(vehicleUnavailable.duration == 1)

        // Testing execute
        mapUpdatePhase.execute()
        assert(mapUpdatePhase.currentTick == 1)
        assert(!mapUpdatePhase.shouldReroute)
        assert(!this.bases[0].vehicles[0].isAvailable)
        assert(dataHolder.unavailableVehicles.contains(this.bases[0].vehicles[0].id))
        assert(vehicleUnavailable.duration == 0)
        assert(events.contains(vehicleUnavailable))

        mapUpdatePhase.execute()
        assert(mapUpdatePhase.currentTick == 2)
        assert(!mapUpdatePhase.shouldReroute)
        assert(this.bases[0].vehicles[0].isAvailable)
        assert(!dataHolder.unavailableVehicles.contains(this.bases[0].vehicles[0].id))
        assert(!events.contains(vehicleUnavailable))
    }


    @Test
    fun testTrafficJam() {
        events.add(this.trafficJam)
        assert(dataHolder.events.contains(trafficJam))
        assert(dataHolder.events.isNotEmpty())
        assert(!mapUpdatePhase.shouldReroute)
        assert(roads[0].weight == 10)
        assert(trafficJam.duration == 1)
        assert(mapUpdatePhase.currentTick == 0)

        // Testing execute
        mapUpdatePhase.execute()
        assert(dataHolder.events.contains(trafficJam))
        assert(dataHolder.events.isNotEmpty())
        assert(!mapUpdatePhase.shouldReroute)
        assert(roads[0].weight == 20)
        assert(trafficJam.duration == 0)
        assert(mapUpdatePhase.currentTick == 1)

        mapUpdatePhase.execute()
        assert(!dataHolder.events.contains(trafficJam))
        assert(dataHolder.events.isEmpty())
        assert(!mapUpdatePhase.shouldReroute)
        assert(roads[0].weight == 10)
        assert(trafficJam.duration == 0)
        assert(mapUpdatePhase.currentTick == 2)
    }

    @Test
    fun testRushHour() {
        events.add(this.rushHour)
        assert(dataHolder.events.contains(rushHour))
        assert(dataHolder.events.isNotEmpty())
        assert(!mapUpdatePhase.shouldReroute)
        assert(roads[0].weight == 10)
        assert(rushHour.duration == 1)
        assert(mapUpdatePhase.currentTick == 0)

        // Testing execute
        mapUpdatePhase.execute()
        assert(dataHolder.events.contains(rushHour))
        assert(dataHolder.events.isNotEmpty())
        assert(!mapUpdatePhase.shouldReroute)
        assert(roads[0].weight == 20)
        assert(rushHour.duration == 0)
        assert(mapUpdatePhase.currentTick == 1)

        mapUpdatePhase.execute()
        assert(!dataHolder.events.contains(rushHour))
        assert(dataHolder.events.isEmpty())
        assert(!mapUpdatePhase.shouldReroute)
        assert(roads[0].weight == 10)
        assert(rushHour.duration == 0)
        assert(mapUpdatePhase.currentTick == 2)
    }

    @Test
    fun testRoadClosure() {
        events.add(this.roadClosure)
        assert(dataHolder.events.contains(roadClosure))
        assert(dataHolder.events.isNotEmpty())
        assert(!mapUpdatePhase.shouldReroute)
        assert(roadClosure.duration == 1)
        assert(vertices[0].connectingRoads[1] == roads[0])
        assert(vertices[1].connectingRoads[0] == roads[0])

        // Testing execute
        mapUpdatePhase.execute()
        assert(dataHolder.events.contains(roadClosure))
        assert(dataHolder.events.isNotEmpty())
        assert(!mapUpdatePhase.shouldReroute)
        assert(roadClosure.duration == 0)
        assert(vertices[0].connectingRoads[1] != roads[0])
        assert(vertices[1].connectingRoads[0] != roads[0])

        mapUpdatePhase.execute()
        assert(!dataHolder.events.contains(roadClosure))
        assert(dataHolder.events.isEmpty())
        assert(!mapUpdatePhase.shouldReroute)
        assert(roadClosure.duration == 0)
        assert(vertices[0].connectingRoads[1] == roads[0])
        assert(vertices[1].connectingRoads[0] == roads[0])
    }

    @Test
    fun testConstruction1() {
        events.add(this.construction1)
        assert(dataHolder.events.contains(construction1))
        assert(dataHolder.events.isNotEmpty())
        assert(!mapUpdatePhase.shouldReroute)
        assert(construction1.duration == 1)
        assert(vertices[0].connectingRoads[1] == roads[0])
        assert(vertices[1].connectingRoads[0] == roads[0])
        assert(roads[0].weight == 10)

        // Testing execute
        mapUpdatePhase.execute()
        assert(dataHolder.events.contains(construction1))
        assert(dataHolder.events.isNotEmpty())
        assert(!mapUpdatePhase.shouldReroute)
        assert(construction1.duration == 0)
        assert(vertices[0].connectingRoads[1] != roads[0])
        assert(vertices[1].connectingRoads[0] == roads[0])
        assert(roads[0].weight == 20)

        mapUpdatePhase.execute()
        assert(dataHolder.events.contains(construction1))
        assert(dataHolder.events.isEmpty())
        assert(!mapUpdatePhase.shouldReroute)
        assert(construction1.duration == 0)
        assert(vertices[0].connectingRoads[1] == roads[0])
        assert(vertices[1].connectingRoads[0] == roads[0])
        assert(roads[0].weight == 10)
    }

    @Test
    fun testConstruction2() {
        events.add(this.construction2)
        assert(dataHolder.events.contains(construction2))
        assert(dataHolder.events.isNotEmpty())
        assert(!mapUpdatePhase.shouldReroute)
        assert(construction2.duration == 1)
        assert(vertices[0].connectingRoads[1] == roads[0])
        assert(vertices[1].connectingRoads[0] == roads[0])
        assert(roads[0].weight == 10)

        // Testing execute
        mapUpdatePhase.execute()
        assert(dataHolder.events.contains(construction2))
        assert(dataHolder.events.isNotEmpty())
        assert(!mapUpdatePhase.shouldReroute)
        assert(construction2.duration == 0)
        assert(vertices[0].connectingRoads[1] == roads[0])
        assert(vertices[1].connectingRoads[0] == roads[0])
        assert(roads[0].weight == 20)

        mapUpdatePhase.execute()
        assert(dataHolder.events.contains(construction2))
        assert(dataHolder.events.isEmpty())
        assert(!mapUpdatePhase.shouldReroute)
        assert(construction2.duration == 0)
        assert(vertices[0].connectingRoads[1] == roads[0])
        assert(vertices[1].connectingRoads[0] == roads[0])
        assert(roads[0].weight == 10)
    }
}
