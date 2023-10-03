package allocationphasetests


import de.unisaarland.cs.se.selab.dataClasses.bases.Base
import de.unisaarland.cs.se.selab.dataClasses.bases.PoliceStation
import de.unisaarland.cs.se.selab.dataClasses.emergencies.Emergency
import de.unisaarland.cs.se.selab.dataClasses.emergencies.EmergencyType
import de.unisaarland.cs.se.selab.dataClasses.vehicles.CapacityType
import de.unisaarland.cs.se.selab.dataClasses.vehicles.PoliceCar
import de.unisaarland.cs.se.selab.dataClasses.vehicles.VehicleStatus
import de.unisaarland.cs.se.selab.dataClasses.vehicles.VehicleType
import de.unisaarland.cs.se.selab.graph.Graph
import de.unisaarland.cs.se.selab.graph.PrimaryType
import de.unisaarland.cs.se.selab.graph.Road
import de.unisaarland.cs.se.selab.graph.SecondaryType
import de.unisaarland.cs.se.selab.graph.Vertex
import de.unisaarland.cs.se.selab.phases.AllocationPhase
import de.unisaarland.cs.se.selab.simulation.DataHolder
import org.junit.jupiter.api.Test

class CheckAndAssignTest {

    val r1 = Road(
        PrimaryType.SIDE_STREET,
        SecondaryType.NONE,
        "Bikini_Bottom",
        "r1",
        10,
        3
    )

    val r2 = Road(
        PrimaryType.SIDE_STREET,
        SecondaryType.NONE,
        "Bikini_Bottom",
        "r2",
        10,
        3
    )

    val r3 = Road(
        PrimaryType.SIDE_STREET,
        SecondaryType.NONE,
        "Bikini_Bottom",
        "r3",
        10,
        3
    )

    val v0 = Vertex(0, mutableMapOf(Pair(1, r1), Pair(2, r2)))
    val v1 = Vertex(1, mutableMapOf(Pair(0, r1), Pair(2, r3)))
    val v2 = Vertex(2, mutableMapOf(Pair(0, r2), Pair(1, r3)))

    val b1 = PoliceStation(1, 0, 12, 1, mutableListOf())
    val graph = Graph(listOf(v0, v1, v2), listOf(r1, r2, r3))
    val dataHolder = DataHolder(graph, emptyList<Base>(), mutableListOf(), mutableListOf())
    val ap = AllocationPhase(dataHolder)

    @Test
    fun test1() {
        val emergency = Emergency(
            id = 1,
            emergencyType = EmergencyType.CRIME,
            severity = 1,
            startTick = 5,
            handleTime = 1,
            maxDuration = 10,
            villageName = "Bikini_Bottom",
            roadName = "r3"
        )

        val vehicle = PoliceCar(
            vehicleType = VehicleType.POLICE_CAR,
            id = 1,
            staffCapacity = 4,
            height = 1,
            maxCriminalCapacity = 5,
            assignedBaseID = 1
        )

        val vehicleCapcity = Pair(CapacityType.CRIMINAL, 5)
        val requiredCapaity = mutableMapOf(Pair(CapacityType.CRIMINAL, 1))

        emergency.location = Pair(v1, v2)
        vehicle.lastVisitedVertex = v0

        ap.checkAndAssign(vehicleCapcity, requiredCapaity, vehicle, emergency)
        assert(vehicle.assignedBaseID == 1)
        assert(vehicle.assignedEmergencyID == 1)
        assert(vehicle.vehicleStatus == VehicleStatus.ASSIGNED_TO_EMERGENCY)
        // print(emergency.requiredCapacity[CapacityType.CRIMINAL])
        assert((emergency.requiredCapacity[CapacityType.CRIMINAL] ?: 1) <= 0)
        assert(dataHolder.vehicleToEmergency[vehicle.id] == emergency)
    }
}
