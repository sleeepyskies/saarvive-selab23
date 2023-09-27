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
     * Goes to the next token
     */
    fun next() {
        if (hasNext()) {
            this.currentIndex++
        } else {
            throw IndexOutOfBoundsException("Next element not exists")
        }
    }

    /**
     * Returns next token
     */
    fun checkNext(): String {
        if (hasNext()) {
            return tokens[this.currentIndex + 1]
        } else {
            throw IndexOutOfBoundsException("Next element not exists")
        }
    }

    /**
     * Returns current token
     */
    fun popCurrent(): String {
        return tokens[this.currentIndex]
    }

    /**
     * Returns previous token
     */
    fun previous(): String {
        if (this.currentIndex != 0) {
            return tokens[this.currentIndex - 1]
        } else {
            throw IndexOutOfBoundsException("Previous element not exists")
        }
    }

    /**
     * Checks if current token is of [kind] and equals [str]
     */
    fun peek(kind: TokenKind, str: String): Boolean {
        return peekKind() == kind && tokens[this.currentIndex] == str
    }

    /**
     * Checks if current token is of [kind]
     */
    fun peek(kind: TokenKind): Boolean {
        return peekKind() == kind
    }

    /**
     * Check if current token equals [str]
     */
    fun peek(str: String): Boolean {
        return tokens[this.currentIndex] == str
    }

    /**
     * Returns the kind of the symbol
     */
    fun peekKind(): TokenKind {
        val token = tokens[currentIndex]
        return when {
            token == "village" || token == "name" || token == "heightLimit" || token == "weight" ||
                token == "primaryType" || token == "secondaryType"
            -> TokenKind.KEYWORD

            Pattern.matches("\\w+", token) -> TokenKind.IDENTIFIER
            else -> TokenKind.SYMBOL
        }
    }
}
