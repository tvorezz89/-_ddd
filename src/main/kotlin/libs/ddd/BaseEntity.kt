package libs.ddd

import jakarta.persistence.Column
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import java.util.Objects

@MappedSuperclass
abstract class BaseEntity<TId : Comparable<TId>> : Comparable<BaseEntity<TId>> {

    @Id
    @JvmField
    @Column(name = "id")
    protected var id: TId? = null

    constructor()

    constructor(id: TId) {
        this.id = id
    }

    protected fun isTransient(): Boolean = id == null || id == defaultValue()

    protected open fun defaultValue(): TId? = null

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (this === other) return true
        if (other !is BaseEntity<*>) return false
        if (this::class.java != other::class.java) return false
        if (this.isTransient() || other.isTransient()) return false
        return Objects.equals(this.id, other.id)
    }

    override fun hashCode(): Int = (this::class.java.toString() + (id?.toString() ?: "")).hashCode()

    override fun compareTo(other: BaseEntity<TId>): Int {
        if (this === other) return 0
        return this.id!!.compareTo(other.id!!)
    }
}
