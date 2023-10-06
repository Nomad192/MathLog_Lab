fun main() {
    val file = File( generateSequence { readlnOrNull() }.takeWhile { it.isNotEmpty() }
        .mapIndexed  { index, lineStr ->
            val (contextStr, expressionStr) = lineStr.split("|-", limit = 2)
            Record(lineStr, parseContext(contextStr), parseExpression(expressionStr), index + 1)
        }
        .toList())
    file.records.forEach { println("[${it.number}] ${it.original} [${it.rule}]") }
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

private fun convertLine(line: Line) : Line
{
    val context = line.context.clone() as Context
    var expression = line.expression
    while (expression is Expression.Implication)
    {
        context.add(expression.left)
        expression = expression.right
    }

    return Line(context, expression)
}

data class Line(val context: Context, val expression: Expression)

data class Record(val original: String
, val contextOriginalList: List<Expression>
, val line: Line
, val number: Int
, val convertedLine: Line = convertLine(line)
, var rule: String = "")
{
    constructor(original: String, contextOriginalList: List<Expression>, expression: Expression, number: Int) :
            this(original, contextOriginalList, Line(Context(contextOriginalList), expression), number)
}