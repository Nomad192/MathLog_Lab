import mathlog.parse.File
import mathlog.proof.ProofNode
import mathlog.proof.removeDed

fun main() {
    val file = File( generateSequence { readlnOrNull() }.takeWhile { it.isNotEmpty() } )
//    println(file)
    println(file.records.values.last().original)



    val tree = ProofNode.parseRecords(file.records)
    
//    println("===============================")
//    println(tree)
//    println("===============================")
//    println(tree.toSolution())
//    println("===============================")

    tree.checkDoubleLink()
    tree.resetCheckDoubleLink()
    tree.checkMP()

    val treeWithoutDed = removeDed(tree)
//    println(treeWithoutDed)
//    println("===============================")

    treeWithoutDed.checkDoubleLink()
    treeWithoutDed.checkMP()
    treeWithoutDed.checkContext(treeWithoutDed.line.context)
    treeWithoutDed.checkHyp()

    println(treeWithoutDed.toSolution())
}