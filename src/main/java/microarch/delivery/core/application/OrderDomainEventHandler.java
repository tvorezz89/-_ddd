package microarch.delivery.core.application;

import microarch.delivery.core.domain.model.order.events.OrderAssignedDomainEvent;
import microarch.delivery.core.domain.model.order.events.OrderCompletedDomainEvent;
import microarch.delivery.core.ports.DomainEventProducer;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class OrderDomainEventHandler {
    private final DomainEventProducer producer;

    public OrderDomainEventHandler(DomainEventProducer producer1) {
        producer = producer1;
    }

    @EventListener
    public void handle(OrderAssignedDomainEvent event) {
        producer.produce(event);
    }

    @EventListener
    public void handle(OrderCompletedDomainEvent event) {
        producer.produce(event);
    }
}
