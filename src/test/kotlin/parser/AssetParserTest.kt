package parser

import de.unisaarland.cs.se.selab.dataClasses.bases.FireStation
import de.unisaarland.cs.se.selab.dataClasses.bases.Hospital
import de.unisaarland.cs.se.selab.dataClasses.bases.PoliceStation
import de.unisaarland.cs.se.selab.dataClasses.vehicles.VehicleType
import de.unisaarland.cs.se.selab.parser.AssetParser
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class AssetParserTest {

    @Test
    fun testValidAssetParsing() {
        val parser = AssetParser(
            assetSchemaFile = "src/main/resources/schema/asset.schema",
            jsonFile = "src/test/resources/parser/assetParser/example_assets.json"
        )

        val (allBases, allVehicles) = parser.parse()

        assertEquals(9, allBases.size)
        assertTrue(allBases.any { it is FireStation })
        assertTrue(allBases.any { it is PoliceStation })
        assertTrue(allBases.any { it is Hospital })

        val fireStation = allBases.find { it is FireStation && it.baseID == 0 } as FireStation
        assertEquals(2, fireStation.vertexID)
        assertEquals(62, fireStation.staff)

        val policeStation = allBases.find { it is PoliceStation && it.baseID  == 1 } as PoliceStation
        assertEquals(4, policeStation.vertexID)
        assertEquals(12, policeStation.staff)
        assertEquals(3, policeStation.dogs)

        val hospital = allBases.find { it is Hospital && it.baseID == 2 } as Hospital
        assertEquals(5, hospital.vertexID)
        assertEquals(32, hospital.staff)
        assertEquals(3, hospital.doctors)

        // Test vehicles
        assertEquals(54, allVehicles.size)
        assertTrue(allVehicles.any { it.vehicleType == VehicleType.POLICE_CAR })
        assertTrue(allVehicles.any { it.vehicleType == VehicleType.AMBULANCE })
        assertTrue(allVehicles.any { it.vehicleType == VehicleType.FIRE_TRUCK_WATER })

        // Test specific vehicle attributes
        val policeCar = allVehicles.find { it.vehicleType == VehicleType.POLICE_CAR && it.id == 0 }
        assertEquals(1, policeCar?.assignedBaseID)
        assertEquals(2, policeCar?.height)
        assertEquals(5, policeCar?.staffCapacity)

        val ambulance = allVehicles.find { it.vehicleType == VehicleType.AMBULANCE && it.id == 42 }
        assertEquals(2, ambulance?.assignedBaseID)
        assertEquals(4, ambulance?.height)
        assertEquals(2, ambulance?.staffCapacity)

        val fireTruckWater = allVehicles.find { it.vehicleType == VehicleType.FIRE_TRUCK_WATER && it.id == 18 }
        assertEquals(0, fireTruckWater?.assignedBaseID)
        assertEquals(2, fireTruckWater?.height)
        assertEquals(5, fireTruckWater?.staffCapacity)
    }
