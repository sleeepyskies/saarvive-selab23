import de.unisaarland.cs.se.selab.dataClasses.bases.Base
import de.unisaarland.cs.se.selab.dataClasses.vehicles.Vehicle

data class PoliceStation( // b
    private var dogs: Int,
    override val baseID: Int,
    override var staff: Int,
    override val vertexID: Int,
    override val vehicles: List<Vehicle>
) : Base(baseID, staff, vertexID, vehicles)
