package microarch.delivery.adapters.out.kafka;

import com.google.protobuf.Timestamp;
import lombok.RequiredArgsConstructor;
import microarch.delivery.core.domain.model.order.events.OrderCompletedDomainEvent;
import microarch.delivery.core.domain.model.order.events.OrderCreatedDomainEvent;
import microarch.delivery.core.ports.OrdersEventProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import queues.order.OrderEventsProto;

@Component
@RequiredArgsConstructor
public class OrderKafkaEventProducer implements OrdersEventProducer {

    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    @Value("${app.kafka.orders-events-topic}")
    private String topic;

    @Override
    public void produce(OrderCreatedDomainEvent domainEvent) {
        var integrationEvent = mapToOrderCompleted(domainEvent);

        kafkaTemplate.send(topic, domainEvent.getOrderId().toString(), integrationEvent.toByteArray());
    }

    @Override
    public void produce(OrderCompletedDomainEvent domainEvent) {
        var integrationEvent = mapToOrderCompleted(domainEvent);

        kafkaTemplate.send(topic, domainEvent.getOrderId().toString(), integrationEvent.toByteArray());
    }

    private OrderEventsProto.OrderCreatedIntegrationEvent mapToOrderCompleted(OrderCreatedDomainEvent event) {
        return OrderEventsProto.OrderCreatedIntegrationEvent.newBuilder().setEventId(event.getEventId().toString())
                .setOrderId(event.getOrderId().toString())
                .setOccurredAt(Timestamp.newBuilder().setNanos(event.getOccurredOnUtc().getNano()).build())
                .setEventType("order.created").build();
    }

    private OrderEventsProto.OrderCompletedIntegrationEvent mapToOrderCompleted(OrderCompletedDomainEvent event) {
        return OrderEventsProto.OrderCompletedIntegrationEvent.newBuilder().setEventId(event.getEventId().toString())
                .setOrderId(event.getOrderId().toString())
                .setOccurredAt(Timestamp.newBuilder().setNanos(event.getOccurredOnUtc().getNano()).build())
                .setEventType("order.completed").setCourierId(event.getCourierId().toString()).build();
    }
}
