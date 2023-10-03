package mapupdatephasetests

import de.unisaarland.cs.se.selab.graph.Graph
import de.unisaarland.cs.se.selab.phases.MapUpdatePhase
import de.unisaarland.cs.se.selab.simulation.DataHolder
import org.junit.jupiter.api.Test

class MapUpdatePhaseTest {

    @Test
    fun noEventsTest() {
        val emptyGraph = Graph(emptyList(), emptyList())
        val dataHolder = DataHolder(emptyGraph, emptyList(), mutableListOf(), mutableListOf())
        val mup = MapUpdatePhase(dataHolder)
        mup.execute()
        assert(mup.currentTick == 1)
        assert(!mup.shouldReroute)
        assert(mup.events.isEmpty())
    }
}
