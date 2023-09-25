import de.unisaarland.cs.se.selab.dataClasses.bases.Base
import de.unisaarland.cs.se.selab.dataClasses.vehicles.Vehicle

data class PoliceStation( // b
    private var dogs: Int,
    private val baseID: Int,
    private var staff: Int,
    private val vertexID: String,
    private val vehicles: List<Vehicle>
) : Base(baseID, staff, vertexID, vehicles)
