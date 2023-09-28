package phasetests

import de.unisaarland.cs.se.selab.dataClasses.bases.Base
import de.unisaarland.cs.se.selab.dataClasses.bases.FireStation
import de.unisaarland.cs.se.selab.dataClasses.bases.Hospital
import de.unisaarland.cs.se.selab.dataClasses.bases.PoliceStation
import de.unisaarland.cs.se.selab.dataClasses.emergencies.Emergency
import de.unisaarland.cs.se.selab.dataClasses.emergencies.EmergencyStatus
import de.unisaarland.cs.se.selab.dataClasses.emergencies.EmergencyType
import de.unisaarland.cs.se.selab.dataClasses.events.Event
import de.unisaarland.cs.se.selab.dataClasses.vehicles.Vehicle
import de.unisaarland.cs.se.selab.graph.Graph
import de.unisaarland.cs.se.selab.graph.PrimaryType
import de.unisaarland.cs.se.selab.graph.Road
import de.unisaarland.cs.se.selab.graph.SecondaryType
import de.unisaarland.cs.se.selab.graph.Vertex
import de.unisaarland.cs.se.selab.phases.EmergencyPhase
import de.unisaarland.cs.se.selab.simulation.DataHolder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class EmergencyPhaseTest {
    var emergencies: MutableList<Emergency> = mutableListOf()
    var bases: MutableList<Base> = mutableListOf()
    var graph: Graph = Graph(mutableListOf<Vertex>(), mutableListOf<Road>())
    val dataHolder: DataHolder = DataHolder(graph, bases, mutableListOf<Event>(), emergencies)
    val emergencyPhase: EmergencyPhase = EmergencyPhase(dataHolder)

    /**
     * Initialises a list of emergencies and bases to use for the testing
     */
    @BeforeEach
    fun setUp() {
        // Reset
        emergencies = mutableListOf()
        bases = mutableListOf()
        // Graph Init
        // Vertices Init
        val vertexA: Vertex = Vertex(0, mutableMapOf<Vertex, Road>())
        val vertexB: Vertex = Vertex(1, mutableMapOf<Vertex, Road>())
        val vertexC: Vertex = Vertex(2, mutableMapOf<Vertex, Road>())
        val vertexD: Vertex = Vertex(3, mutableMapOf<Vertex, Road>())
        val vertexE: Vertex = Vertex(4, mutableMapOf<Vertex, Road>())

        // Road Init
        val roadAB: Road = Road(PrimaryType.SIDE_STREET, SecondaryType.TUNNEL, "VillageA", "RoadAB", 5, 2)
        val roadBC: Road = Road(PrimaryType.MAIN_STREET, SecondaryType.NONE, "VillageA", "RoadBC", 10, 4)
        val roadBD: Road = Road(PrimaryType.SIDE_STREET, SecondaryType.ONE_WAY_STREET, "VillageA", "RoadBD", 15, 5)
        val roadDE: Road = Road(PrimaryType.SIDE_STREET, SecondaryType.NONE, "VillageA", "RoadDE", 12, 4)
        val roadEB: Road = Road(PrimaryType.SIDE_STREET, SecondaryType.NONE, "VillageA", "RoadEB", 3, 3)

        // Connecting Vertices
        vertexA.connectingRoads[vertexB] = roadAB
        vertexB.connectingRoads[vertexA] = roadAB

        vertexB.connectingRoads[vertexC] = roadBC
        vertexC.connectingRoads[vertexB] = roadBC

        vertexB.connectingRoads[vertexD] = roadBD

        vertexD.connectingRoads[vertexE] = roadDE
        vertexE.connectingRoads[vertexD] = roadDE

        vertexB.connectingRoads[vertexE] = roadEB
        vertexE.connectingRoads[vertexB] = roadEB

        // Set Graph
        val vertices = mutableListOf(vertexA, vertexB, vertexC, vertexD, vertexE)
        val roads = mutableListOf(roadAB, roadBC, roadBD, roadDE, roadEB)
        this.graph = Graph(vertices, roads)

        // Base Init
        val baseA = PoliceStation(0, 0, 0, 0, mutableListOf<Vehicle>())
        val baseB = PoliceStation(0, 1, 0, 1, mutableListOf<Vehicle>())
        val baseC = Hospital(0, 2, 0, 2, mutableListOf<Vehicle>())
        val baseD = FireStation(3, 0, 3, mutableListOf<Vehicle>())
        val baseE = FireStation(4, 0, 4, mutableListOf<Vehicle>())

        // Set Bases
        this.bases = mutableListOf(baseA, baseB, baseC, baseD, baseE)
    }

    @Test
    public fun test1() {
        emergencyPhase.execute()

        assert(dataHolder.ongoingEmergencies.isEmpty())
        assert(dataHolder.resolvedEmergencies.isEmpty())
        assert(dataHolder.emergencies.isEmpty())
    }

    @Test fun test2() {
        // Emergency Init
        val emergencyA = Emergency(0, EmergencyType.CRIME, 1, 0, 5, 5, "VillageA", "RoadAB")

        // Set Emergencies
        this.emergencies = mutableListOf(emergencyA)

        emergencyPhase.execute()

        assert(dataHolder.emergencies.isEmpty())
        assert(dataHolder.resolvedEmergencies.isEmpty())
        assert(dataHolder.ongoingEmergencies.size == 1)
        assert(dataHolder.ongoingEmergencies.contains(this.emergencies[0]))
        assert(dataHolder.ongoingEmergencies[0].emergencyStatus == EmergencyStatus.ASSIGNED)
        assert(dataHolder.emergencyToBase[this.emergencies[0].id]!!.baseID == 0)
        assert(dataHolder.emergencyToBase.size == 1)
    }

    @Test
    fun test3() {
        // Emergency Init
        val emergencyA = Emergency(0, EmergencyType.CRIME, 1, 5, 5, 5, "VillageA", "RoadAB")

        // Set Emergencies
        this.emergencies = mutableListOf(emergencyA)

        assert(dataHolder.emergencies.contains(this.emergencies[0]))
        assert(dataHolder.emergencies.size == 1)
        assert(dataHolder.resolvedEmergencies.isEmpty())
        assert(dataHolder.ongoingEmergencies.isEmpty())
        assert(dataHolder.emergencies[0].emergencyStatus == EmergencyStatus.UNASSIGNED)
        assert(dataHolder.emergencyToBase.isEmpty())
    }
}
