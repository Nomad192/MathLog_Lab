package mathlog.parse

sealed class Expression {
    abstract override fun toString(): String
    abstract fun copy(): Expression
    abstract fun toStringBuilder(stringBuilder: StringBuilder)

    data class Conjunction(val left: Expression, val right: Expression) : Expression() {
        companion object {
            const val SYMBOL: String = "&"
        }

        override fun copy(): Expression = copy(left = left, right = right)

        override fun toString(): String {
            return "($left$SYMBOL$right)"
        }

        override fun toStringBuilder(stringBuilder: StringBuilder) {
            stringBuilder.append("(")
            left.toStringBuilder(stringBuilder)
            stringBuilder.append(SYMBOL)
            right.toStringBuilder(stringBuilder)
            stringBuilder.append(")")
        }
    }

    data class Disjunction(val left: Expression, val right: Expression) : Expression() {
        companion object {
            const val SYMBOL: String = "|"
        }

        override fun copy(): Expression = copy(left = left, right = right)

        override fun toString(): String {
            return "($left$SYMBOL$right)"
        }

        override fun toStringBuilder(stringBuilder: StringBuilder) {
            stringBuilder.append("(")
            left.toStringBuilder(stringBuilder)
            stringBuilder.append(SYMBOL)
            right.toStringBuilder(stringBuilder)
            stringBuilder.append(")")
        }
    }

    data class Implication(val left: Expression, val right: Expression) : Expression() {
        companion object {
            const val SYMBOL: String = "->"
        }

        override fun copy(): Expression = copy(left = left, right = right)

        override fun toString(): String {
            return "($left$SYMBOL$right)"
        }

        override fun toStringBuilder(stringBuilder: StringBuilder) {
            stringBuilder.append("(")
            left.toStringBuilder(stringBuilder)
            stringBuilder.append(SYMBOL)
            right.toStringBuilder(stringBuilder)
            stringBuilder.append(")")
        }
    }

    data class Denial(val expression: Expression) : Expression() {
        companion object {
            const val SYMBOL: String = "!"
        }

        override fun copy(): Expression = copy(expression = expression)

        override fun toString(): String {
            return "($SYMBOL$expression)"
        }

        override fun toStringBuilder(stringBuilder: StringBuilder) {
            stringBuilder.append("(")
            stringBuilder.append(SYMBOL)
            expression.toStringBuilder(stringBuilder)
            stringBuilder.append(")")
        }
    }

    data class Variable(val name: String) : Expression() {
        override fun copy(): Expression = copy(name = name)

        override fun toString(): String {
            return name
        }

        override fun toStringBuilder(stringBuilder: StringBuilder) {
            stringBuilder.append(name)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        return when (this) {
            is Conjunction -> this.left == (other as Conjunction).left && this.right == other.right
            is Disjunction -> this.left == (other as Disjunction).left && this.right == other.right
            is Implication -> this.left == (other as Implication).left && this.right == other.right
            is Denial -> this.expression == (other as Denial).expression
            is Variable -> this.name == (other as Variable).name
        }
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}