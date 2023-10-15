package mathlog.parse

data class File(val records: Map<Int, Record>)
{
    constructor(input: Sequence<String>) : this(input.mapIndexed  { index, lineStr ->
        val (contextStr, expressionStr) = lineStr.split("|-", limit = 2)
        val realIndex = index + 1
        Pair(realIndex, Record(lineStr, ContextList(contextStr), parseExpression(expressionStr), realIndex))
    }.toMap())

    init {
        records.forEach { it.value.rule = Rule.getRule(records.values.toList(), it.value) }
    }

    override fun toString(): String {
        return records.values.joinToString("\n")
    }
}