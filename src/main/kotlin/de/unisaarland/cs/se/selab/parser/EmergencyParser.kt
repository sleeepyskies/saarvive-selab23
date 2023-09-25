package de.unisaarland.cs.se.selab.parser
import org.json.*
import de.unisaarland.cs.se.selab.dataClasses.Emergency
import de.unisaarland.cs.se.selab.getSchema
import java.io.File
import javax.swing.text.html.parser.Parser

class EmergencyParser {

    var emergencies = mutableListOf<Emergency>()
    var schema = getSchema(Parser::class.java,"emergency.schema")
    var json = JSONObject(File(schema).readText())

    public fun parse(fileName: String): List<Emergency> {
//        for (i in 0 <= until < json.getJSONARRAY of )
//        return Emergency()
        return emptyList()
    }



}