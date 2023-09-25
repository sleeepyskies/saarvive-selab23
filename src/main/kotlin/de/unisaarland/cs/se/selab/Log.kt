package de.unisaarland.cs.se.selab

import de.unisaarland.cs.se.selab.dataClasses.Emergency

class Log (private val logFilePath: String?) {

    public fun displayInitializationInfoValid(fileName: String): Unit {
        TODO()
    }

    public fun displayInitializationInfoInvalid(fileName: String): Unit {
        TODO()
    }

    public fun displaySimulationStart(): Unit {
        TODO()
    }

    public fun displaySimulationTick(tickNumber: Int): Unit {
        TODO()
    }

    public fun displayEmergencyAssignment(emergencyId: Int, baseId: Int): Unit {
        TODO()
    }

    public fun displayAssetAllocation(assetId: Int, emergencyId: Int, ticksToArrive : Int) : Unit {
        TODO()
    }

    public fun displayAssetRequest(emergencyId: Int, baseId: Int, requestId: Int) : Unit {
        TODO()
    }

    public fun displayAssetArrival(emergencyId: Int, assetId: Int, vertexId: Int) : Unit {
        TODO()
    }

    public fun displayAssetReallocation(emergencyId: Int, assetId: Int) : Unit {
        TODO()
    }

    public fun displayRequestFailed(emergencyId: Int): Unit {
        TODO()
    }

    public fun displayEmergencyHandlingStart(emergencyId: Int): Unit {
        TODO()
    }

    public fun displayEmergencyResolved(emergencyId: Int): Unit {
        TODO()
    }

    public fun displayEmergencyFailed(emergencyId: Int): Unit {
        TODO()
    }

    public fun displayEventStarted(eventId: Int): Unit {
        TODO()
    }

    public fun displayEventEnded(eventId: Int): Unit {
        TODO()
    }

    fun displayStatistics(emergencies: List<Emergency>, assetsRerouted: Int): Unit {
        TODO()
    }

    fun displayAssetsRerouted (vehicleID: Int, emergencyID: Int): Unit {
        TODO()
    }

}
