package mathlog.parse

class ContextObject(expressions: List<Expression>) : HashMap<Expression, Int>() {
    init {
        expressions.forEach { expression ->
            put(expression, getOrDefault(expression, 0) + 1)
        }
    }

    fun add(expression: Expression) {
        put(expression, getOrDefault(expression, 0) + 1)
    }
}