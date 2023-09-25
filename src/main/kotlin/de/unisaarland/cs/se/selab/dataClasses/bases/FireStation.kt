import de.unisaarland.cs.se.selab.dataClasses.Base
data class FireStation(
    private val baseID: Int,
    private var staff: Int,
    private val vertexID: String,
    private val vehicles: List<Vehicle>
): Base(baseID, staff, vertexID, vehicles)