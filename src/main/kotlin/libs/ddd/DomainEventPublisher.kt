package libs.ddd

interface DomainEventPublisher {
    fun publish(aggregates: Iterable<out Aggregate<*>>)
}
