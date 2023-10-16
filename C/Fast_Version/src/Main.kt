import mathlog.parse.File
import mathlog.proof.ProofNode
import mathlog.proof.removeDed

fun main() {
    val file = File(generateSequence { readlnOrNull() }.takeWhile { it.isNotEmpty() })
    println(file.records.values.last().original)
    print(removeDed(ProofNode.parseRecords(file.records)).toSolution())
}