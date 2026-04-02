package microarch.delivery.core.application.event.handlers;

import lombok.RequiredArgsConstructor;
import microarch.delivery.core.domain.model.order.events.OrderCreatedDomainEvent;
import microarch.delivery.core.ports.OrderCreateProducer;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import queues.order.OrderEventsProto;

@Service
@RequiredArgsConstructor
public class OrderCreatedDomainEventHandler {

    private final OrderCreateProducer orderCreateProducer;

    @EventListener
    public void handle(OrderCreatedDomainEvent event) throws Exception {
        var message = OrderEventsProto.OrderCreatedIntegrationEvent.newBuilder()
                .setEventId(event.getEventId().toString())
                .setEventType("ORDER_CREATED")
                .setOrderId(event.getOrderId().toString())
                .build();
        orderCreateProducer.publish(message);
    }
}
