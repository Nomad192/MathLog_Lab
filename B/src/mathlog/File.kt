package mathlog

data class File(val records: List<Record>)
{
    init {
        records.forEach { it.rule = Rule.getRule(records, it) }
    }
}