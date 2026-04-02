package microarch.delivery.core.application.event.handlers;

import lombok.RequiredArgsConstructor;
import microarch.delivery.core.domain.model.order.events.OrderCompletedDomainEvent;
import microarch.delivery.core.ports.OrderCompleteProducer;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import queues.order.OrderEventsProto;

@Service
@RequiredArgsConstructor
public class OrderCompletedDomainEventHandler {

    private final OrderCompleteProducer orderCompleteProducer;


    @EventListener
    public void handle(OrderCompletedDomainEvent event) throws Exception {
        var message = OrderEventsProto.OrderCompletedIntegrationEvent.newBuilder()
                .setEventId(event.getEventId().toString())
                .setEventType("ORDER_COMPLETED")
                .setOrderId(event.getOrderId().toString())
                .setCourierId(event.getCourierId().toString())
                .build();
        orderCompleteProducer.publish(message);
    }
}
