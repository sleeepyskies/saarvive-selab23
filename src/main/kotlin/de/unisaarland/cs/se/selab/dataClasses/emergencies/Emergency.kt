package de.unisaarland.cs.se.selab.dataClasses.emergencies

import de.unisaarland.cs.se.selab.dataClasses.CapacityType
import de.unisaarland.cs.se.selab.dataClasses.VehicleType
import de.unisaarland.cs.se.selab.global.Number
import de.unisaarland.cs.se.selab.graph.Vertex

data class Emergency(
    val id: Int,
    val emergencyType: EmergencyType,
    val severity: Int,
    val startTick: Int,
    var handleTime: Int, // change it for "stalking" required time on every tick
    val maxDuration: Int,
    val villageName: String,
    val roadName: String
) {
    internal lateinit var location: Pair<Vertex, Vertex> // TODO (initialise in DATAHOLDER)
    private val emergencyStatus: EmergencyStatus = EmergencyStatus.UNASSIGNED
    private val requiredVehicles: MutableMap<VehicleType, Int> =
        setRequiredVehicles() // add and remove dynamically
    private val requiredCapacity: MutableMap<CapacityType, Int> = setRequiredCapacity() // add and remove dynamically

    private fun setRequiredCapacity(): MutableMap<CapacityType, Int> {
        return when (this.emergencyType) {
            EmergencyType.FIRE -> setCapacityForFire()
            EmergencyType.CRIME -> setCapacityForCrime()
            EmergencyType.MEDICAL -> setCapacityForMedical()
            EmergencyType.ACCIDENT -> setCapacityForAccident()
        }
    }

    private fun setCapacityForAccident(): MutableMap<CapacityType, Int> {
        return when (this.severity) {
            1 -> mutableMapOf()

            2 -> mutableMapOf(CapacityType.PATIENT to 1)

            3 -> mutableMapOf(CapacityType.PATIENT to 2)

            else -> {
                error("Severity for ACCIDENT is not correct. EmergencyID: ${this.id} ")
            }
        }
    }

    private fun setCapacityForMedical(): MutableMap<CapacityType, Int> {
        return when (this.severity) {
            1 -> mutableMapOf()

            2 -> mutableMapOf(CapacityType.PATIENT to 2)

            3 -> mutableMapOf(CapacityType.PATIENT to Number.FIVE)

            else -> {
                error("Severity for MEDICAL is not correct. EmergencyID: ${this.id} ")
            }
        }
    }

    private fun setCapacityForCrime(): MutableMap<CapacityType, Int> {
        return when (this.severity) {
            1 -> mutableMapOf(CapacityType.CRIMINAL to 1)

            2 -> mutableMapOf(CapacityType.CRIMINAL to Number.FOUR)

            3 -> mutableMapOf(CapacityType.CRIMINAL to Number.EIGHT, CapacityType.PATIENT to 1)

            else -> error("Severity for CRIME is not correct. EmergencyID: ${this.id} ")
        }
    }

    private fun setCapacityForFire(): MutableMap<CapacityType, Int> {
        return when (this.severity) {
            1 -> mutableMapOf(CapacityType.WATER to Number.ONE_THOUSAND_TWO_HUNDRED)
            2 -> mutableMapOf(
                CapacityType.WATER to Number.THREE_THOUSAND,
                CapacityType.LADDER_LENGTH to Number.THIRTY,
                CapacityType.PATIENT to 1
            )

            3 ->
                mutableMapOf(
                    CapacityType.WATER to Number.SIX_THOUSAND,
                    CapacityType.LADDER_LENGTH to Number.FORTY,
                    CapacityType.PATIENT to 2
                )

            else -> error("Severity for FIRE is not correct. EmergencyID: ${this.id} ")
        }
    }

    private fun setRequiredVehicles(): MutableMap<VehicleType, Int> {
        return when (this.emergencyType) {
            EmergencyType.FIRE -> setVehiclesForFire()
            EmergencyType.CRIME -> setVehiclesForCrime()
            EmergencyType.MEDICAL -> setVehiclesForMedical()
            EmergencyType.ACCIDENT -> setVehiclesForAccident()
        }
    }

    private fun setVehiclesForAccident(): MutableMap<VehicleType, Int> {
        return when (this.severity) {
            1 -> mutableMapOf(VehicleType.FIRE_TRUCK_TECHNICAL to 1)

            2 -> mutableMapOf(
                VehicleType.FIRE_TRUCK_TECHNICAL to 2,
                VehicleType.POLICE_MOTORCYCLE to 1,
                VehicleType.POLICE_CAR to 1,
                VehicleType.AMBULANCE to 1
            )

            3 -> mutableMapOf(
                VehicleType.FIRE_TRUCK_TECHNICAL to Number.FOUR,
                VehicleType.POLICE_MOTORCYCLE to 2,
                VehicleType.POLICE_CAR to Number.FOUR,
                VehicleType.AMBULANCE to 3,
                VehicleType.EMERGENCY_DOCTOR_CAR to 1
            )

            else -> {
                error("Severity for ACCIDENT is not correct. EmergencyID: ${this.id} ")
            }
        }
    }

    private fun setVehiclesForMedical(): MutableMap<VehicleType, Int> {
        return when (this.severity) {
            1 -> mutableMapOf(VehicleType.AMBULANCE to 1)

            2 -> mutableMapOf(
                VehicleType.AMBULANCE to 2,
                VehicleType.EMERGENCY_DOCTOR_CAR to 1
            )

            3 -> mutableMapOf(
                VehicleType.AMBULANCE to Number.FIVE,
                VehicleType.EMERGENCY_DOCTOR_CAR to 2,
                VehicleType.FIRE_TRUCK_TECHNICAL to 2
            )

            else -> {
                error("Severity for MEDICAL is not correct. EmergencyID: ${this.id} ")
            }
        }
    }

    private fun setVehiclesForCrime(): MutableMap<VehicleType, Int> {
        return when (this.severity) {
            1 -> mutableMapOf(VehicleType.POLICE_CAR to 1)

            2 -> mutableMapOf(
                VehicleType.POLICE_CAR to Number.FOUR,
                VehicleType.K9_POLICE_CAR to 1,
                VehicleType.AMBULANCE to 1
            )

            3 -> mutableMapOf(
                VehicleType.POLICE_CAR to Number.SIX,
                VehicleType.POLICE_MOTORCYCLE to 2,
                VehicleType.K9_POLICE_CAR to 2,
                VehicleType.AMBULANCE to 2,
                VehicleType.FIREFIGHTER_TRANSPORTER to 1
            )

            else -> error("Severity for CRIME is not correct. EmergencyID: ${this.id} ")
        }
    }

    private fun setVehiclesForFire(): MutableMap<VehicleType, Int> {
        return when (this.severity) {
            1 -> mutableMapOf(VehicleType.FIRE_TRUCK_WATER to 2)

            2 -> mutableMapOf(
                VehicleType.FIRE_TRUCK_WATER to Number.FOUR,
                VehicleType.FIRE_TRUCK_LADDER to 1,
                VehicleType.FIREFIGHTER_TRANSPORTER to 1,
                VehicleType.AMBULANCE to 1
            )

            3 -> mutableMapOf(
                VehicleType.FIRE_TRUCK_WATER to Number.SIX,
                VehicleType.FIRE_TRUCK_LADDER to 2,
                VehicleType.FIREFIGHTER_TRANSPORTER to 2,
                VehicleType.AMBULANCE to 2,
                VehicleType.EMERGENCY_DOCTOR_CAR to 1
            )

            else -> error("Severity for FIRE is not correct. EmergencyID: ${this.id} ")
        }
    }

    fun getRequiredVehicles(): Map<VehicleType, Int> {
        return requiredVehicles
    }

    fun getEmergencyStatus(): EmergencyStatus {
        return emergencyStatus
    }
}
