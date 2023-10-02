package allocationphasetests

import de.unisaarland.cs.se.selab.dataClasses.bases.Base
import de.unisaarland.cs.se.selab.dataClasses.bases.FireStation
import de.unisaarland.cs.se.selab.dataClasses.emergencies.Emergency
import de.unisaarland.cs.se.selab.dataClasses.emergencies.EmergencyStatus
import de.unisaarland.cs.se.selab.dataClasses.emergencies.EmergencyType
import de.unisaarland.cs.se.selab.dataClasses.events.VehicleUnavailable
import de.unisaarland.cs.se.selab.dataClasses.vehicles.FireTruckWater
import de.unisaarland.cs.se.selab.dataClasses.vehicles.Vehicle
import de.unisaarland.cs.se.selab.dataClasses.vehicles.VehicleType
import de.unisaarland.cs.se.selab.graph.Graph
import de.unisaarland.cs.se.selab.graph.PrimaryType
import de.unisaarland.cs.se.selab.graph.Road
import de.unisaarland.cs.se.selab.graph.SecondaryType
import de.unisaarland.cs.se.selab.graph.Vertex
import de.unisaarland.cs.se.selab.phases.AllocationPhase
import de.unisaarland.cs.se.selab.simulation.DataHolder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AllocationPhaseTest {

    private lateinit var graph: Graph
    private lateinit var allocationPhase: AllocationPhase
    private lateinit var dataHolder: DataHolder
    private val emergency = Emergency(
        0,
        EmergencyType.FIRE,
        1,
        0,
        1,
        20,
        "Village",
        "roadBC"
    )
    private lateinit var bases: List<Base>
    private lateinit var vehicles: List<Vehicle>

    @BeforeEach
    public fun setUp() {
        val vertexA = Vertex(0, mutableMapOf())
        val vertexB = Vertex(1, mutableMapOf())
        val vertexC = Vertex(2, mutableMapOf())

        val roadAB = Road(
            PrimaryType.MAIN_STREET,
            SecondaryType.NONE,
            "Village",
            "roadAB",
            10,
            5
        )

        val roadBC = Road(
            PrimaryType.SIDE_STREET,
            SecondaryType.NONE,
            "Village",
            "roadBC",
            10,
            5
        )

        vertexA.connectingRoads[1] = roadAB
        vertexB.connectingRoads[0] = roadAB

        vertexB.connectingRoads[2] = roadBC
        vertexC.connectingRoads[1] = roadBC

        this.graph = Graph(listOf(vertexA, vertexB, vertexC), listOf(roadAB, roadBC))

        val vehicle1 = FireTruckWater(
            VehicleType.FIRE_TRUCK_WATER,
            0,
            1,
            2,
            0,
            600
        )
        val vehicle2 = FireTruckWater(
            VehicleType.FIRE_TRUCK_WATER,
            1,
            1,
            2,
            0,
            600
        )
        val vehicle3 = FireTruckWater(
            VehicleType.FIRE_TRUCK_WATER,
            1,
            1,
            2,
            0,
            600
        )
        vehicle1.lastVisitedVertex = vertexA
        vehicle2.lastVisitedVertex = vertexA
        vehicle3.lastVisitedVertex = vertexA
        this.vehicles = listOf(vehicle1, vehicle2, vehicle3)
        this.bases = listOf(FireStation(0, 0, 5, vehicles.toMutableList()))
        val event = VehicleUnavailable(0, 1, 0, 2)
        this.dataHolder = DataHolder(this.graph, bases, mutableListOf(event), mutableListOf(emergency))
        this.allocationPhase = AllocationPhase(dataHolder)
        this.dataHolder.emergencyToBase[0] = bases[0]
    }

    @Test
    public fun allocationPhaseTest1() {
        // more setup
        this.emergency.emergencyStatus = EmergencyStatus.ASSIGNED
        dataHolder.ongoingEmergencies.add(dataHolder.emergencies[0])
        dataHolder.emergencies.clear()

        allocationPhase.execute()
        assert(dataHolder.ongoingEmergencies.contains(this.emergency))
        assert(dataHolder.emergencies.isEmpty())
        assert(dataHolder.emergencyToBase[0] == this.bases[0])
        val vehicleList = dataHolder.emergencyToVehicles[0]
        if (vehicleList != null) {
            assert(vehicleList.contains(vehicles[0]))
            assert(vehicleList.contains(vehicles[1]))
            assert(!vehicleList.contains(vehicles[2]))
        }
        // assert(dataHolder.vehicleToEmergency[0] == this.emergency)
        assert(dataHolder.vehicleToEmergency[1] == this.emergency)
        assert(dataHolder.vehicleToEmergency[2] != this.emergency)
    }
}
