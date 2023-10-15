package mathlog.proof

import mathlog.parse.ContextObject
import mathlog.parse.Expression
import mathlog.parse.Line
import mathlog.parse.parseExpression
import mathlog.tools.renameExpression

class RemoveFromContext {
    companion object
    {
        private fun getContextWithoutExpression(expressionToRemove: Expression, originalLine: Line) : ContextObject
        {
            val contextWithoutExpression = originalLine.context.clone() as ContextObject
            contextWithoutExpression.remove(expressionToRemove)
            return contextWithoutExpression
        }

        private fun getNameScheme(expressionToRemove: Expression, originalLine: Line): (String) -> Expression
        {
            return { oldName ->
                when (oldName) {
                    "A" -> expressionToRemove
                    "B" -> originalLine.expression
                    else -> throw IllegalArgumentException("An unbelievable name inside an axiom")
                }
            }
        }

        fun forAxiomOrHyp(expressionToRemove: Expression, originalNode: ProofNode): ProofNode
        {
            val contextWithoutExpression = getContextWithoutExpression(expressionToRemove, originalNode.line)
            val nameScheme = getNameScheme(expressionToRemove, originalNode.line)

            val nPlus03 = "B -> A -> B"     /** [Ax. sch. 1] **/
            val nPlus06 = "B"               /** [Original Ax or Hyp] **/
            val nPlus1 = "A -> B"           /** [MP n+0.6, n+0.3] **/

            val nPlus03Node =
            ProofNode.Axiom(Line(contextWithoutExpression.clone() as ContextObject, renameExpression(parseExpression(nPlus03), nameScheme)))
            val nPlus06Node = if (originalNode is ProofNode.Axiom)
                ProofNode.Axiom(Line(contextWithoutExpression.clone() as ContextObject, renameExpression(parseExpression(nPlus06), nameScheme)))
            else
                ProofNode.Hyp(Line(contextWithoutExpression.clone() as ContextObject, renameExpression(parseExpression(nPlus06), nameScheme)))
            return ProofNode.MP(
                Line(contextWithoutExpression.clone() as ContextObject, renameExpression(parseExpression(nPlus1), nameScheme)),
                nPlus06Node, nPlus03Node
            )
        }

        fun forEqual(expressionToRemove: Expression, originalLine: Line): ProofNode
        {
            val contextWithoutExpression = getContextWithoutExpression(expressionToRemove, originalLine)
            val nameScheme = getNameScheme(expressionToRemove, originalLine)

            val nPlus02 = "A -> (A -> A)"                                           /** [Ax. sch. 1] **/
            val nPlus04 = "(A -> (A -> A)) -> (A -> (A -> A) -> A) -> (A -> A)"     /** [Ax. sch. 2] **/
            val nPlus06 = "(A -> (A -> A) -> A) -> (A -> A)"                        /** [MP n+0.2, n+0.4] **/
            val nPlus08 = "(A -> (A -> A) -> A)"                                    /** [Ax. sch. 1] **/
            val nPlus1 = "A -> A"                                                   /** [MP n+0.8, n+0.6] **/

            val nPlus02Node =
            ProofNode.Axiom(Line(contextWithoutExpression.clone() as ContextObject, renameExpression(parseExpression(nPlus02), nameScheme)))
            val nPlus04Node =
                ProofNode.Axiom(Line(contextWithoutExpression.clone() as ContextObject, renameExpression(parseExpression(nPlus04), nameScheme)))
            val nPlus06Node = ProofNode.MP(
                Line(contextWithoutExpression.clone() as ContextObject, renameExpression(parseExpression(nPlus06), nameScheme)),
                nPlus02Node,
                nPlus04Node
            )
            val nPlus08Node =
                ProofNode.Axiom(Line(contextWithoutExpression.clone() as ContextObject, renameExpression(parseExpression(nPlus08), nameScheme)))
            return ProofNode.MP(
                Line(contextWithoutExpression.clone() as ContextObject, renameExpression(parseExpression(nPlus1), nameScheme)),
                nPlus08Node,
                nPlus06Node
            )
        }

        fun forMP(expressionToRemove: Expression, mp: ProofNode.MP): ProofNode
        {
            val contextWithoutExpression = getContextWithoutExpression(expressionToRemove, mp.line)
            val nameScheme: (String) -> Expression = { oldName ->
                when (oldName) {
                    "A" -> expressionToRemove
                    "B" -> mp.line.expression
                    "J" -> (mp.leftNode.line.expression as Expression.Implication).right
                    else -> throw IllegalArgumentException("An unbelievable name inside an axiom")
                }
            }

            val nPlus03 = "(A -> J) -> (A -> J -> B) -> (A -> B)"   /** [Ax. sch. 2] **/
            val nPlus06 = "(A -> J -> B) -> (A -> B)"               /** [MP j, n+0.3] **/
            val nPlus1 = "(A -> B)"                                 /** [MP n+0.6, k] **/


            val nPlus03Node =
            ProofNode.Axiom(Line(contextWithoutExpression.clone() as ContextObject, renameExpression(parseExpression(nPlus03), nameScheme)))
            val nPlus06Node = ProofNode.MP(
                Line(contextWithoutExpression.clone() as ContextObject, renameExpression(parseExpression(nPlus06), nameScheme)),
                mp.leftNode, nPlus03Node
            )
            return ProofNode.MP(
                Line(contextWithoutExpression.clone() as ContextObject, renameExpression(parseExpression(nPlus1), nameScheme)),
                mp.implNode, nPlus06Node
            )
        }
    }
}