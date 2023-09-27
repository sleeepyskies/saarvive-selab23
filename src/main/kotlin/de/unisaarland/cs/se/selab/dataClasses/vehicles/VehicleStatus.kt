package de.unisaarland.cs.se.selab.dataClasses.vehicles

/**
 * used to tell what the status of the vehicle is
 */
enum class VehicleStatus {
    RECHARGING,
    IN_BASE,
    ASSIGNED_TO_EMERGENCY,
    HANDLING,
    MOVING_TO_EMERGENCY,
    MOVING_TO_BASE,
    ARRIVED,
    WAITING_AT_EMERGENCY
}
