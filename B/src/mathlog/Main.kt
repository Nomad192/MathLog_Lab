package mathlog

fun main() {
    val file = File( generateSequence { readlnOrNull() }.takeWhile { it.isNotEmpty() }
        .mapIndexed  { index, lineStr ->
            val (contextStr, expressionStr) = lineStr.split("|-", limit = 2)
            Record(lineStr, ContextList(contextStr), parseExpression(expressionStr), index + 1)
        }
        .toList())
    file.records.forEach { println("[${it.number}] ${it.original} [${(it.rule as Rule)}]") }
}