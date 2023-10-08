package mathlog

@Deprecated("Was replaced by a more correct verification of the axiom. Check EqualAxiom.")
val renameToDefaultExpression = RenameToDefaultExpression()

@Deprecated("Was replaced by a more correct verification of the axiom. Check EqualAxiom.")
class RenameToDefaultExpression
{
    private lateinit var variableMap: MutableMap<String, Int>
    private var variableIdCounter = 0

    /** ============================================================================================================ **/

    operator fun invoke(expression: Expression) = entryPoint(expression)

    /** ============================================================================================================ **/

    private fun generateVariableId(): Int {
        return this.variableIdCounter++
    }

    private fun entryPoint(expression : Expression): Expression {
        variableMap = mutableMapOf()
        variableIdCounter = 0
        return work(expression)
    }

    private fun work(expression : Expression): Expression
    {
        return when (expression) {
            is Expression.Variable -> {
                val newName = variableMap.getOrPut(expression.name) { generateVariableId() }
                Expression.Variable(newName.toString())
            }
            is Expression.Conjunction -> Expression.Conjunction(work(expression.left), work(expression.right))
            is Expression.Disjunction -> Expression.Disjunction(work(expression.left), work(expression.right))
            is Expression.Implication -> Expression.Implication(work(expression.left), work(expression.right))
            is Expression.Denial -> Expression.Denial(work(expression.expression))
        }
    }
}