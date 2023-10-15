package mathlog.proof

import mathlog.parse.Expression

val removeDed = RemoveDed()

class RemoveDed: (ProofNode) -> ProofNode {
    data class DedLists(val addToContext: List<Expression>, val removeFromContext: List<Expression>)
    companion object
    {
        private fun getDedLists(dedExpression: Expression, prevExpression: Expression): DedLists {
            var prevExprCopy = prevExpression
            val addToContext: MutableList<Expression> = ArrayList()

            while (true) {
                var dedExprCopy = dedExpression
                val removeFromContext: MutableList<Expression> = ArrayList()

                while (true) {
                    if (dedExprCopy == prevExprCopy) {
                        return DedLists(addToContext, removeFromContext.reversed())
                    }

                    if (dedExprCopy !is Expression.Implication)
                        break
                    removeFromContext.add(dedExprCopy.left)
                    dedExprCopy = dedExprCopy.right
                }

                if (prevExprCopy !is Expression.Implication)
                    break
                addToContext.add(prevExprCopy.left)
                prevExprCopy = prevExprCopy.right
            }

            throw IllegalArgumentException("Is non Ded")
        }
    }

    override fun invoke(node: ProofNode): ProofNode
    {
        when(node)
        {
            is ProofNode.Axiom -> return node
            is ProofNode.Hyp -> return node
            is ProofNode.MP -> {
                node.leftNode = this(node.leftNode)
                node.implNode = this(node.implNode)

                return node
            }
            is ProofNode.Ded -> {
                this(node.prev)

                val dedLists = getDedLists(node.line.expression, node.prev.line.expression)

//                println("node: $node")
                dedLists.addToContext.forEach{
//                    println("---------------")
//                    println("add $it")
//                    println("---------------")
                    node.prev = AddToContext.forDedPrev(it, node.prev)
                }
                dedLists.removeFromContext.forEach{
//                    println("---------------")
//                    println("remove $it")
//                    println("---------------")
                    node.prev = node.prev.removeFromContext(it)
                }

                return node.prev
            }
        }
    }
}