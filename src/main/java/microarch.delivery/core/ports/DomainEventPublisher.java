package microarch.delivery.core.ports;

import libs.ddd.Aggregate;

public interface DomainEventPublisher {
    void publish(Iterable<? extends Aggregate<?>> aggregates);
}