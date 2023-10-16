package mathlog.proof

import mathlog.parse.*

sealed class ProofNode {
    fun toSolution(): String {
        val stringBuilder = StringBuilder()
        this.toSolutionWithStringBuilder(stringBuilder)
        return stringBuilder.toString()
    }

    abstract fun toSolutionWithStringBuilder(stringBuilder: StringBuilder)
    abstract val expression: Expression

    abstract fun removeFromContext(expressionToRemove: Expression): ProofNode

    data class Axiom(override val expression: Expression) : ProofNode() {
        override fun toSolutionWithStringBuilder(stringBuilder: StringBuilder) {
            expression.toStringBuilder(stringBuilder)
            stringBuilder.append("\n")
        }

        override fun removeFromContext(expressionToRemove: Expression): ProofNode =
            RemoveFromContext.forAxiomOrHyp(expressionToRemove, this)
    }

    data class Hyp(override val expression: Expression) : ProofNode() {
        override fun toSolutionWithStringBuilder(stringBuilder: StringBuilder) {
            expression.toStringBuilder(stringBuilder)
            stringBuilder.append("\n")
        }

        override fun removeFromContext(expressionToRemove: Expression): ProofNode {
            return if (expressionToRemove == expression) RemoveFromContext.forEqual(expressionToRemove, expression)
            else RemoveFromContext.forAxiomOrHyp(expressionToRemove, this)
        }
    }

    data class MP(override val expression: Expression, var leftNode: ProofNode, var implNode: ProofNode) : ProofNode() {
        override fun toSolutionWithStringBuilder(stringBuilder: StringBuilder) {
            leftNode.toSolutionWithStringBuilder(stringBuilder)
            implNode.toSolutionWithStringBuilder(stringBuilder)
            expression.toStringBuilder(stringBuilder)
            stringBuilder.append("\n")
        }

        override fun removeFromContext(expressionToRemove: Expression): ProofNode {
            leftNode = leftNode.removeFromContext(expressionToRemove)
            implNode = implNode.removeFromContext(expressionToRemove)
            return RemoveFromContext.forMP(expressionToRemove, this)
        }
    }

    data class Ded(override val expression: Expression, var prev: ProofNode) : ProofNode() {
        override fun toSolutionWithStringBuilder(stringBuilder: StringBuilder) {
            prev.toSolutionWithStringBuilder(stringBuilder)
            expression.toStringBuilder(stringBuilder)
            stringBuilder.append("\n")
        }

        override fun removeFromContext(expressionToRemove: Expression): ProofNode {
            throw IllegalArgumentException("Unable to remove context from Ded")
        }
    }

    companion object {
        fun parseRecords(records: Map<Int, Record>, record: Record = records.values.last()): ProofNode {
            when (val rule = record.rule) {
                is Rule.Axiom -> return Axiom(record.line.expression)
                is Rule.Hyp -> return Hyp(record.line.expression)
                is Rule.MP -> {
                    val left = parseRecords(records, records[rule.leftNumber]!!)
                    val impl = parseRecords(records, records[rule.implNumber]!!)
                    return MP(record.line.expression, left, impl)
                }

                is Rule.Ded -> {
                    val prev = parseRecords(records, records[rule.number]!!)
                    return Ded(record.line.expression, prev)
                }

                Rule.Incorrect -> throw IllegalArgumentException("The proof is incorrect: $record")
                Rule.Empty -> throw IllegalArgumentException("The proof was not parsed: $record")
            }
        }
    }
}