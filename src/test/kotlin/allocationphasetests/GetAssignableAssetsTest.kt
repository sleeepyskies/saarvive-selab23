package allocationphasetests

import de.unisaarland.cs.se.selab.dataClasses.bases.FireStation
import de.unisaarland.cs.se.selab.dataClasses.bases.Hospital
import de.unisaarland.cs.se.selab.dataClasses.bases.PoliceStation
import de.unisaarland.cs.se.selab.dataClasses.vehicles.Ambulance
import de.unisaarland.cs.se.selab.dataClasses.vehicles.FireTruckWater
import de.unisaarland.cs.se.selab.dataClasses.vehicles.FireTruckWithLadder
import de.unisaarland.cs.se.selab.dataClasses.vehicles.PoliceCar
import de.unisaarland.cs.se.selab.dataClasses.vehicles.Vehicle
import de.unisaarland.cs.se.selab.dataClasses.vehicles.VehicleType

class GetAssignableAssetsTest {

    // define vehicles

    // Police Cars
    val policeCar1 = PoliceCar(
        vehicleType = VehicleType.POLICE_CAR,
        id = 1,
        staffCapacity = 4,
        height = 160,
        maxCriminalCapacity = 5,
        assignedBaseID = 2 // Assign to PoliceStation1
    )

    // policeStation1.

    val policeCar2 = PoliceCar(
        vehicleType = VehicleType.POLICE_CAR,
        id = 2,
        staffCapacity = 4,
        height = 160,
        maxCriminalCapacity = 5,
        assignedBaseID = 2 // Assign to PoliceStation1
    )
    // policeStation1.vehicles.add(policeCar2)

    val policeCar3 = PoliceCar(
        vehicleType = VehicleType.POLICE_CAR,
        id = 3,
        staffCapacity = 4,
        height = 160,
        maxCriminalCapacity = 5,
        assignedBaseID = 5 // Assign to PoliceStation2
    )
    // policeStation2.vehicles.add(policeCar3)

    // Fire Trucks with Ladder
    val fireTruckWithLadder1 = FireTruckWithLadder(
        vehicleType = VehicleType.FIRE_TRUCK_LADDER,
        id = 4,
        staffCapacity = 6,
        height = 200,
        ladderLength = 8,
        assignedBaseID = 0 // Assign to FireStation1
    )
    // fireStation1.vehicles.add(fireTruckWithLadder1)

    val fireTruckWithLadder2 = FireTruckWithLadder(
        vehicleType = VehicleType.FIRE_TRUCK_LADDER,
        id = 5,
        staffCapacity = 6,
        height = 200,
        ladderLength = 8,
        assignedBaseID = 0 // Assign to FireStation1
    )
    // fireStation1.vehicles.add(fireTruckWithLadder2)

    val fireTruckWithLadder3 = FireTruckWithLadder(
        vehicleType = VehicleType.FIRE_TRUCK_LADDER,
        id = 6,
        staffCapacity = 6,
        height = 200,
        ladderLength = 8,
        assignedBaseID = 3 // Assign to FireStation2
    )
    // fireStation2.vehicles.add(fireTruckWithLadder3)

    // Fire Trucks with Water Capacity
    val fireTruckWater1 = FireTruckWater(
        vehicleType = VehicleType.FIRE_TRUCK_WATER,
        id = 7,
        staffCapacity = 5,
        height = 180,
        maxWaterCapacity = 600,
        assignedBaseID = 0 // Assign to FireStation1
    )
    // fireStation1.vehicles.add(fireTruckWater1)

    val fireTruckWater2 = FireTruckWater(
        vehicleType = VehicleType.FIRE_TRUCK_WATER,
        id = 8,
        staffCapacity = 5,
        height = 180,
        maxWaterCapacity = 600,
        assignedBaseID = 3 // Assign to FireStation2
    )
    // fireStation2.vehicles.add(fireTruckWater2)

    val fireTruckWater3 = FireTruckWater(
        vehicleType = VehicleType.FIRE_TRUCK_WATER,
        id = 9,
        staffCapacity = 5,
        height = 180,
        maxWaterCapacity = 600,
        assignedBaseID = 3 // Assign to FireStation2
    )
    // fireStation2.vehicles.add(fireTruckWater3)

    // Ambulances
    val ambulance1 = Ambulance(
        vehicleType = VehicleType.AMBULANCE,
        id = 10,
        staffCapacity = 2,
        height = 170,
        assignedBaseID = 1 // Assign to Hospital1
    )
    // hospital1.vehicles.add(ambulance1)

    val ambulance2 = Ambulance(
        vehicleType = VehicleType.AMBULANCE,
        id = 11,
        staffCapacity = 2,
        height = 170,
        assignedBaseID = 4 // Assign to Hospital2
    )
    // hospital2.vehicles.add(ambulance2)

    val ambulance3 = Ambulance(
        vehicleType = VehicleType.AMBULANCE,
        id = 12,
        staffCapacity = 2,
        height = 170,
        assignedBaseID = 4 // Assign to Hospital2
    )
    // hospital2.vehicles.add(ambulance3)

    // General Vehicles
    val vehicle1 = Vehicle(
        vehicleType = VehicleType.FIREFIGHTER_TRANSPORTER,
        id = 13,
        staffCapacity = 3,
        height = 150,
        assignedBaseID = 0 // Assign to FireStation1
    )
    // fireStation1.vehicles.add(vehicle1)

    val vehicle2 = Vehicle(
        vehicleType = VehicleType.EMERGENCY_DOCTOR_CAR,
        id = 14,
        staffCapacity = 4,
        height = 160,
        assignedBaseID = 1 // Assign to Hospital1
    )
    // hospital1.vehicles.add(vehicle2)

    val vehicle3 = Vehicle(
        vehicleType = VehicleType.POLICE_MOTORCYCLE,
        id = 15,
        staffCapacity = 2,
        height = 170,
        assignedBaseID = 2 // Assign to PoliceStation1
    )
    // policeStation1.vehicles.add(vehicle3)

    val vehicle4 = Vehicle(
        vehicleType = VehicleType.FIRE_TRUCK_TECHNICAL,
        id = 16,
        staffCapacity = 5,
        height = 180,
        assignedBaseID = 3 // Assign to FireStation2
    )
    // fireStation2.vehicles.add(vehicle4)

    val vehicle5 = Vehicle(
        vehicleType = VehicleType.EMERGENCY_DOCTOR_CAR,
        id = 17,
        staffCapacity = 3,
        height = 190,
        assignedBaseID = 4 // Assign to Hospital2
    )
    // hospital2.vehicles.add(vehicle5)

    val vehicle6 = Vehicle(
        vehicleType = VehicleType.K9_POLICE_CAR,
        id = 18,
        staffCapacity = 4,
        height = 200,
        assignedBaseID = 5 // Assign to PoliceStation2
    )
    // policeStation2.vehicles.add(vehicle6)

    val f1Vehicles = mutableListOf(fireTruckWithLadder1, fireTruckWithLadder2, fireTruckWater1, vehicle1)
    val f2Vehicles = mutableListOf(fireTruckWithLadder3, fireTruckWater2, fireTruckWater3, vehicle4)
    val p1Vehicles = mutableListOf(policeCar1, policeCar2, vehicle3)
    val p2Vehicles = mutableListOf(policeCar3, vehicle6)
    val h1Vehicles = mutableListOf(ambulance1, vehicle2)
    val h2Vehicles = mutableListOf(ambulance2, ambulance3, vehicle5)

    // Define the bases
    val fireStation1 = FireStation(0, 0, 1, f1Vehicles)
    val hospital1 = Hospital(1, 2, 2, 3, h1Vehicles)
    var policeStation1 = PoliceStation(2, 8, 3, 5, p1Vehicles)
    val fireStation2 = FireStation(3, 3, 4, f2Vehicles)
    val hospital2 = Hospital(4, 5, 5, 6, h2Vehicles)
    val policeStation2 = PoliceStation(5, 7, 6, 8, p2Vehicles)
}
