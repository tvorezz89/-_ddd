package microarch.delivery.core.application.eventHandler;

import lombok.RequiredArgsConstructor;
import microarch.delivery.core.domain.model.order.events.OrderCompletedDomainEvent;
import microarch.delivery.core.ports.OrdersEventProducer;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderCompletedEventHandler {

    private final OrdersEventProducer eventProducer;

    @EventListener
    public void handle(OrderCompletedDomainEvent domainEvent) {
        eventProducer.produce(domainEvent);
    }
}
