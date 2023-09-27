package de.unisaarland.cs.se.selab.parser

import java.util.regex.Pattern

/**
 * Class for creating tokens out os provided [str]
 */
class StringTokenizer(str: String) {
    private val tokens: MutableList<String> = mutableListOf()
    private var currentIndex = 0

    init {
        // Define regex for "keywords" and "symbols"
        val matcher = Pattern.compile("\\w+|\\S").matcher(str)
        while (matcher.find()) {
            this.tokens.add(matcher.group())
        }
    }

    /**
     * Check if next token exists
     */
    fun hasNext(): Boolean {
        return currentIndex < this.tokens.size
    }

    /**
     * Returns next token
     */
    fun next(): String {
        if (hasNext()) {
            val token = tokens[this.currentIndex]
            this.currentIndex++
            return token
        } else {
            throw IndexOutOfBoundsException("Next element not exists")
        }
    }

    /**
     * Checks if current token is of [kind] and equals [str]
     */
    fun peek(kind: TokenKind, str: String): Boolean {
        return
    }
}
