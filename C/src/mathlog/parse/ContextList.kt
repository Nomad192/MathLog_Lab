package mathlog.parse

class ContextList(contextStr: String) : ArrayList<Expression>() {
    init {
        if (contextStr.isNotBlank()) {
            val expressions = contextStr.split(",").map { parseExpression(it.trim()) }
            addAll(expressions)
        }
    }
}