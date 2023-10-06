data class File(val records: List<Record>)
{
    init {
        records.forEach { it.rule = getRule(records, it) }
    }

    companion object
    {
        private fun getRule(records: List<Record>, record: Record): String
        {
            return checkAxioms(record.line.expression)?.let { "Ax. sch. $it" }
                ?: checkHyp(record)?.let { "Hyp. $it" }
                ?: checkMP(records, record)?.let { "M.P. ${it.first}, ${it.second}" }
                ?: checkDed(records, record)?.let { "Ded. $it" }
                ?: "Incorrect"
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
                if (recordImpl.rule == "") break
                if (recordImpl.line.expression is Expression.Implication
                    && recordImpl.line.expression.right == currentRecord.line.expression
                    && recordImpl.line.context == currentRecord.line.context)
                {
                    for ((j, record) in records.withIndex())
                    {
                        if (i == j) continue
                        if (record.rule == "") break
                        if (record.line.expression == recordImpl.line.expression.left
                            && record.line.context == currentRecord.line.context)
                            return Pair(record.number, recordImpl.number)
                    }
                }
            }

            return null
        }

        private fun checkDed(records: List<Record>, currentRecord: Record): Int? {
            return records.firstOrNull { it.rule.isNotBlank() && it.convertedLine == currentRecord.convertedLine }?.number
        }
    }
}