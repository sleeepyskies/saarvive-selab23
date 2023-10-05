package allocationphasetests

import de.unisaarland.cs.se.selab.dataClasses.bases.FireStation
import de.unisaarland.cs.se.selab.dataClasses.bases.Hospital
import de.unisaarland.cs.se.selab.dataClasses.bases.PoliceStation
import de.unisaarland.cs.se.selab.dataClasses.emergencies.Emergency
import de.unisaarland.cs.se.selab.dataClasses.emergencies.EmergencyType
import de.unisaarland.cs.se.selab.dataClasses.vehicles.Ambulance
import de.unisaarland.cs.se.selab.dataClasses.vehicles.FireTruckWater
import de.unisaarland.cs.se.selab.dataClasses.vehicles.FireTruckWithLadder
import de.unisaarland.cs.se.selab.dataClasses.vehicles.PoliceCar
import de.unisaarland.cs.se.selab.dataClasses.vehicles.Vehicle
import de.unisaarland.cs.se.selab.dataClasses.vehicles.VehicleType
import de.unisaarland.cs.se.selab.graph.Graph
import de.unisaarland.cs.se.selab.graph.PrimaryType
import de.unisaarland.cs.se.selab.graph.Road
import de.unisaarland.cs.se.selab.graph.SecondaryType
import de.unisaarland.cs.se.selab.graph.Vertex
import de.unisaarland.cs.se.selab.phases.AllocationHelper
import de.unisaarland.cs.se.selab.simulation.DataHolder
import org.junit.jupiter.api.Test

class GetAssignableAssetsTest {

    // make graph
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

    val vertexA = Vertex(0, mutableMapOf(Pair(1, roadAB)))
    val vertexB = Vertex(1, mutableMapOf(Pair(0, roadAB), Pair(2, roadBC)))
    val vertexC = Vertex(2, mutableMapOf(Pair(1, roadBC)))

    val graph = Graph(listOf(vertexA, vertexB, vertexC), listOf(roadAB, roadBC))

    // define vehicles
    // Police Cars
    val policeCar = PoliceCar(
        vehicleType = VehicleType.POLICE_CAR,
        id = 1,
        staffCapacity = 4,
        height = 160,
        maxCriminalCapacity = 5,
        assignedBaseID = 2 // Assign to PoliceStation1
    )

    // Fire Trucks with Ladder
    val fireTruckWithLadder = FireTruckWithLadder(
        vehicleType = VehicleType.FIRE_TRUCK_LADDER,
        id = 4,
        staffCapacity = 6,
        height = 200,
        ladderLength = 8,
        assignedBaseID = 0 // Assign to FireStation1
    )

    // Fire Trucks with Water Capacity
    val fireTruckWater = FireTruckWater(
        vehicleType = VehicleType.FIRE_TRUCK_WATER,
        id = 7,
        staffCapacity = 5,
        height = 180,
        maxWaterCapacity = 600,
        assignedBaseID = 0 // Assign to FireStation1
    )

    // Ambulances
    val ambulance = Ambulance(
        vehicleType = VehicleType.AMBULANCE,
        id = 10,
        staffCapacity = 2,
        height = 170,
        assignedBaseID = 1 // Assign to Hospital1
    )

    // General Vehicles
    val fireTransporter = Vehicle(
        vehicleType = VehicleType.FIREFIGHTER_TRANSPORTER,
        id = 13,
        staffCapacity = 3,
        height = 150,
        assignedBaseID = 0 // Assign to FireStation1
    )
    // fireStation1.vehicles.add(vehicle1)

    val doctorCar = Vehicle(
        vehicleType = VehicleType.EMERGENCY_DOCTOR_CAR,
        id = 14,
        staffCapacity = 4,
        height = 160,
        assignedBaseID = 1 // Assign to Hospital1
    )
    // hospital1.vehicles.add(vehicle2)

    val policeMotorCycle = Vehicle(
        vehicleType = VehicleType.POLICE_MOTORCYCLE,
        id = 15,
        staffCapacity = 2,
        height = 170,
        assignedBaseID = 2 // Assign to PoliceStation1
    )
    // policeStation1.vehicles.add(vehicle3)

    val fireTechnical = Vehicle(
        vehicleType = VehicleType.FIRE_TRUCK_TECHNICAL,
        id = 16,
        staffCapacity = 5,
        height = 180,
        assignedBaseID = 3 // Assign to FireStation2
    )

    val k9 = Vehicle(
        vehicleType = VehicleType.K9_POLICE_CAR,
        id = 18,
        staffCapacity = 4,
        height = 200,
        assignedBaseID = 5 // Assign to PoliceStation2
    )

    val f1Vehicles = mutableListOf(
        fireTruckWithLadder,
        fireTruckWithLadder,
        fireTruckWater,
        fireTransporter,
        fireTechnical
    )
    val p1Vehicles = mutableListOf(policeCar, policeCar, policeMotorCycle, k9)
    val h1Vehicles = mutableListOf(ambulance, doctorCar)

    // Define the bases
    val fireStation1 = FireStation(0, 0, 1, f1Vehicles)
    val hospital1 = Hospital(1, 2, 2, 3, h1Vehicles)
    val policeStation1 = PoliceStation(2, 8, 3, 5, p1Vehicles)

    // Define data holder
    val dataHolder = DataHolder(graph, emptyList(), mutableListOf(), mutableListOf())

    val ap = AllocationHelper(dataHolder)

    @Test
    fun getAssignableAssetsTest1() {
        val emergency = Emergency(
            id = 1,
            emergencyType = EmergencyType.FIRE,
            severity = 1,
            startTick = 5,
            handleTime = 0,
            maxDuration = 10,
            villageName = "Village1",
            roadName = "Road1"
        )

        val res = ap.getAssignableAssets(fireStation1, emergency)
        assert(res == listOf(fireTruckWater))
    }

    @Test
    fun getAssignableAssetsTest2() {
        val emergency = Emergency(
            id = 1,
            emergencyType = EmergencyType.FIRE,
            severity = 1,
            startTick = 5,
            handleTime = 0,
            maxDuration = 10,
            villageName = "Village1",
            roadName = "Road1"
        )

        val res = ap.getAssignableAssets(fireStation1, emergency)
        assert(res == listOf(fireTruckWater))
    }

    @Test
    fun getAssignableAssetsTest3() {
        val emergency = Emergency(
            id = 1,
            emergencyType = EmergencyType.MEDICAL,
            severity = 3,
            startTick = 5,
            handleTime = 0,
            maxDuration = 10,
            villageName = "Village1",
            roadName = "Road1"
        )

        val res = ap.getAssignableAssets(hospital1, emergency)
        assert(res == listOf(ambulance, doctorCar))
    }

    @Test
    fun getAssignableAssetsTest4() {
        val emergency = Emergency(
            id = 1,
            emergencyType = EmergencyType.FIRE,
            severity = 3,
            startTick = 5,
            handleTime = 0,
            maxDuration = 10,
            villageName = "Village1",
            roadName = "Road1"
        )

        val res = ap.getAssignableAssets(hospital1, emergency)
        assert(res == listOf(ambulance, doctorCar))
    }
}
