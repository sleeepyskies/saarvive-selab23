package requestphasetests

import de.unisaarland.cs.se.selab.dataClasses.Request
import de.unisaarland.cs.se.selab.global.Log
import de.unisaarland.cs.se.selab.phases.RequestPhase
import de.unisaarland.cs.se.selab.simulation.DataHolder
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
}


