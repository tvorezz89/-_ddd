package microarch.delivery.adapters.out.kafka;

import libs.ddd.DomainEvent;
import lombok.RequiredArgsConstructor;
import microarch.delivery.core.domain.model.order.events.OrderAssignedDomainEvent;
import microarch.delivery.core.domain.model.order.events.OrderCompletedDomainEvent;
import microarch.delivery.core.ports.DomainEventProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import queues.order.events.OrderEventsProto.OrderAssignedIntegrationEvent;
import queues.order.events.OrderEventsProto.OrderCompletedIntegrationEvent;

import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
public class KafkaDomainEventProducer implements DomainEventProducer {

    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    @Value("${app.kafka.order-events-topic}")
    private String topic;

    @Override
    public void produce(DomainEvent event) {
        try {
            switch (event) {
            case OrderAssignedDomainEvent e -> {
                var integrationEvent = mapToProto(e);
                kafkaTemplate.send(topic, e.getOrderId().toString(), integrationEvent.toByteArray()).get();
            }
            case OrderCompletedDomainEvent e -> {
                var integrationEvent = mapToProto(e);
                kafkaTemplate.send(topic, e.getOrderId().toString(), integrationEvent.toByteArray()).get();
            }
            default -> throw new IllegalArgumentException("Unknown event type: " + event.getClass().getName());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Kafka publish interrupted", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Kafka publish failed", e);
        }
    }

    private OrderAssignedIntegrationEvent mapToProto(OrderAssignedDomainEvent event) {
        return OrderAssignedIntegrationEvent.newBuilder().setOrderId(event.getOrderId().toString()).build();
    }

    private OrderCompletedIntegrationEvent mapToProto(OrderCompletedDomainEvent event) {
        return OrderCompletedIntegrationEvent.newBuilder().setOrderId(event.getOrderId().toString()).build();
    }
}
