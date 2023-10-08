package mathlog

val parseExpression = ParseExpression()

class ParseExpression
{
    private lateinit var tokens: List<String>
    private var currentTokenIndex: Int = 0

    companion object {
        private const val VARIABLE_FORMAT = "[A-Z][A-Z0-9']*"
    }

    /** ============================================================================================================ **/

    operator fun invoke(input: String) = entryPoint(input)

    /** ============================================================================================================ **/

    private fun entryPoint(input: String): Expression {
        tokens = tokenize(input)
        currentTokenIndex = 0
        return parseExpression()
    }

    private fun tokenize(input: String): List<String> {
        val regex = Regex("$VARIABLE_FORMAT|!|&|\\||->|\\(|\\)")
        val matches = regex.findAll(input)
        return matches.map { it.value }.toList()
    }

    private fun check(expected: String): Boolean {
        if (currentTokenIndex < tokens.size && tokens[currentTokenIndex] == expected) {
            currentTokenIndex++
            return true
        }
        return false
    }

    private fun parseExpression(): Expression {
        return parseImplication()
    }

    private fun parseImplication(): Expression = parseDisjunction().let {
        if (check("->")) return Expression.Implication(it, parseImplication())
        return it
    }

    private fun parseDisjunction(): Expression {
        var left = parseConjunction()
        while (check("|")) left = Expression.Disjunction(left, parseConjunction())
        return left
    }

    private fun parseConjunction(): Expression {
        var left = parseDenial()
        while (check("&")) left = Expression.Conjunction(left, parseDenial())
        return left
    }

    private fun parseDenial(): Expression {
        if (check("!")) return Expression.Denial(parseDenial())

        if (check("(")) return parseExpression().also { check(")") }
        // if (check("(")) return parseExpression().also { require(check(")")) { "Expected closing bracket" } }

        return parseVariable()
    }

    private fun parseVariable() : Expression = Expression.Variable(tokens[currentTokenIndex++])

    /**
    private fun parseVariable() : Expression {
    val token = tokens[currentTokenIndex++]
    Regex("^${variableFormat}$").find(token) ?: throw IllegalArgumentException("Invalid variable: $token")
    return Expression.Variable(token)
    }
     **/
}