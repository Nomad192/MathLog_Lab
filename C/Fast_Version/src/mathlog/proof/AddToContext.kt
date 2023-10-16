package mathlog.proof

import mathlog.parse.Expression
import mathlog.parse.parseExpression
import mathlog.tools.renameExpression

class AddToContext {
    companion object {
        val forDedPrev = ForDedPrev()

        private fun getNameScheme(expressionToAdd: Expression, originalExpression: Expression): (String) -> Expression {
            return { oldName ->
                when (oldName) {
                    "A" -> expressionToAdd
                    "B" -> (originalExpression as Expression.Implication).right
                    else -> throw IllegalArgumentException("An unbelievable name inside an axiom")
                }
            }
        }
    }

    class ForDedPrev : (Expression, ProofNode) -> ProofNode {
        companion object {
            /** [ Hyp ] **/
            private const val N_PLUS_1 = "A"

            /** [MP n+1, n] **/
            private const val N_PLUS_2 = "B"

            private val expressionNPlus1 = parseExpression(N_PLUS_1)
            private val expressionNPlus2 = parseExpression(N_PLUS_2)
        }

        override fun invoke(expressionToAdd: Expression, dedPrev: ProofNode): ProofNode {
            val nameScheme = getNameScheme(expressionToAdd, dedPrev.expression)

            val nPlus1Node =
                ProofNode.Hyp(renameExpression(expressionNPlus1, nameScheme))
            return ProofNode.MP(
                renameExpression(expressionNPlus2, nameScheme),
                nPlus1Node, dedPrev
            )
        }
    }
}