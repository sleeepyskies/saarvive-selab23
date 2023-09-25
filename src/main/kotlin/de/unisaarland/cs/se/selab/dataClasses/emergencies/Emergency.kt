package de.unisaarland.cs.se.selab.dataClasses.emergencies

import de.unisaarland.cs.se.selab.dataClasses.CapacityType
import de.unisaarland.cs.se.selab.graph.Vertex
import de.unisaarland.cs.se.selab.dataClasses.VehicleType


class Emergency(
    val id: Int,
    val emergencyType: EmergencyType,
    val severity: Int,
    val startTick: Int,
    var handleTime: Int, // change it for "stalking" required time on every tick
    val maxDuration: Int,
    val villageName: String,
    val roadName: String
) {
    private val location: Pair<Vertex, Vertex> = TODO()
    private var emergencyStatus: EmergencyStatus = EmergencyStatus.UNASSIGNED
    private val requiredVehicles: MutableMap<VehicleType, Int> =
        this.calculateRequiredVehicles() // add and remove dynamically
    private val requiredCapacity: MutableMap<CapacityType, Int> =
        this.calculateRequiredCapacity() // add and remove dynamically

    private fun calculateRequiredVehicles(): MutableMap<VehicleType, Int> {
        when (this.emergencyType) {
            EmergencyType.FIRE -> when (this.severity) {
                1 -> this.requiredVehicles[VehicleType.FIRE_TRUCK_WATER] = 2
                2 -> this.requiredVehicles.putAll(
                    setOf(
                        VehicleType.FIRE_TRUCK_WATER to 4,
                        VehicleType.FIRE_TRUCK_LADDER to 1,
                        VehicleType.FIREFIGHTER_TRANSPORTER to 1,
                        VehicleType.AMBULANCE to 1
                    )
                )

                3 -> this.requiredVehicles.putAll(
                    setOf(
                        VehicleType.FIRE_TRUCK_WATER to 6,
                        VehicleType.FIRE_TRUCK_LADDER to 2,
                        VehicleType.FIREFIGHTER_TRANSPORTER to 2,
                        VehicleType.AMBULANCE to 2,
                        VehicleType.EMERGENCY_DOCTOR_CAR to 1
                    )
                )
                // do we actually need else statement for EMERGENCY SEVERITY
            }

            EmergencyType.CRIME -> when (this.severity) {
                1 -> this.requiredVehicles[VehicleType.POLICE_CAR] = 1
                2 -> this.requiredVehicles.putAll(
                    setOf(
                        VehicleType.POLICE_CAR to 4,
                        VehicleType.K9_POLICE_CAR to 1,
                        VehicleType.AMBULANCE to 1
                    )
                )

                3 -> this.requiredVehicles.putAll(
                    setOf(
                        VehicleType.POLICE_CAR to 6,
                        VehicleType.POLICE_MOTORCYCLE to 2,
                        VehicleType.K9_POLICE_CAR to 2,
                        VehicleType.AMBULANCE to 2,
                        VehicleType.FIREFIGHTER_TRANSPORTER to 1
                    )
                )
            }

            EmergencyType.MEDICAL -> when (this.severity) {
                1 -> this.requiredVehicles[VehicleType.AMBULANCE] = 1
                2 -> this.requiredVehicles.putAll(
                    setOf(
                        VehicleType.AMBULANCE to 2,
                        VehicleType.EMERGENCY_DOCTOR_CAR to 1
                    )
                )

                3 -> this.requiredVehicles.putAll(
                    setOf(
                        VehicleType.AMBULANCE to 5,
                        VehicleType.EMERGENCY_DOCTOR_CAR to 2,
                        VehicleType.FIRE_TRUCK_TECHNICAL to 2
                    )
                )
            }

            EmergencyType.ACCIDENT -> when (this.severity) {
                1 -> this.requiredVehicles[VehicleType.FIRE_TRUCK_TECHNICAL] = 1
                2 -> this.requiredVehicles.putAll(
                    setOf(
                        VehicleType.FIRE_TRUCK_TECHNICAL to 2,
                        VehicleType.POLICE_MOTORCYCLE to 1,
                        VehicleType.POLICE_CAR to 1,
                        VehicleType.AMBULANCE to 1
                    )
                )

                3 -> this.requiredVehicles.putAll(
                    setOf(
                        VehicleType.FIRE_TRUCK_TECHNICAL to 4,
                        VehicleType.POLICE_MOTORCYCLE to 2,
                        VehicleType.POLICE_CAR to 4,
                        VehicleType.AMBULANCE to 3,
                        VehicleType.EMERGENCY_DOCTOR_CAR to 1
                    )
                )
                // do we actually need else statement for EMERGENCY SEVERITY
            }
            // do we actually need else statement for EMERGENCY TYPE
        }
        return TODO("Provide the return value")
    }

    private fun calculateRequiredCapacity(): MutableMap<CapacityType, Int> {
        return TODO("Provide the return value")
    }
}