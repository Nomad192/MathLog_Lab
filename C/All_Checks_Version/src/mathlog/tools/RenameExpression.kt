package mathlog.tools

import mathlog.parse.Expression

val renameExpression = RenameExpression()

class RenameExpression {
    private lateinit var renameFunctor: (String) -> Expression

    /** ============================================================================================================ **/

    operator fun invoke(expression: Expression, renameFunctor: (String) -> Expression = StandardRename()) =
        entryPoint(expression, renameFunctor)

    /** ============================================================================================================ **/

    private fun entryPoint(expression: Expression, renameFunctional: (String) -> Expression): Expression {
        this.renameFunctor = renameFunctional
        return work(expression)
    }

    private fun work(expression: Expression): Expression {
        return when (expression) {
            is Expression.Variable -> renameFunctor(expression.name)
            is Expression.Conjunction -> Expression.Conjunction(work(expression.left), work(expression.right))
            is Expression.Disjunction -> Expression.Disjunction(work(expression.left), work(expression.right))
            is Expression.Implication -> Expression.Implication(work(expression.left), work(expression.right))
            is Expression.Denial -> Expression.Denial(work(expression.expression))
        }
    }
}

class StandardRename : (String) -> Expression {
    private var variableMap: MutableMap<String, Int> = mutableMapOf()
    private var variableIdCounter = 0

    override operator fun invoke(oldName: String) = Expression.Variable(generateNewName(oldName).toString())

    private fun generateNewName(oldName: String): Int {
        return variableMap.getOrPut(oldName) { generateVariableId() }
    }

    private fun generateVariableId(): Int {
        return this.variableIdCounter++
    }
}