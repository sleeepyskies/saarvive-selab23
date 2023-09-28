package parsertests

import de.unisaarland.cs.se.selab.dataClasses.bases.FireStation
import de.unisaarland.cs.se.selab.dataClasses.bases.Hospital
import de.unisaarland.cs.se.selab.dataClasses.bases.PoliceStation
import de.unisaarland.cs.se.selab.dataClasses.vehicles.VehicleType
import de.unisaarland.cs.se.selab.parser.AssetParser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class AssetParserTest {

    @Test
    fun testValidAssetParsing() {
        val parser = AssetParser(
            assetSchemaFile = "src/main/resources/schema/assets.schema",
            jsonFile = ""
        )

        val (allBases, allVehicles) = parser.parse()

        assertEquals(9, allBases.size)
        assertTrue(allBases.any { it is FireStation })
        assertTrue(allBases.any { it is PoliceStation })
        assertTrue(allBases.any { it is Hospital })

        val fireStation = allBases.find { it is FireStation && it.baseID == 0 } as FireStation
        assertEquals(2, fireStation.vertexID)
        assertEquals(62, fireStation.staff)

        val policeStation = allBases.find { it is PoliceStation && it.baseID == 1 } as PoliceStation
        assertEquals(4, policeStation.vertexID)
        assertEquals(12, policeStation.staff)
        assertEquals(3, policeStation.dogs)

        val hospital = allBases.find { it is Hospital && it.baseID == 2 } as Hospital
        assertEquals(5, hospital.vertexID)
        assertEquals(32, hospital.staff)
        assertEquals(3, hospital.doctors)

        assertEquals(54, allVehicles.size)
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

    @Test
    fun testInvalidPathToJSON() {
        assertThrows<IllegalArgumentException> {
            AssetParser(
                assetSchemaFile = "src/main/resources/schema/assets.schema",
                jsonFile = ""
            )
        }
    }

    @Test
    fun testInvalidAssetsJSON() {
        assertThrows<IllegalArgumentException> {
            AssetParser(
                assetSchemaFile = "src/main/resources/schema/assets.schema",
                jsonFile = ""
            ).parse()
        }
    }

    @Test
    fun testEmptyAssetList() {
        val parser = AssetParser(
            assetSchemaFile = "src/main/resources/schema/assets.schema",
            jsonFile = ""
        )

        val (allBases, allVehicles) = parser.parse()

        assertEquals(0, allBases.size)
        assertEquals(0, allVehicles.size)
    }

    @Test
    fun testSingleAsset() {
        val parser = AssetParser(
            assetSchemaFile = "src/main/resources/schema/assets.schema",
            jsonFile = ""
        )

        val (allBases, allVehicles) = parser.parse()

        assertEquals(1, allBases.size)
        assertEquals(1, allVehicles.size)

        val singleBase = allBases.first()
        assertTrue(singleBase is FireStation)
        assertEquals(0, singleBase.baseID)
        assertEquals(2, singleBase.vertexID)
        assertEquals(62, singleBase.staff)

        val singleVehicle = allVehicles.first()
        assertEquals(VehicleType.FIRE_TRUCK_WATER, singleVehicle.vehicleType)
        assertEquals(0, singleVehicle.id)
        assertEquals(0, singleVehicle.assignedBaseID)
        assertEquals(2, singleVehicle.height)
        assertEquals(5, singleVehicle.staffCapacity)
    }
}
