package mathlog

sealed class Expression {
    data class Conjunction(val left: Expression, val right: Expression) : Expression()
    {
        companion object {
            const val SYMBOL: String = "&"
        }

        override fun toString(): String {
            return "($left$SYMBOL$right)"
        }
    }
    data class Disjunction(val left: Expression, val right: Expression) : Expression()
    {
        companion object {
            const val SYMBOL: String = "|"
        }

        override fun toString(): String {
            return "($left$SYMBOL$right)"
        }
    }
    data class Implication(val left: Expression, val right: Expression) : Expression()
    {
        companion object {
            const val SYMBOL: String = "->"
        }

        override fun toString(): String {
            return "($left$SYMBOL$right)"
        }
    }
    data class Denial(val expression: Expression) : Expression()
    {
        companion object {
            const val SYMBOL: String = "!"
        }

        override fun toString(): String {
            return "($SYMBOL$expression)"
        }
    }
    data class Variable(val name: String) : Expression()
    {
        override fun toString(): String {
            return name
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        return when (this) {
            is Conjunction -> this.left        == (other as Conjunction).left && this.right == other.right
            is Disjunction -> this.left        == (other as Disjunction).left && this.right == other.right
            is Implication -> this.left        == (other as Implication).left && this.right == other.right
            is Denial -> this.expression  == (other as Denial).expression
            is Variable -> this.name        == (other as Variable).name
        }
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}