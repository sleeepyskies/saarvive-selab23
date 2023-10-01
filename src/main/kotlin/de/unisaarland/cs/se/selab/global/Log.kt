package de.unisaarland.cs.se.selab.global

import de.unisaarland.cs.se.selab.dataClasses.emergencies.Emergency
import de.unisaarland.cs.se.selab.dataClasses.emergencies.EmergencyStatus
import java.io.File
import java.io.PrintWriter

/**
 * Log class handles logging for the simulation.
 */
object Log {
    var filePath: String = "stdout"

    /**
     * Helper function for logging messages.
     */
    private fun logIt(message: String) {
        if (filePath == "stdout") {
            val writer = PrintWriter(System.out)
            writer.println(message)
            writer.flush()
        } else {
            val writer = PrintWriter(File(filePath))
            writer.println(message)
            writer.flush()
        }
    }

    /**
     * log successful initialization.
     */
    fun displayInitializationInfoValid(fileName: String) {
        logIt("Initialization Info: $fileName successfully parsed and validated")
    }

    /**
     * log unsuccessful initialization.
     */
    fun displayInitializationInfoInvalid(fileName: String) {
        logIt("Initialization Info: $fileName invalid")
    }

    /**
     * log start of simulation.
     */
    fun displaySimulationStart() {
        logIt("Simulation starts")
    }

    /**
     * log current simulation tick.
     */
    fun displaySimulationTick(tickNumber: Int) {
        logIt("Simulation Tick: $tickNumber")
    }

    /**
     * log emergency id assignment to a specific base.
     */
    fun displayEmergencyAssignment(emergencyId: Int, baseId: Int) {
        logIt("Emergency Assignment: $emergencyId assigned to $baseId")
    }

    /**
     * log asset id allocation to an emergency id along with ticks with arrive.
     */
    fun displayAssetAllocation(assetId: Int, emergencyId: Int, ticksToArrive: Int) {
        logIt("Asset Allocation: $assetId allocated to $emergencyId; $ticksToArrive ticks to arrive.")
    }

    /**
     * log asset request for an emergency.
     */
    fun displayAssetRequest(emergencyId: Int, baseId: Int, requestId: Int) {
        logIt("Asset Request: $requestId sent to $baseId for $emergencyId.")
    }

    /**
     * log asset arrival at a vertex id.
     */
    fun displayAssetArrival(assetId: Int, vertexId: Int) {
        logIt("Asset Arrival: $assetId arrived at $vertexId.")
    }

    /**
     * log asset reallocation to a different emergency id.
     */
    fun displayAssetReallocation(emergencyId: Int, assetId: Int) {
        logIt("Asset Reallocation: $assetId reallocated to $emergencyId.")
    }

    /**
     * log a failed request for an emergency.
     */
    fun displayRequestFailed(emergencyId: Int) {
        logIt("Request Failed: $emergencyId failed.")
    }

    /**
     * log start of emergency handling.
     */
    fun displayEmergencyHandlingStart(emergencyId: Int) {
        logIt("Emergency Handling Start: $emergencyId handling started.")
    }

    /**
     * log a resolved emergency.
     */
    fun displayEmergencyResolved(emergencyId: Int) {
        logIt("Emergency Resolved: $emergencyId resolved.")
    }

    /**
     * log a failed emergency.
     */
    fun displayEmergencyFailed(emergencyId: Int) {
        logIt("Emergency Failed: $emergencyId failed.")
    }

    /**
     * log start of an event.
     */
    fun displayEventStarted(eventId: Int) {
        logIt("Event Triggered: $eventId triggered.")
    }

    /**
     * log end of an event.
     */
    fun displayEventEnded(eventId: Int) {
        logIt("Event Ended: $eventId ended.")
    }

    /**
     * log final statistics at the end of simulation.
     */
    fun displayStatistics(emergencies: List<Emergency>, assetsRerouted: Int, currentTick: Int) {
        val numberReceivedEmergencies = emergencies.count { it.startTick <= currentTick }
        val numberOngoingEmergencies = emergencies.count { it.emergencyStatus == EmergencyStatus.ONGOING }
        val numberFailedEmergencies = emergencies.count { it.emergencyStatus == EmergencyStatus.FAILED }
        val numberResolvedEmergencies = emergencies.count { it.emergencyStatus == EmergencyStatus.RESOLVED }

        logIt("Simulation End")
        logIt("Simulation Statistics: $assetsRerouted assets rerouted.")
        logIt("Simulation Statistics: $numberReceivedEmergencies received emergencies.")
        logIt("Simulation Statistics: $numberOngoingEmergencies ongoing emergencies.")
        logIt("Simulation Statistics: $numberFailedEmergencies failed emergencies.")
        logIt("Simulation Statistics: $numberResolvedEmergencies resolved emergencies.")
    }

    /**
     * log the number of rerouted assets.
     */
    fun displayAssetsRerouted(assetsRerouted: Int) {
        logIt("Assets Rerouted: $assetsRerouted")
    }

    /**
     * log the end of simulation if parsed files invalid
     */
    fun displaySimulationEnd() {
        logIt("Simulation End")
    }
}
