package mathlog.proof

import mathlog.parse.ContextObject
import mathlog.parse.Expression
import mathlog.parse.Line
import mathlog.parse.parseExpression
import mathlog.tools.renameExpression

class AddToContext {
    companion object
    {
//        private fun getContextWithExpression(expressionToAdd: Expression, originalLine: Line) : ContextObject
//        {
//            val contextWithoutExpression = originalLine.context.clone() as ContextObject
//            contextWithoutExpression.add(expressionToAdd)
//            return contextWithoutExpression
//        }

        private fun getNameScheme(expressionToAdd: Expression, originalLine: Line): (String) -> Expression
        {
            return { oldName ->
                when (oldName) {
                    "A" -> expressionToAdd
                    "B" -> (originalLine.expression as Expression.Implication).right
                    else -> throw IllegalArgumentException("An unbelievable name inside an axiom")
                }
            }
        }

        fun forDedPrev(expressionToAdd: Expression, dedPrev: ProofNode): ProofNode
        {
//            println(expressionToAdd)

//            println("=-00-0=-0-=0=0=-0-==0-=00-0==-0-=0-0=-0-0")
//            println(dedPrev.line.context)
            dedPrev.addToContext(expressionToAdd)
//            println(dedPrev.line.context)
//            println("=-00-0=-0-=0=0=-0-==0-=00-0==-0-=0-0=-0-0")

            val contextWithExpression = dedPrev.line.context.clone() as ContextObject
            val nameScheme = getNameScheme(expressionToAdd, dedPrev.line)

            val nPlus1  = "A"       /** [ Hyp ] **/
            val nPlus2  = "B"       /** [MP n+1, n] **/

            val nPlus1Node =
            ProofNode.Hyp(Line(contextWithExpression.clone() as ContextObject, renameExpression(parseExpression(nPlus1), nameScheme)))
            return ProofNode.MP(
                Line(contextWithExpression.clone() as ContextObject, renameExpression(parseExpression(nPlus2), nameScheme)),
                nPlus1Node, dedPrev
            )
        }
    }
}