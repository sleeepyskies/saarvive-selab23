package parsertests

import de.unisaarland.cs.se.selab.dataClasses.bases.FireStation
import de.unisaarland.cs.se.selab.dataClasses.bases.Hospital
import de.unisaarland.cs.se.selab.dataClasses.bases.PoliceStation
import de.unisaarland.cs.se.selab.dataClasses.vehicles.PoliceCar
import de.unisaarland.cs.se.selab.dataClasses.vehicles.VehicleType
import de.unisaarland.cs.se.selab.parser.AssetParser
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.FileNotFoundException

class AssetParserTest {

    @Test
    fun testValidAsset() {
        val parser = AssetParser(
            assetSchemaFile = "assets.schema",
            assetJsonFile = "src/systemtest/resources/assetsJsons/example_assets.json"
        )

        val (vehicles, bases) = parser.parse()

        // Validate bases
        assert(bases.size == 9) // Ensure there are 9 bases

        val fireStations = bases.filterIsInstance<FireStation>()
        val policeStations = bases.filterIsInstance<PoliceStation>()
        val hospitals = bases.filterIsInstance<Hospital>()

        assert(fireStations.size == 3) // Ensure there are 3 fire stations
        assert(policeStations.size == 3) // Ensure there are 3 police stations
        assert(hospitals.size == 3) // Ensure there are 3 hospitals

        // Validate vehicles
        assert(vehicles.size == 54) // Ensure there are 54 vehicles

        val policeCars = vehicles.filter { it.vehicleType == VehicleType.POLICE_CAR }
        val k9PoliceCars = vehicles.filter { it.vehicleType == VehicleType.K9_POLICE_CAR }
        val policeMotorcycles = vehicles.filter { it.vehicleType == VehicleType.POLICE_MOTORCYCLE }
        val ambulances = vehicles.filter { it.vehicleType == VehicleType.AMBULANCE }
        val emergencyDoctorCars = vehicles.filter { it.vehicleType == VehicleType.EMERGENCY_DOCTOR_CAR }

        assert(policeCars.size == 6) // Ensure there are 6 police cars
        assert(k9PoliceCars.size == 6) // Ensure there are 6 K9 police cars
        assert(policeMotorcycles.size == 6) // Ensure there are 6 police motorcycles
        assert(ambulances.size == 6) // Ensure there are 6 ambulances
        assert(emergencyDoctorCars.size == 6) // Ensure there are 6 emergency doctor cars

        // Validate specific attributes of the first base and vehicle
        val base1 = bases[0]
        assert(base1 is FireStation)
        assert(base1.baseID == 0)
        assert(base1.vertexID == 2)
        assert(base1.staff == 62)

        val vehicle1 = vehicles[0]
        assert(vehicle1.vehicleType == VehicleType.POLICE_CAR)
        assert(vehicle1.id == 0)
        assert(vehicle1.assignedBaseID == 1)
        assert(vehicle1.height == 2)
        assert(vehicle1.staffCapacity == 5)
        if (vehicle1 is PoliceCar) {
            assert(vehicle1.maxCriminalCapacity == 3)
        }
    }

    @Test
    fun testInvalidPathToJSON() {
        assertThrows<FileNotFoundException> {
            AssetParser(
                assetSchemaFile = "assets.schema",
                assetJsonFile = "invalid/path/to/json"
            )
        }
    }

    @Test
    fun testInvalidAssetsJSON() {
        assertThrows<IllegalArgumentException> {
            AssetParser(
                assetSchemaFile = "assets.schema",
                assetJsonFile = "src/test/resources/parsertests/assetParser/invalid_assets.json"
            ).parse()
        }
    }

    @Test
    fun testEmptyAssetListBASE() {
        assertThrows<IllegalArgumentException> {
            AssetParser(
                assetSchemaFile = "assets.schema",
                assetJsonFile = "src/test/resources/parsertests/assetParser/empty_assets.json"
            ).parse()
        }
    }

    @Test
    fun testEmptyAssetListVEHICLE() {
        assertThrows<IllegalArgumentException> {
            AssetParser(
                assetSchemaFile = "assets.schema",
                assetJsonFile = "src/test/resources/parsertests/assetParser/empty_assets.json"
            ).parse()
        }
    }

    @Test
    fun testSingleAssetVEHICLE() {
        assertThrows<IllegalArgumentException> {
            AssetParser(
                assetSchemaFile = "assets.schema",
                assetJsonFile = "src/test/resources/parsertests/assetParser/single_asset.json"
            ).parse()
        }
    }

    @Test
    fun testValidBasesNoVehicles() {
        assertThrows<IllegalArgumentException> {
            AssetParser(
                assetSchemaFile = "assets.schema",
                assetJsonFile = "src/test/resources/parsertests/assetParser/valid_bases_no_vehicles.json"
            ).parse()
        }
    }

    @Test
    fun testValidVehiclesNoBases() {
        assertThrows<IllegalArgumentException> {
            AssetParser(
                assetSchemaFile = "assets.schema",
                assetJsonFile = "src/test/resources/parsertests/assetParser/valid_vehicles_no_bases.json"
            ).parse()
        }
    }

    @Test
    fun testVehiclesAssignedToNonExistentBases() {
        assertThrows<IllegalArgumentException> {
            AssetParser(
                assetSchemaFile = "assets.schema",
                assetJsonFile = "src/test/resources/parsertests/assetParser/vehicles_non_existent_bases.json"
            ).parse()
        }
    }

    @Test
    fun testDuplicateVehicleIds() {
        assertThrows<IllegalArgumentException> {
            AssetParser(
                assetSchemaFile = "assets.schema",
                assetJsonFile = "src/test/resources/parsertests/assetParser/duplicate_vehicle_ids.json"
            ).parse()
        }
    }

    @Test
    fun testNegativeBaseId() {
        assertThrows<IllegalArgumentException> {
            AssetParser(
                assetSchemaFile = "assets.schema",
                assetJsonFile = "src/test/resources/parsertests/assetParser/negative_base_id.json"
            ).parse()
        }
    }

    @Test
    fun testInvalidBaseType() {
        assertThrows<IllegalArgumentException> {
            AssetParser(
                assetSchemaFile = "assets.schema",
                assetJsonFile = "src/test/resources/parsertests/assetParser/invalid_base_type.json"
            ).parse()
        }
    }

    @Test
    fun testNegativeVehicleHeight() {
        assertThrows<IllegalArgumentException> {
            AssetParser(
                assetSchemaFile = "assets.schema",
                assetJsonFile = "src/test/resources/parsertests/assetParser/negative_vehicle_height.json"
            ).parse()
        }
    }

    @Test
    fun testNegativeBaseStaff() {
        assertThrows<IllegalArgumentException> {
            AssetParser(
                assetSchemaFile = "assets.schema",
                assetJsonFile = "src/test/resources/parsertests/assetParser/negative_base_staff.json"
            ).parse()
        }
    }

    @Test
    fun testInvalidVehicleType() {
        assertThrows<IllegalArgumentException> {
            AssetParser(
                assetSchemaFile = "assets.schema",
                assetJsonFile = "src/test/resources/parsertests/assetParser/invalid_vehicle_type.json"
            ).parse()
        }
    }

    @Test
    fun testInvalidVehicleStaffCapacity() {
        assertThrows<IllegalArgumentException> {
            AssetParser(
                assetSchemaFile = "assets.schema",
                assetJsonFile = "src/test/resources/parsertests/assetParser/invalid_vehicle_staff_capacity.json"
            ).parse()
        }
    }

    @Test
    fun testInvalidVehicleCriminalCapacity() {
        assertThrows<IllegalArgumentException> {
            AssetParser(
                assetSchemaFile = "assets.schema",
                assetJsonFile = "src/test/resources/parsertests/assetParser/invalid_vehicle_criminal_capacity.json"
            ).parse()
        }
    }

    @Test
    fun testInvalidThereIsBaseWithNoVehicle() {
        assertThrows<IllegalArgumentException> {
            AssetParser(
                assetSchemaFile = "assets.schema",
                assetJsonFile = "src/test/resources/parsertests/assetParser/thisBaseHasNoCars.json"
            ).parse()
        }
    }

    @Test
    fun testInvalidPoliceCarHasWaterCap() {
        assertThrows<IllegalArgumentException> {
            AssetParser(
                assetSchemaFile = "assets.schema",
                assetJsonFile = "src/test/resources/parsertests/assetParser/invalid_PoliceCarHasWaterCap.json"
            ).parse()
        }
    }

    @Test
    fun testInvalidAmbulanceHasWaterCap() {
        assertThrows<IllegalArgumentException> {
            AssetParser(
                assetSchemaFile = "assets.schema",
                assetJsonFile = "src/test/resources/parsertests/assetParser/invalid_AmbulanceHasWaterCap.json"
            ).parse()
        }
    }

    @Test
    fun testInvalidHospitalHasADog() {
        assertThrows<IllegalArgumentException> {
            AssetParser(
                assetSchemaFile = "assets.schema",
                assetJsonFile = "src/test/resources/parsertests/assetParser/invalid_HospitalHasADog.json"
            ).parse()
        }
    }

    @Test
    fun testInvalidPoliceStationHasNoDog() {
        assertThrows<IllegalArgumentException> {
            AssetParser(
                assetSchemaFile = "assets.schema",
                assetJsonFile = "src/test/resources/parsertests/assetParser/invalid_PoliceStationHasNoDog.json"
            ).parse()
        }
    }

    @Test
    fun `valid scenario file`() {
        val parser = AssetParser(
            assetSchemaFile = "assets.schema",
            assetJsonFile = "src/systemtest/resources/assetsJsons/validScenario1_bases.json"
        )

        val (_, bases) = parser.parse()

        // Validate the first base
        val base1 = bases[0]
        assert(base1 is FireStation)
        assert(base1.baseID == 0)
        assert(base1.vertexID == 0)
        assert(base1.staff == 1)
    }

    @Test
    fun testPoliceStationWithK9CarButNoDogs() {
        assertThrows<IllegalArgumentException> {
            AssetParser(
                assetSchemaFile = "assets.schema",
                assetJsonFile =
                "src/test/resources/parsertests/assetParser/invalid_PoliceStationWithK9CarButNoDogs.json"
            ).parse()
        }
    }

    @Test
    fun testHospitalWithEmergencyDoctorCarButNoDoctors() {
        assertThrows<IllegalArgumentException> {
            AssetParser(
                assetSchemaFile = "assets.schema",
                assetJsonFile =
                "src/test/resources/parsertests/assetParser/invalid_HospitalWithEmergencyDoctorCarButNoDoctors.json"
            ).parse()
        }
    }
}
