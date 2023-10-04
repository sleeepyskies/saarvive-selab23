package requestphasetests

import de.unisaarland.cs.se.selab.dataClasses.Request
import de.unisaarland.cs.se.selab.dataClasses.bases.PoliceStation
import de.unisaarland.cs.se.selab.dataClasses.vehicles.PoliceCar
import de.unisaarland.cs.se.selab.dataClasses.vehicles.VehicleStatus
import de.unisaarland.cs.se.selab.dataClasses.vehicles.VehicleType
import de.unisaarland.cs.se.selab.phases.RequestPhase
import de.unisaarland.cs.se.selab.simulation.DataHolder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class RequestPhaseTests {
    @Mock
    private lateinit var dataHolder: DataHolder

    @Mock
    //private lateinit var log: Log

    private lateinit var requestPhase: RequestPhase

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        requestPhase = RequestPhase(dataHolder)

    }

    @Test
    fun `test request exists`() {
        `when`(dataHolder.requests).thenReturn(mutableListOf(
            Request(listOf(1, 2), 1, 1, mutableMapOf(), mutableMapOf())))
        assertTrue(requestPhase.requestExists())
    }

    @Test
    fun `test request does not exist`() {
        `when`(dataHolder.requests).thenReturn(mutableListOf())
        assertTrue(!requestPhase.requestExists())
    }

    @Test
    fun `test assignable assets`(){
        val vehicle1 = PoliceCar(VehicleType.POLICE_CAR, 1,1,2,1,2)
        val vehicle2 = PoliceCar(VehicleType.POLICE_CAR, 2,1,2,1,1)
        vehicle1.vehicleStatus = VehicleStatus.IN_BASE
        vehicle1.isAvailable = true
        vehicle2.vehicleStatus = VehicleStatus.MOVING_TO_EMERGENCY
        vehicle2.isAvailable = false
        val base = PoliceStation(1, 1, 3,1, mutableListOf())
        base.vehicles.add(vehicle1)
        base.vehicles.add(vehicle2)
        `when`(dataHolder.bases).thenReturn(listOf(base))

        // Test getAssignableAssets with valid requested vehicles
        val requestedVehicles = mapOf(VehicleType.POLICE_CAR to 1)
        val assignableAssets = requestPhase.getAssignableAssets(base, requestedVehicles)
        assertEquals(1, assignableAssets.size)
        assertEquals(VehicleType.POLICE_CAR, assignableAssets[0].vehicleType)

        // Test getAssignableAssets with empty requested vehicles
        val emptyRequestedVehicles = emptyMap<VehicleType, Int>()
        val emptyAssignableAssets = requestPhase.getAssignableAssets(base, emptyRequestedVehicles)
        assertEquals(0, emptyAssignableAssets.size)
    }
}


