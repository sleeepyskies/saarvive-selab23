package vehicleupdatephasetests

import de.unisaarland.cs.se.selab.dataClasses.bases.Base
import de.unisaarland.cs.se.selab.dataClasses.bases.FireStation
import de.unisaarland.cs.se.selab.dataClasses.bases.Hospital
import de.unisaarland.cs.se.selab.dataClasses.bases.PoliceStation
import de.unisaarland.cs.se.selab.dataClasses.emergencies.Emergency
import de.unisaarland.cs.se.selab.dataClasses.emergencies.EmergencyStatus
import de.unisaarland.cs.se.selab.dataClasses.emergencies.EmergencyType
import de.unisaarland.cs.se.selab.dataClasses.events.Event
import de.unisaarland.cs.se.selab.dataClasses.events.TrafficJam
import de.unisaarland.cs.se.selab.dataClasses.vehicles.Vehicle
import de.unisaarland.cs.se.selab.dataClasses.vehicles.FireTruckWater
import de.unisaarland.cs.se.selab.dataClasses.vehicles.VehicleType
import de.unisaarland.cs.se.selab.dataClasses.vehicles.PoliceCar
import de.unisaarland.cs.se.selab.dataClasses.vehicles.Ambulance
import de.unisaarland.cs.se.selab.dataClasses.vehicles.VehicleStatus
import de.unisaarland.cs.se.selab.graph.*
import de.unisaarland.cs.se.selab.phases.VehicleUpdatePhase
import de.unisaarland.cs.se.selab.simulation.DataHolder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LogTest {
    private lateinit var graph: Graph
    private lateinit var roads: List<Road>
    private lateinit var vertices: List<Vertex>
    private lateinit var bases: List<Base>

    private val trafficJam: TrafficJam = TrafficJam(3, 1, 0, 2, 0, 1)
    private val events: MutableList<Event> = mutableListOf(trafficJam)
    private val emergency: Emergency = Emergency(0, EmergencyType.FIRE, 1, 0, 1, 20, "Village", "roadBC")
    private val emergencies: MutableList<Emergency> = mutableListOf()
    private lateinit var dataHolder: DataHolder
    private lateinit var vehicleUpdatePhase: VehicleUpdatePhase
    private lateinit var fireVehicleA: FireTruckWater
    private lateinit var fireVehicleB: FireTruckWater

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

        this.fireVehicleA = FireTruckWater(VehicleType.FIRE_TRUCK_WATER, 0, 1, 2, 0, 600)
        this.fireVehicleB = FireTruckWater(VehicleType.FIRE_TRUCK_WATER, 1, 1, 2, 0, 600)
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
        this.vehicleUpdatePhase = VehicleUpdatePhase(dataHolder)
    }

    @Test
    fun testLogging(){
        emergency.emergencyStatus = EmergencyStatus.ONGOING
        dataHolder.activeVehicles.add(fireVehicleA)
        fireVehicleA.assignedEmergencyID = 0
        fireVehicleA.currentRouteWeightProgress = 0
        fireVehicleA.currentRoute = graph.calculateShortestRoute(vertices[0], vertices[1], fireVehicleA.height)
        fireVehicleA.remainingRouteWeight = graph.weightOfRoute(vertices[0], vertices[1], fireVehicleA.height)
        fireVehicleA.lastVisitedVertex = vertices[0]
        fireVehicleA.vehicleStatus = VehicleStatus.MOVING_TO_EMERGENCY
        dataHolder.vehicleToEmergency[fireVehicleA.id] = this.emergency
        vehicleUpdatePhase.execute()
    }
}
