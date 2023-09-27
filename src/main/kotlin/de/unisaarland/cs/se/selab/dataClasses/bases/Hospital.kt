import de.unisaarland.cs.se.selab.dataClasses.bases.Base
import de.unisaarland.cs.se.selab.dataClasses.vehicles.Vehicle

/**
 * represents a fire station base
 */
data class Hospital(
    private var doctors: Int,
    override val baseID: Int,
    override var staff: Int,
    override val vertexID: Int,
    override val vehicles: List<Vehicle>
) : Base(baseID, staff, vertexID, vehicles)
