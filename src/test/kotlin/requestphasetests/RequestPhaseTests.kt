package requestphasetests

import de.unisaarland.cs.se.selab.dataClasses.Request
import de.unisaarland.cs.se.selab.dataClasses.bases.PoliceStation
import de.unisaarland.cs.se.selab.dataClasses.vehicles.PoliceCar
import de.unisaarland.cs.se.selab.dataClasses.vehicles.Vehicle
import de.unisaarland.cs.se.selab.dataClasses.vehicles.VehicleStatus
import de.unisaarland.cs.se.selab.dataClasses.vehicles.VehicleType
import de.unisaarland.cs.se.selab.phases.RequestPhase
import de.unisaarland.cs.se.selab.simulation.DataHolder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class RequestPhaseTests {
    @Mock
    private lateinit var dataHolder: DataHolder

    private lateinit var requestPhase: RequestPhase

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        requestPhase = RequestPhase(dataHolder)
    }

    @Test
    fun `test request exists`() {
        `when`(dataHolder.requests).thenReturn(
            mutableListOf(
                Request(listOf(1, 2), 1, 1, mutableMapOf(), mutableMapOf())
            )
        )
        assertTrue(requestPhase.requestExists())
    }

    @Test
    fun `test request does not exist`() {
        `when`(dataHolder.requests).thenReturn(mutableListOf())
        assertTrue(!requestPhase.requestExists())
    }

    @Test
    fun `test assignable assets`() {
        val vehicle1 = PoliceCar(VehicleType.POLICE_CAR, 1, 1, 2, 1, 2)
        val vehicle2 = PoliceCar(VehicleType.POLICE_CAR, 2, 1, 2, 1, 1)
        vehicle1.vehicleStatus = VehicleStatus.IN_BASE
        vehicle1.isAvailable = true
        vehicle2.vehicleStatus = VehicleStatus.MOVING_TO_EMERGENCY
        vehicle2.isAvailable = false
        val base = PoliceStation(1, 1, 3, 1, mutableListOf())
        base.vehicles.add(vehicle1)
        base.vehicles.add(vehicle2)
        `when`(dataHolder.bases).thenReturn(listOf(base))

        // Test getAssignableAssets with valid requested vehicles
        val requestedVehicles = mapOf(VehicleType.POLICE_CAR to 1)
        val assignableAssets = requestPhase.getAssignableAssets(base, requestedVehicles)
        assertEquals(1, assignableAssets.size)
        assertEquals(VehicleType.POLICE_CAR, assignableAssets[0].vehicleType)

        // Test getAssignableAssets with invalid requested vehicles
        val invalidRequestedVehicles = mapOf(VehicleType.POLICE_CAR to 3)
        val invalidAssignableAssets = requestPhase.getAssignableAssets(base, invalidRequestedVehicles)
        assertEquals(1, invalidAssignableAssets.size)

        // Test getAssignableAssets with empty requested vehicles
        val emptyRequestedVehicles = emptyMap<VehicleType, Int>()
        val emptyAssignableAssets = requestPhase.getAssignableAssets(base, emptyRequestedVehicles)
        assertEquals(0, emptyAssignableAssets.size)
    }

    @Test
    fun `test get normal vehicles`() {
        val vehicle1 = PoliceCar(VehicleType.POLICE_CAR, 21, 0, 3, 1, 1)
        val vehicle2 = Vehicle(VehicleType.FIRE_TRUCK_TECHNICAL, 22, 0, 5, 2)
        val vehicle3 = Vehicle(VehicleType.AMBULANCE, 23, 1, 2, 3)
        val vehicles = listOf(vehicle1, vehicle2, vehicle3)

        val normalVehicles = requestPhase.getNormalVehicles(vehicles)
        assertEquals(1, normalVehicles.size)
        assertTrue(normalVehicles.all { it.vehicleType != VehicleType.AMBULANCE })
        assertTrue(normalVehicles.all { it.vehicleType != VehicleType.POLICE_CAR })
    }

    @Test
    fun `test get special vehicles`() {
        val vehicle1 = PoliceCar(VehicleType.POLICE_CAR, 21, 1, 3, 1, 1)
        val vehicle2 = Vehicle(VehicleType.FIRE_TRUCK_TECHNICAL, 22, 0, 5, 2)
        val vehicle3 = Vehicle(VehicleType.AMBULANCE, 23, 1, 2, 3)
        val vehicles = listOf(vehicle1, vehicle2, vehicle3)

        val specialVehicles = requestPhase.getSpecialVehicles(vehicles)
        assertEquals(2, specialVehicles.size)
        assertTrue(specialVehicles.all { it.vehicleType != VehicleType.FIRE_TRUCK_TECHNICAL })
    }

    @Test
    fun `test can assign vehicle for K_9 car`() {
        val k9PoliceCar = Vehicle(VehicleType.K9_POLICE_CAR, 1, 2, 2, 1)
        k9PoliceCar.isAvailable = true
        k9PoliceCar.vehicleStatus = VehicleStatus.IN_BASE

        val dataHolder = mock(DataHolder::class.java)
        val requestPhase = RequestPhase(dataHolder)

        val result = requestPhase.canAssignVehicle(k9PoliceCar)

        assertTrue(result)
    }
}
