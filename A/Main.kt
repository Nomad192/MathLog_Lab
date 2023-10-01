fun main() {
    val input = readlnOrNull() ?: throw IllegalArgumentException("Empty input")
    print(ParsingStatement(input).parseExpression())
}

class ParsingStatement(input: String) {
    private var input: String = input.replace("\\s".toRegex(), "")
    private var pos: Int = 0

    private fun String.format(arg1: String, arg2: String): String = "($this,$arg1,$arg2)"

    private fun check(prefix: String) = input.startsWith(prefix, pos).also { if (it) pos += prefix.length }

    fun parseExpression(): String = parseDisjunction().let {
        if (check("->")) "->".format(it, parseExpression())
        else it
    }

    private fun parseDisjunction(): String = parseConjunction().let {
        fun half(arg: String): String {
            return if (check("|")) half("|".format(arg, parseConjunction()))
            else arg
        }
        return half(it)
    }

    private fun parseConjunction(): String = parseDenial().let {
        fun half(arg: String): String {
            return if (check("&")) half("&".format(arg, parseDenial()))
            else arg
        }
        return half(it)
    }

    private fun parseDenial(): String = when {
        check("!") -> "(!${parseDenial()})"
        check("(") -> parseExpression().also { check(")") }
        else -> parseVariable()
    }

    private fun parseVariable(): String {
        val match =
            Regex("^[A-Z][A-Za-z0-9']*").find(input.substring(pos)) ?: throw NoSuchElementException("Parse Error")
        return match.value.also { pos += match.value.length }
    }
}