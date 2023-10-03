package mapupdatephasetests

import de.unisaarland.cs.se.selab.dataClasses.bases.Base
import de.unisaarland.cs.se.selab.dataClasses.bases.FireStation
import de.unisaarland.cs.se.selab.dataClasses.bases.Hospital
import de.unisaarland.cs.se.selab.dataClasses.bases.PoliceStation
import de.unisaarland.cs.se.selab.dataClasses.emergencies.Emergency
import de.unisaarland.cs.se.selab.dataClasses.events.*
import de.unisaarland.cs.se.selab.dataClasses.vehicles.*
import de.unisaarland.cs.se.selab.graph.*
import de.unisaarland.cs.se.selab.phases.MapUpdatePhase
import de.unisaarland.cs.se.selab.simulation.DataHolder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MapUpdatePhaseTest {
    private lateinit var graph: Graph
    private lateinit var roads: List<Road>
    private lateinit var vertices: List<Vertex>
    private lateinit var bases: List<Base>
    private var construction: Construction = Construction(0, 1, 0, 2, 0, 1, true)
    private var roadClosure: RoadClosure = RoadClosure(1, 1, 0, 0, 1)
    private var rushHour: RushHour = RushHour(2, 1, 1, listOf(PrimaryType.MAIN_STREET), 2)
    private var trafficJam: TrafficJam = TrafficJam(3, 1, 0, 2, 0,  1)
    private var vehicleUnavailable: VehicleUnavailable = VehicleUnavailable(4, 1,  0, 0)
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
        assert(mup.events.isEmpty())
    }

    @Test
    fun testVehicleUnavailableMethods() {
        events.add(this.vehicleUnavailable)
        assert(dataHolder.events.isNotEmpty())

        // testing triggerEvent
        mapUpdatePhase.triggerEvent(this.events)
        assert(!mapUpdatePhase.shouldReroute)
        assert(!this.bases[0].vehicles[0].isAvailable)
        assert(dataHolder.unavailableVehicles.contains(this.bases[0].vehicles[0].id))

        // testing reduceEventDuration
        assert(this.vehicleUnavailable.duration == 1)
        mapUpdatePhase.reduceEventDuration(this.events)
        assert(this.vehicleUnavailable.duration == 0)
        mapUpdatePhase.reduceEventDuration(this.events)
        assert(this.events.isEmpty())
        assert(!dataHolder.unavailableVehicles.contains(this.bases[0].vehicles[0].id))
        assert(dataHolder.unavailableVehicles.isEmpty())
        assert(!mapUpdatePhase.shouldReroute)
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
}
