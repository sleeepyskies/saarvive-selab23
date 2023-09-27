package de.unisaarland.cs.se.selab.parser

import de.unisaarland.cs.se.selab.global.Log
import de.unisaarland.cs.se.selab.graph.Graph
import de.unisaarland.cs.se.selab.graph.Road
import de.unisaarland.cs.se.selab.graph.Vertex
import java.io.File
import java.lang.IllegalArgumentException
import kotlin.system.exitProcess

/**
 * Class for parsing the .dot file that contains information about Map. Receives [dotFilePath] as path to the .dot data
 * file
 */
class CountyParser(private val dotFilePath: String) {
    private val dotFile: File
    private val data: String
    private val roads: List<Road> = mutableListOf() // List of roads on the map (for compatability)

    init {
        try {
            // Convert path to a file
            this.dotFile = File(dotFilePath)
            this.data = this.dotFile.readText()
        } catch (_: NullPointerException) {
            // File doesn't exist
            exitProcess(1)
        }
    }

    fun parse(): Graph {
        // Creating it if the syntax is valid
        val blueprint = createBlueprint()

        return Graph()
    }

    /**
     * Calculates the number of occurrences of [char] in the given [str]
     */
    private fun countChar(str: String, char: Char): Int {
        return str.count { it == char }
    }


    /**
     * Checks if the [kind] and [token] of expected token are the same as current token in [tokenizer]
     */
    private fun expected(token: String, tokenizer: StringTokenizer, kind: TokenKind = TokenKind.SYMBOL): Boolean {
        return kind == tokenizer.peekKind() && token == tokenizer.popCurrent()
    }

    /**
     * Checks if the [kind] of expected token are the same as current token in [tokenizer]
     */
    private fun expected(tokenizer: StringTokenizer, kind: TokenKind = TokenKind.SYMBOL): Boolean {
        return kind == tokenizer.peekKind()
    }


    /**
     * Checking the syntax inside the braces, data provided in [str]
     */
    private fun parseVerticesAndEdges(str: String): Map<String, String> {
        val blueprint = mutableMapOf<String, String>()
        val tokens = StringTokenizer(str)
        while (tokens.hasNext()) {
            var currentStr = ""

            if (tokens.peek(TokenKind.IDENTIFIER)) {
                currentStr += tokens.popCurrent()
                tokens.next()
                if (expected("->", tokens) || expected(";", tokens)) {
                    when {
                        tokens.peek(";") -> {
                            if (!keyExists(blueprint, currentStr)) (
                                    blueprint.put(currentStr, "Vertex")) else {
                                throw IllegalArgumentException()
                            }
                        }

                        tokens.peek("->")
                        -> {
                            currentStr += tokens.popCurrent()
                            if (tokens.hasNext()) {
                                tokens.next()
                                if (tokens.peek(TokenKind.IDENTIFIER)) {
                                    currentStr += tokens.popCurrent()
                                    if (!keyExists(blueprint, currentStr)) {
                                        blueprint.put(currentStr, parseAttributes(tokens))
                                    } else {
                                        throw IllegalArgumentException()
                                    }

                                } else {
                                    throw IllegalArgumentException()
                                }
                            } else {
                                throw IllegalArgumentException()
                            }
                        }
                    }
                    if (tokens.hasNext()) {
                        tokens.next()
                    }
                } else {
                    throw IllegalArgumentException()
                }
            } else {
                throw IllegalArgumentException()
            }
        }
        return blueprint
    }

    /**
     * Parses attributes for road
     */
    private fun parseAttributes(tokens: StringTokenizer): String {
        var currentStr = ""
        tokens.next()
        if (expected("[", tokens)) {
            currentStr += tokens.popCurrent()
            tokens.next()
        } else throw IllegalArgumentException()
        if (expected("village", tokens, TokenKind.KEYWORD)){
            currentStr += tokens.popCurrent()
            tokens.next()
        } else throw IllegalArgumentException()
        if (expected("=", tokens)){
            currentStr += tokens.popCurrent()
            tokens.next()
        } else throw IllegalArgumentException()
        if (expected(tokens, TokenKind.IDENTIFIER)){
            currentStr += tokens.popCurrent()
            tokens.next()
        } else throw IllegalArgumentException()
        if (expected(";", tokens)){
            currentStr += tokens.popCurrent()
            tokens.next()
        } else throw IllegalArgumentException()
        if (expected("name", tokens, TokenKind.KEYWORD)){
            currentStr += tokens.popCurrent()
            tokens.next()
        } else throw IllegalArgumentException()
        if (expected("=", tokens)){
            currentStr += tokens.popCurrent()
            tokens.next()
        } else throw IllegalArgumentException()
        if (expected(tokens, TokenKind.IDENTIFIER)){
            currentStr += tokens.popCurrent()
            tokens.next()
        } else throw IllegalArgumentException()
        if (expected(";", tokens)){
            currentStr += tokens.popCurrent()
            tokens.next()
        } else throw IllegalArgumentException()
        if (expected("heightLimit", tokens, TokenKind.KEYWORD)){
            currentStr += tokens.popCurrent()
            tokens.next()
        } else throw IllegalArgumentException()
        if (expected("=", tokens)){
            currentStr += tokens.popCurrent()
            tokens.next()
        } else throw IllegalArgumentException()
        if (expected(tokens, TokenKind.IDENTIFIER)){
            currentStr += tokens.popCurrent()
            tokens.next()
        } else throw IllegalArgumentException()
        if (expected(";", tokens)){
            currentStr += tokens.popCurrent()
            tokens.next()
        } else throw IllegalArgumentException()
        if (expected("weight", tokens, TokenKind.KEYWORD)){
            currentStr += tokens.popCurrent()
            tokens.next()
        } else throw IllegalArgumentException()
        if (expected("=", tokens)){
            currentStr += tokens.popCurrent()
            tokens.next()
        } else throw IllegalArgumentException()
        if (expected(tokens, TokenKind.IDENTIFIER)){
            currentStr += tokens.popCurrent()
            tokens.next()
        } else throw IllegalArgumentException()
        if (expected(";", tokens)){
            currentStr += tokens.popCurrent()
            tokens.next()
        } else throw IllegalArgumentException()
        if (expected("primaryType", tokens, TokenKind.KEYWORD)){
            currentStr += tokens.popCurrent()
            tokens.next()
        } else throw IllegalArgumentException()
        if (expected("=", tokens)){
            currentStr += tokens.popCurrent()
            tokens.next()
        } else throw IllegalArgumentException()
        if ((tokens.popCurrent() == "countryRoad")||(tokens.popCurrent() == "sideStreet")||(tokens.popCurrent() == "mainStreet")){
            currentStr += tokens.popCurrent()
            tokens.next()
        } else throw IllegalArgumentException()
        if (expected(";", tokens)){
            currentStr += tokens.popCurrent()
            tokens.next()
        } else throw IllegalArgumentException()
        if (expected("secondaryType", tokens, TokenKind.KEYWORD)){
            currentStr += tokens.popCurrent()
            tokens.next()
        } else throw IllegalArgumentException()
        if (expected("=", tokens)){
            currentStr += tokens.popCurrent()
            tokens.next()
        } else throw IllegalArgumentException()
        if ((tokens.popCurrent() == "oneWayStreet")||(tokens.popCurrent() == "tunnel")||(tokens.popCurrent() == "none")){
            currentStr += tokens.popCurrent()
            tokens.next()
        } else throw IllegalArgumentException()
        if (expected(";", tokens)){
            currentStr += tokens.popCurrent()
            tokens.next()
        } else throw IllegalArgumentException()
        if (expected("]", tokens)) {
            currentStr += tokens.popCurrent()
            tokens.next()
        } else throw IllegalArgumentException()
        if (expected(";", tokens)) {
            currentStr += tokens.popCurrent()
        } else throw IllegalArgumentException()
        return  currentStr
    }

    /**
     * Checks if the Road connection ([currentStr]) is unique in [blueprint]
     */
    private fun keyExists(blueprint: MutableMap<String, String>, currentStr: String): Boolean {
        return blueprint.contains(currentStr)
    }

    /**
     * Creates blueprint of id or id->if for Vertices and Roads
     */
    private fun createBlueprint(): Map<String, String> {
        val blueprint = mutableMapOf<String, String>()
        try {
            if (countChar(this.data, '{') == 1 && countChar(this.data, '}') == 1) {
                val regexAll =
                    "\\A(?:digraph)(?:[\\s]+)(?:[a-zA-Z]+)(((?:[\\s]+)[{])|[{])(?:[(?:[\\s]+)|(?:[\\S]+)]+)[}]((\\Z)|((?:[\\s]+)\\Z))".toRegex()
                regexAll.matchEntire(this.data) ?: throw IllegalArgumentException()
                val start = this.data.indexOf("{")
                val end = this.data.indexOf("}")
                val subs = this.data.substring(start + 1, end)
                blueprint.putAll(parseVerticesAndEdges(subs))
            } else {
                throw IllegalArgumentException()
            }
        } catch (_: Exception) {
            Log.displayInitializationInfoInvalid(this.dotFile.name)
            exitProcess(1)
        }
        return blueprint
    }

    private fun validateBlueprint(blueprint: Map<String, String>): Boolean {
        return true
    }

    private fun createGraph(vertices: List<Vertex>): Graph {
        return Graph(vertices)
    }

    protected fun createRoad(road: String): Road {
        return Road()
    }

    private fun createRoadList(blueprint: Map<String, String>): List<Road> {
        val roads = mutableListOf<Road>()
        return roads
    }

    private fun createVertexList(blueprint: Map<String, String>): List<Vertex> {
        val vertices = mutableListOf<Vertex>()
        return vertices
    }

    protected fun createVertex(vertex: String): Vertex {
        return Vertex()
    }

    private fun connectVertices(vertices: List<Vertex>, roads: List<Road>, blueprint: Map<String, String>) {
        return Unit
    }
}
