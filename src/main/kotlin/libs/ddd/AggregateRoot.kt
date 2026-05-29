package libs.ddd

interface AggregateRoot<ID : Comparable<ID>> {
    fun getId(): ID?
    fun getDomainEvents(): List<DomainEvent>
    fun clearDomainEvents()
}
