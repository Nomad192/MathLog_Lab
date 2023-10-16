package mathlog.proof

import mathlog.parse.Expression
import mathlog.parse.parseExpression
import mathlog.tools.renameExpression

class RemoveFromContext {
    companion object {
        val forMP = ForMP()
        val forAxiomOrHyp = ForAxiomOrHyp()
        val forEqual = ForEqual()

        private fun getNameScheme(
            expressionToRemove: Expression,
            originalExpression: Expression
        ): (String) -> Expression {
            return { oldName ->
                when (oldName) {
                    "A" -> expressionToRemove
                    "B" -> originalExpression
                    else -> throw IllegalArgumentException("An unbelievable name inside an axiom")
                }
            }
        }
    }

    class ForAxiomOrHyp : (Expression, ProofNode) -> ProofNode {
        companion object {
            /** [Ax. sch. 1] **/
            private const val N_PLUS_03 = "B -> A -> B"

            /** [Original Ax or Hyp] **/
            private const val N_PLUS_06 = "B"

            /** [MP n+0.6, n+0.3] **/
            private const val N_PLUS_1 = "A -> B"

            private val expressionNPlus03 = parseExpression(N_PLUS_03)
            private val expressionNPlus06 = parseExpression(N_PLUS_06)
            private val expressionNPlus1 = parseExpression(N_PLUS_1)
        }

        override fun invoke(expressionToRemove: Expression, originalNode: ProofNode): ProofNode {
            val nameScheme = getNameScheme(expressionToRemove, originalNode.expression)

            val nPlus03Node =
                ProofNode.Axiom(renameExpression(expressionNPlus03, nameScheme))
            val nPlus06Node = when (originalNode) {
                is ProofNode.Axiom ->
                    ProofNode.Axiom(renameExpression(expressionNPlus06, nameScheme))

                is ProofNode.Hyp ->
                    ProofNode.Hyp(renameExpression(expressionNPlus06, nameScheme))

                else -> throw IllegalArgumentException("This method is only for Axiom or Hyp")
            }
            return ProofNode.MP(
                renameExpression(expressionNPlus1, nameScheme),
                nPlus06Node, nPlus03Node
            )
        }
    }

    class ForEqual : (Expression, Expression) -> ProofNode {
        companion object {
            /** [Ax. sch. 1] **/
            private const val N_PLUS_02 = "A -> (A -> A)"

            /** [Ax. sch. 2] **/
            private const val N_PLUS_04 = "(A -> (A -> A)) -> (A -> (A -> A) -> A) -> (A -> A)"

            /** [MP n+0.2, n+0.4] **/
            private const val N_PLUS_06 = "(A -> (A -> A) -> A) -> (A -> A)"

            /** [Ax. sch. 1] **/
            private const val N_PLUS_08 = "(A -> (A -> A) -> A)"

            /** [MP n+0.8, n+0.6] **/
            private const val N_PLUS_1 = "A -> A"

            private val expressionNPlus02 = parseExpression(N_PLUS_02)
            private val expressionNPlus04 = parseExpression(N_PLUS_04)
            private val expressionNPlus06 = parseExpression(N_PLUS_06)
            private val expressionNPlus08 = parseExpression(N_PLUS_08)
            private val expressionNPlus1 = parseExpression(N_PLUS_1)
        }

        override fun invoke(expressionToRemove: Expression, originalExpression: Expression): ProofNode {
            val nameScheme = getNameScheme(expressionToRemove, originalExpression)


            val nPlus02Node =
                ProofNode.Axiom(renameExpression(expressionNPlus02, nameScheme))
            val nPlus04Node =
                ProofNode.Axiom(renameExpression(expressionNPlus04, nameScheme))
            val nPlus06Node = ProofNode.MP(
                renameExpression(expressionNPlus06, nameScheme),
                nPlus02Node,
                nPlus04Node
            )
            val nPlus08Node =
                ProofNode.Axiom(renameExpression(expressionNPlus08, nameScheme))
            return ProofNode.MP(
                renameExpression(expressionNPlus1, nameScheme),
                nPlus08Node,
                nPlus06Node
            )
        }
    }

    class ForMP : (Expression, ProofNode.MP) -> ProofNode {
        companion object {
            /** [Ax. sch. 2] **/
            private const val N_PLUS_03 = "(A -> J) -> (A -> J -> B) -> (A -> B)"

            /** [MP j, n+0.3] **/
            private const val N_PLUS_06 = "(A -> J -> B) -> (A -> B)"

            /** [MP n+0.6, k] **/
            private const val N_PLUS_1 = "(A -> B)"

            private val expressionNPlus03 = parseExpression(N_PLUS_03)
            private val expressionNPlus06 = parseExpression(N_PLUS_06)
            private val expressionNPlus1 = parseExpression(N_PLUS_1)
        }

        override fun invoke(expressionToRemove: Expression, mp: ProofNode.MP): ProofNode {
            val nameScheme: (String) -> Expression = { oldName ->
                when (oldName) {
                    "A" -> expressionToRemove
                    "B" -> mp.expression
                    "J" -> (mp.leftNode.expression as Expression.Implication).right
                    else -> throw IllegalArgumentException("An unbelievable name inside an axiom")
                }
            }

            val nPlus03Node =
                ProofNode.Axiom(renameExpression(expressionNPlus03, nameScheme))
            val nPlus06Node = ProofNode.MP(
                renameExpression(expressionNPlus06, nameScheme),
                mp.leftNode, nPlus03Node
            )
            return ProofNode.MP(
                renameExpression(expressionNPlus1, nameScheme),
                mp.implNode, nPlus06Node
            )
        }
    }
}