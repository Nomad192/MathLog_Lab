val Axioms : Map<Int, Expression> = AxiomsScheme.axioms

object AxiomsScheme {
    private val axiomSchemeStr = """
        [1]  a -> b -> a
        [2]  (a->b) -> (a->b->c) -> (a->c)
        [3]  a -> b -> a & b
        [4]  a & b -> a
        [5]  a & b -> b
        [6]  a -> a | b
        [7]  b -> a | b
        [8]  (a->c) -> (b->c) -> (a|b->c)
        [9]  (a->b) -> (a->!b) -> !a
        [10] !!a -> a
    """.trimIndent()

    val axioms: Map<Int, Expression> by lazy {
        axiomSchemeStr.split("\n").associate { line ->
            val parts = line.split("]", limit = 2)
            val number = parts.first().substring(1).toInt()
            val expressionStr = parseExpression(parts.last().uppercase())
            Pair(number, expressionStr)
        }
    }
}