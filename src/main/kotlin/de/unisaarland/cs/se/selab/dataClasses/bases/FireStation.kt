import de.unisaarland.cs.se.selab.dataClasses.bases.Base
import de.unisaarland.cs.se.selab.dataClasses.vehicles.Vehicle

data class FireStation( // b
    override val baseID: Int,
    private var staff: Int,
    private val vertexID: Int,
    private val vehicles: List<Vehicle>
) : Base(baseID, staff, vertexID, vehicles)
