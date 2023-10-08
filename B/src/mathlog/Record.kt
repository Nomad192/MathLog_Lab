package mathlog

data class Record(val original: String
                  , val contextOriginalList: List<Expression>
                  , val line: Line
                  , val number: Int
                  , val convertedLine: Line = convertLine(line)
                  , var rule: Rule = Rule.Empty)
{
    constructor(original: String, contextOriginalList: List<Expression>, expression: Expression, number: Int) :
            this(original, contextOriginalList, Line(Context(contextOriginalList), expression), number)
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