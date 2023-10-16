import mathlog.parse.File
import mathlog.proof.ProofNode
import mathlog.proof.removeDed

fun main() {
    val file = File(generateSequence { readlnOrNull() }.takeWhile { it.isNotEmpty() })
    println(file.records.values.last().original)

    val tree = ProofNode.parseRecords(file.records)

    tree.checkDoubleLink()
    tree.resetCheckDoubleLink()
    tree.checkMP()

    val treeWithoutDed = removeDed(tree)

    treeWithoutDed.checkDoubleLink()
    treeWithoutDed.checkMP()
    treeWithoutDed.checkContext(treeWithoutDed.line.context)
    treeWithoutDed.checkHyp()

    print(treeWithoutDed.toSolution())
}