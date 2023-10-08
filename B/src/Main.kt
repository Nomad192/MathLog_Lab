import mathlog.File
import mathlog.Record
import mathlog.Rule

fun main() {
    val file = File( generateSequence { readlnOrNull() }.takeWhile { it.isNotEmpty() } )
    println(file)
}