package parsertests

import de.unisaarland.cs.se.selab.parser.AssetParser
import de.unisaarland.cs.se.selab.parser.BaseParser
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.random.Random
import kotlin.test.assertEquals

class AssetParserUnitTest {
    private lateinit var assetParser: AssetParser
    private lateinit var baseParser: BaseParser
    // private lateinit var vehicleParser: VehicleParser <= add later when test

    private val schema = "assets.schema"

    @BeforeEach
    fun setUp() {
        val jsonFile = "src/systemtest/resources/assetsJsons/example_assets.json"
        assetParser = AssetParser(schema, jsonFile)
        baseParser = BaseParser(assetParser.json) // Initialize the BaseParser with the json from AssetParser
    }

    @Test
    fun `test id validation`() {
        assertThrows<IllegalArgumentException> { baseParser.validateBaseId(Random.nextInt(-1000, -1)) }
    }

    @Test
    fun `test base validation`() {
        val baseType = "FIRE_STATION"
        val baseType2 = "POLICE_STATION"
        val baseType3 = "HOSPITAL"
        val baseType4 = "INVALID_BASE_TYPE"
        val baseType5 = "BASE"
        val baseType6 = "MINECRAFT"
        val baseType7 = "GHH"
        val baseType8 = ""
        val baseType9 = "     "
        assertEquals(baseType, baseParser.validateBaseType(baseType))
        assertEquals(baseType2, baseParser.validateBaseType(baseType2))
        assertEquals(baseType3, baseParser.validateBaseType(baseType3))
        assertThrows<IllegalArgumentException> { baseParser.validateBaseType(baseType4) }
        assertThrows<IllegalArgumentException> { baseParser.validateBaseType(baseType5) }
        assertThrows<IllegalArgumentException> { baseParser.validateBaseType(baseType6) }
        assertThrows<IllegalArgumentException> { baseParser.validateBaseType(baseType7) }
        assertThrows<IllegalArgumentException> { baseParser.validateBaseType(baseType8) }
        assertThrows<IllegalArgumentException> { baseParser.validateBaseType(baseType9) }
    }
}
