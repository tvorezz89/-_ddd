package microarch.delivery.core.ports;

import microarch.delivery.core.domain.model.order.events.OrderCompletedDomainEvent;
import microarch.delivery.core.domain.model.order.events.OrderCreatedDomainEvent;

public interface OrdersEventProducer {

    void produce(OrderCreatedDomainEvent domainEvent);

    void produce(OrderCompletedDomainEvent domainEvent);
}
