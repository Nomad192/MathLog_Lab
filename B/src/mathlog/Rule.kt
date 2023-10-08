package mathlog

sealed class Rule {
    abstract val prefix: String
    abstract val full: String

    final override fun toString(): String {
        return full
    }

    data class Axiom(val number: Int) : Rule() {
        override val prefix: String = "Ax. sch."
        override val full: String = "$prefix $number"
    }

    data class Hyp(val number: Int) : Rule() {
        override val prefix: String = "Hyp."
        override val full: String = "$prefix $number"
    }

    data class MP(val leftNumber: Int, val rightNumber: Int) : Rule() {
        override val prefix: String = "M.P."
        override val full: String = "$prefix $leftNumber, $rightNumber"
    }

    data class Ded(val number: Int) : Rule() {
        override val prefix: String = "Ded."
        override val full: String = "$prefix $number"
    }

    object Incorrect: Rule() {
        override val prefix: String = "Incorrect"
        override val full: String = prefix
    }

    object Empty: Rule() {
        override val prefix: String = ""
        override val full: String = prefix
    }

    companion object
    {
        fun getRule(records: List<Record>, record: Record): Rule
        {
            return checkAxioms(record.line.expression)?.let { Axiom(it) }
                ?: checkHyp(record)?.let { Hyp(it) }
                ?: checkMP(records, record)?.let { MP(it.first, it.second) }
                ?: checkDed(records, record)?.let { Ded(it) }
                ?: Incorrect
        }

        private fun checkAxioms(expression: Expression): Int? {
            return Axioms.entries.firstOrNull { equalAxiom(it.value, expression) }?.key
        }

        private fun checkHyp(currentRecord: Record): Int?
        {
            val index = currentRecord.contextOriginalList.indexOfFirst { it == currentRecord.line.expression }
            return if (index != -1) index + 1 else null
        }

        private fun checkMP(records: List<Record>, currentRecord: Record): Pair<Int, Int>?
        {
            for((i, recordImpl) in records.withIndex())
            {
                if (recordImpl.rule is Empty) break
                if (recordImpl.line.expression is Expression.Implication
                    && recordImpl.line.expression.right == currentRecord.line.expression
                    && recordImpl.line.context == currentRecord.line.context)
                {
                    for ((j, record) in records.withIndex())
                    {
                        if (i == j) continue
                        if (record.rule is Empty) break
                        if (record.line.expression == recordImpl.line.expression.left
                            && record.line.context == currentRecord.line.context)
                            return Pair(record.number, recordImpl.number)
                    }
                }
            }

            return null
        }

        private fun checkDed(records: List<Record>, currentRecord: Record): Int? {
            return records.firstOrNull { it.rule !is Empty && it.convertedLine == currentRecord.convertedLine }?.number
        }
    }
}