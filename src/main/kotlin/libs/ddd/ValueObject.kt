package libs.ddd

import java.math.BigDecimal
import java.util.Objects

abstract class ValueObject<T : ValueObject<T>> : Comparable<T> {

    protected abstract fun equalityComponents(): Iterable<Any>

    companion object {
        @Suppress("UNCHECKED_CAST")
        protected fun <V> safeCompare(a: V, b: V): Int {
            if (a === b) return 0
            if (a == null) return -1
            if (b == null) return 1

            return when {
                a is BigDecimal && b is BigDecimal -> a.compareTo(b)
                a is Comparable<*> && b is Comparable<*> -> (a as Comparable<V>).compareTo(b as V)
                else -> throw IllegalArgumentException("Fields must be Comparable")
            }
        }
    }

    private fun toList(iterable: Iterable<Any>): List<Any> = iterable.toList()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val that = other as? ValueObject<*> ?: return false

        val thisComponents = this.equalityComponents().toList()
        val thatComponents = that.equalityComponents().toList()

        return thisComponents == thatComponents
    }

    override fun hashCode(): Int = Objects.hash(toList(equalityComponents()).toTypedArray())

    override fun compareTo(other: T): Int {
        other as ValueObject<*>
        val thisComponents = toList(equalityComponents())
        val otherComponents = toList(other.equalityComponents())

        for (i in 0 until minOf(thisComponents.size, otherComponents.size)) {
            val result = safeCompare(thisComponents[i], otherComponents[i])
            if (result != 0) return result
        }
        return thisComponents.size.compareTo(otherComponents.size)
    }

    override fun toString(): String = "${javaClass.simpleName}[${equalityComponents().toList().joinToString()}]"
}
