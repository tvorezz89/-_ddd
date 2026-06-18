package libs.ddd

import jakarta.persistence.MappedSuperclass
import jakarta.persistence.Transient

@MappedSuperclass
abstract class Aggregate<TId : Comparable<TId>>(id: TId) :
    BaseEntity<TId>(id = id),
    AggregateRoot<TId> {

    @Transient
    protected var events: MutableList<DomainEvent> = mutableListOf()

    override fun getId(): TId? = id

    override fun getDomainEvents(): List<DomainEvent> = events.toList()

    override fun clearDomainEvents() {
        events.clear()
    }

    protected fun raiseDomainEvent(domainEvent: DomainEvent) {
        events.add(domainEvent)
    }
}
