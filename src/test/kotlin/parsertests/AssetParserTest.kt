package parsertests

import de.unisaarland.cs.se.selab.dataClasses.bases.FireStation
import de.unisaarland.cs.se.selab.dataClasses.vehicles.VehicleType
import de.unisaarland.cs.se.selab.parser.AssetParser
import org.json.JSONException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.FileNotFoundException

class AssetParserTest {

    @Test
    fun testValidAssetBASE() {
        val parser = AssetParser(
            assetSchemaFile = "base.schema",
            assetJsonFile = "src/test/resources/parsertests/assetParser/validAssets.json"
        )

        val (_, bases) = parser.parse()

        // Validate the first base
        val base1 = bases[0]
        assert(base1 is FireStation)
        assert(base1.baseID == 1)
        assert(base1.vertexID == 100)
        assert(base1.staff == 20)
    }

    @Test
    fun testValidAssetVEHICLE() {
        val parser = AssetParser(
            assetSchemaFile = "vehicle.schema",
            assetJsonFile = "src/test/resources/parsertests/assetParser/validAssets.json"
        )

        val (vehicles, _) = parser.parse()

        // Validate the first vehicle
        val vehicle1 = vehicles[0]
        assert(vehicle1.vehicleType == VehicleType.FIRE_TRUCK_WATER)
        assert(vehicle1.id == 101)
        assert(vehicle1.assignedBaseID == 1)
    }

    @Test
    fun testInvalidPathToJSON() {
        assertThrows<FileNotFoundException> {
            AssetParser(
                assetSchemaFile = "base.schema",
                assetJsonFile = "invalid/path/to/json"
            )
        }
    }

    @Test
    fun testInvalidAssetsJSONBASE() {
        assertThrows<JSONException> {
            AssetParser(
                assetSchemaFile = "base.schema",
                assetJsonFile = "src/test/resources/parsertests/assetParser/invalid_assets.json"
            ).parseBases()
        }
    }

    @Test
    fun testInvalidAssetsJSONVEHICLE() {
        assertThrows<IllegalArgumentException> {
            AssetParser(
                assetSchemaFile = "vehicle.schema",
                assetJsonFile = "src/test/resources/parsertests/assetParser/invalid_assets.json"
            ).parse()
        }
    }

    @Test
    fun testEmptyAssetListBASE() {
        val parser = AssetParser(
            assetSchemaFile = "base.schema",
            assetJsonFile = "src/test/resources/parsertests/assetParser/empty_assets.json"
        )

        val (_, bases) = parser.parse()
        assertEquals(0, bases.size)
    }

    @Test
    fun testEmptyAssetListVEHICLE() {
        val parser = AssetParser(
            assetSchemaFile = "vehicle.schema",
            assetJsonFile = "src/test/resources/parsertests/assetParser/empty_assets.json"
        )

        val (vehicles, _) = parser.parse()
        assertEquals(0, vehicles.size)
    }

    @Test
    fun testSingleAssetBASE() {
        val parser = AssetParser(
            assetSchemaFile = "base.schema",
            assetJsonFile = "src/test/resources/parsertests/assetParser/single_asset.json"
        )

        // validate single base
        val (_, bases) = parser.parse()
        assertEquals(1, bases.size)

        val singleBase = bases[0]
        assertTrue(singleBase is FireStation)
        assertEquals(0, singleBase.baseID)
        assertEquals(2, singleBase.vertexID)
        assertEquals(62, singleBase.staff)
    }

    @Test
    fun testSingleAssetVEHICLE() {
        val parser = AssetParser(
            assetSchemaFile = "vehicle.schema",
            assetJsonFile = "src/test/resources/parsertests/assetParser/single_asset.json"
        )

        // validate single vehicle
        val (vehicles, _) = parser.parse()
        assertEquals(1, vehicles.size)

        val singleVehicle = vehicles.first()
        assertEquals(VehicleType.FIRE_TRUCK_WATER, singleVehicle.vehicleType)
        assertEquals(0, singleVehicle.id)
        assertEquals(0, singleVehicle.assignedBaseID)
        assertEquals(2, singleVehicle.height)
        assertEquals(5, singleVehicle.staffCapacity)
    }
}
