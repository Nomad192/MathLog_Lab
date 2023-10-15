package mathlog.proof

import mathlog.parse.*

sealed class ProofNode {
    abstract fun toSolution(): String
    abstract val line: Line
    private var numberToLink = 0

    open fun checkDoubleLink()
    {
//        println("-=-=-=-=-=---=-=-=--=--=-=--=-=-=-")
//        println(this)
//        println("checkDoubleLink $numberToLink")
//        println("-=-=-=-=-=---=-=-=--=--=-=--=-=-=-")
        if (numberToLink == 1)
            throw IllegalArgumentException("Double node linking")
        numberToLink += 1
    }

    open fun resetCheckDoubleLink()
    {
        numberToLink = 0
    }

    open fun checkMP() {}

    open fun checkContext(context: ContextObject)
    {
        if(line.context != context)
            throw IllegalArgumentException("The context doesn't match")
    }

    open fun checkHyp() {}

    open fun addToContext(expressionToAdd: Expression) = line.context.add(expressionToAdd)
    abstract fun removeFromContext(expressionToRemove: Expression): ProofNode

    data class Axiom(override val line: Line) : ProofNode() {
        override fun toSolution(): String {
            return line.expression.toString() + "\n"
        }

        override fun removeFromContext(expressionToRemove: Expression): ProofNode
        = RemoveFromContext.forAxiomOrHyp(expressionToRemove, this)
    }

    data class Hyp(override val line: Line) : ProofNode() {
        override fun toSolution(): String {
            return line.expression.toString() + "\n"
        }

        override fun removeFromContext(expressionToRemove: Expression): ProofNode {
//            println("=-990=0-9009=090099=-9=9--0--9=9-9-9-9990-")
//            println(expressionToRemove)
//            println(line.expression)
//            println("=-990=0-9009=090099=-9=9--0--9=9-9-9-9990-")
            return if (expressionToRemove == line.expression) RemoveFromContext.forEqual(expressionToRemove, line)
            else RemoveFromContext.forAxiomOrHyp(expressionToRemove, this)
        }

        override fun checkHyp() {
            if (!line.context.containsKey(line.expression)) {
                throw IllegalArgumentException("The hypothesis is not contained in the context")
            }
        }
    }

    data class MP(override val line: Line, var leftNode: ProofNode, var implNode: ProofNode) : ProofNode() {
        override fun toSolution(): String {
            return leftNode.toSolution() + implNode.toSolution() + line.expression.toString() + "\n"
        }

        override fun checkDoubleLink()
        {
            leftNode.checkDoubleLink()
            implNode.checkDoubleLink()
            super.checkDoubleLink()
        }

        override fun resetCheckDoubleLink()
        {
            leftNode.resetCheckDoubleLink()
            implNode.resetCheckDoubleLink()
            super.resetCheckDoubleLink()
        }

        override fun checkMP()
        {
            leftNode.checkMP()
            implNode.checkMP()

            if(implNode.line.expression !is Expression.Implication)
                throw IllegalArgumentException("MP: implNode is not implication")

            if(leftNode.line.expression != (implNode.line.expression as Expression.Implication).left)
                throw IllegalArgumentException("MP: leftNode is not equal implNode.left")

            if(this.line.expression != (implNode.line.expression as Expression.Implication).right)
                throw IllegalArgumentException("MP: this expression is not equal implNode.right")
        }

        override fun checkContext(context: ContextObject)
        {
            leftNode.checkContext(context)
            implNode.checkContext(context)
            super.checkContext(context)
        }

        override fun checkHyp() {
            leftNode.checkHyp()
            implNode.checkHyp()
        }

        override fun addToContext(expressionToAdd: Expression) {
            leftNode.addToContext(expressionToAdd)
            implNode.addToContext(expressionToAdd)
            line.context.add(expressionToAdd)
        }

        override fun removeFromContext(expressionToRemove: Expression): ProofNode {
            leftNode = leftNode.removeFromContext(expressionToRemove)
            implNode = implNode.removeFromContext(expressionToRemove)
            return RemoveFromContext.forMP(expressionToRemove, this)
        }
    }

    data class Ded(override val line: Line, var prev: ProofNode) : ProofNode() {
        override fun toSolution(): String {
            return prev.toSolution() + line.expression.toString() + "\n"
        }

        override fun checkDoubleLink()
        {
            prev.checkDoubleLink()
            super.checkDoubleLink()
        }

        override fun resetCheckDoubleLink()
        {
            prev.resetCheckDoubleLink()
            super.resetCheckDoubleLink()
        }

        override fun checkMP()
        {
            prev.checkMP()
        }

        override fun checkHyp() {
            prev.checkHyp()
        }

        override fun checkContext(context: ContextObject)
        {
            prev.checkContext(context)
            super.checkContext(context)
        }

        override fun removeFromContext(expressionToRemove: Expression): ProofNode {
            throw IllegalArgumentException("Unable to remove context from Ded")
        }

        override fun addToContext(expressionToAdd: Expression) {
            throw IllegalArgumentException("Unable to add context from Ded")
        }
    }

    companion object
    {
        fun parseRecords(records: Map<Int, Record>, record: Record = records.values.last()): ProofNode
        {
            when(val rule = record.rule)
            {
                is Rule.Axiom -> return Axiom(record.line.copy())
                is Rule.Hyp -> return Hyp(record.line.copy())
                is Rule.MP -> {
                    val left = parseRecords(records, records[rule.leftNumber]!!)
                    val impl = parseRecords(records, records[rule.implNumber]!!)
                    return MP(record.line.copy(), left, impl)
                }
                is Rule.Ded -> {
                    val prev = parseRecords(records, records[rule.number]!!)
                    return Ded(record.line.copy(), prev)
                }
                Rule.Incorrect -> throw IllegalArgumentException("The proof is incorrect: $record")
                Rule.Empty -> throw IllegalArgumentException("The proof was not parsed: $record")
            }
        }
    }
}