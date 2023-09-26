import de.unisaarland.cs.se.selab.dataClasses.bases.Base
import de.unisaarland.cs.se.selab.dataClasses.vehicles.Vehicle

data class Hospital(
    private var doctors: Int,
    private val baseID: Int,
    private var staff: Int,
    private val vertexID: Int,
    private val vehicles: List<Vehicle>
): Base(baseID, staff, vertexID, vehicles)
