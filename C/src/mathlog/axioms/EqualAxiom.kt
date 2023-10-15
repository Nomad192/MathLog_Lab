package mathlog.axioms

import mathlog.parse.Expression

val equalAxiom = EqualAxiom()
class EqualAxiom
{
    private lateinit var variableMap: MutableMap<String, Expression>

    /** ============================================================================================================ **/

    operator fun invoke(axiom: Expression, expression: Expression) = entryPoint(axiom, expression)

    /** ============================================================================================================ **/

    private fun entryPoint(axiom: Expression, expression: Expression): Boolean {
        variableMap = mutableMapOf()
        return work(axiom, expression)
    }

    private fun work(axiom: Expression, expression: Expression): Boolean
    {
        return when (axiom) {
            is Expression.Variable -> {
                val value = variableMap.getOrPut(axiom.name) { expression }
                value == expression
            }
            is Expression.Denial -> {
                if (expression !is Expression.Denial) return false
                return work(axiom.expression, expression.expression)
            }
            is Expression.Conjunction -> {
                if (expression !is Expression.Conjunction) return false
                return work(axiom.left, expression.left) && work(axiom.right, expression.right)
            }
            is Expression.Disjunction -> {
                if (expression !is Expression.Disjunction) return false
                return work(axiom.left, expression.left) && work(axiom.right, expression.right)
            }
            is Expression.Implication -> {
                if (expression !is Expression.Implication) return false
                return work(axiom.left, expression.left) && work(axiom.right, expression.right)
            }
        }
    }
}