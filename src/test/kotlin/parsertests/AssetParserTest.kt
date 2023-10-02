package parsertests

import de.unisaarland.cs.se.selab.dataClasses.bases.FireStation
import de.unisaarland.cs.se.selab.dataClasses.vehicles.PoliceCar
import de.unisaarland.cs.se.selab.dataClasses.vehicles.VehicleType
import de.unisaarland.cs.se.selab.parser.AssetParser
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.FileNotFoundException

class AssetParserTest {

    @Test
    fun testValidAssetBASE() {
        val parser = AssetParser(
            assetSchemaFile = "base.schema",
            assetJsonFile = "src/systemtest/resources/assetsJsons/example_assets.json"
        )

        val (_, bases) = parser.parse()

        // Validate the first base
        val base1 = bases[0]
        assert(base1 is FireStation)
        assert(base1.baseID == 0)
        assert(base1.vertexID == 2)
        assert(base1.staff == 62)
    }

    @Test
    fun testValidAssetVEHICLE() {
        val parser = AssetParser(
            assetSchemaFile = "vehicle.schema",
            assetJsonFile = "src/systemtest/resources/assetsJsons/example_assets.json"
        )

        val (vehicles, _) = parser.parse()

        // Validate the first vehicle
        val vehicle1 = vehicles[0]
        assert(vehicle1.vehicleType == VehicleType.POLICE_CAR)
        assert(vehicle1.id == 0)
        assert(vehicle1.assignedBaseID == 1)
        assert(vehicle1.height == 2)
        assert(vehicle1.staffCapacity == 5)
        if (vehicle1 is PoliceCar) { // access attributes specific to PoliceCar
            assert(vehicle1.maxCriminalCapacity == 3)
        }
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
        assertThrows<IllegalArgumentException> {
            AssetParser(
                assetSchemaFile = "base.schema",
                assetJsonFile = "src/test/resources/parsertests/assetParser/invalid_assets.json"
            ).parse()
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
        assertThrows<IllegalArgumentException> {
            AssetParser(
                assetSchemaFile = "base.schema",
                assetJsonFile = "src/test/resources/parsertests/assetParser/empty_assets.json"
            ).parse()
        }
    }

    @Test
    fun testEmptyAssetListVEHICLE() {
        assertThrows<IllegalArgumentException> {
            AssetParser(
                assetSchemaFile = "vehicle.schema",
                assetJsonFile = "src/test/resources/parsertests/assetParser/empty_assets.json"
            ).parse()
        }
    }

    @Test
    fun testSingleAssetVEHICLE() {
        assertThrows<IllegalArgumentException> {
            AssetParser(
                assetSchemaFile = "vehicle.schema",
                assetJsonFile = "src/test/resources/parsertests/assetParser/single_asset.json"
            ).parse()
        }
    }
}
