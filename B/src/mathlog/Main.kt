package mathlog

fun main() {
    val file = File( generateSequence { readlnOrNull() }.takeWhile { it.isNotEmpty() }
        .mapIndexed  { index, lineStr ->
            val (contextStr, expressionStr) = lineStr.split("|-", limit = 2)
            Record(lineStr, parseContext(contextStr), parseExpression(expressionStr), index + 1)
        }
        .toList())
    file.records.forEach { println("[${it.number}] ${it.original} [${(it.rule as Rule)}]") }
}

class Context(expressions: List<Expression>) : HashMap<Expression, Int>() {
    init {
        expressions.forEach { expression ->
            put(expression, getOrDefault(expression, 0) + 1)
        }
    }

    fun add(expression: Expression) {
        put(expression, getOrDefault(expression, 0) + 1)
    }
}

private fun parseContext(contextStr: String): List<Expression>
{
    return ArrayList(contextStr.takeIf { it.isNotBlank() }?.split(",").orEmpty().map {
        parseExpression(it.trim())
    })
}

data class Line(val context: Context, val expression: Expression)