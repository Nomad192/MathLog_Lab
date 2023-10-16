package mathlog.parse

val parseExpression = ParseExpression()

class ParseExpression {
    private lateinit var tokens: List<String>
    private var currentTokenIndex: Int = 0

    companion object {
        private const val VARIABLE_FORMAT = "[A-Z][A-Z0-9']*"
        private val denialSymbol = Regex.escape(Expression.Denial.SYMBOL)
        private val conjunctionSymbol = Regex.escape(Expression.Conjunction.SYMBOL)
        private val disjunctionSymbol = Regex.escape(Expression.Disjunction.SYMBOL)
        private val implicationSymbol = Regex.escape(Expression.Implication.SYMBOL)

        private val regex =
            Regex("$VARIABLE_FORMAT|$conjunctionSymbol|$disjunctionSymbol|$implicationSymbol|$denialSymbol|\\(|\\)")
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

    private fun parseVariable(): Expression = Expression.Variable(tokens[currentTokenIndex++])

    /** Version with name checks **/
    private fun parseVariableWithChecks(): Expression {
        val token = tokens[currentTokenIndex++]
        Regex("^${VARIABLE_FORMAT}$").find(token) ?: throw IllegalArgumentException("Invalid variable: $token")
        return Expression.Variable(token)
    }
}