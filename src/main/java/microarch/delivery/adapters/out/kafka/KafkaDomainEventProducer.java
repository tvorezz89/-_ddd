package microarch.delivery.adapters.out.kafka;

import java.util.concurrent.ExecutionException;

import libs.ddd.DomainEvent;
import microarch.delivery.core.domain.model.order.events.OrderAssignedDomainEvent;
import microarch.delivery.core.domain.model.order.events.OrderCompletedDomainEvent;
import microarch.delivery.core.ports.DomainEventProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import queues.order.events.OrderEventsProto.OrderAssignedIntegrationEvent;
import queues.order.events.OrderEventsProto.OrderCompletedIntegrationEvent;

@Component
public class KafkaDomainEventProducer implements DomainEventProducer {
    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    @Value("${app.kafka.order-events-topic}")
    private String topic;

    public KafkaDomainEventProducer(KafkaTemplate<String, byte[]> kafkaTemplate1) {
        kafkaTemplate = kafkaTemplate1;
    }

    @Override
    public void produce(DomainEvent event) {
        try {
            switch (event) {
            case OrderAssignedDomainEvent orderAssignedDomainEvent -> {
                var integrationEvent = OrderAssignedIntegrationEvent.newBuilder()
                        .setOrderId(orderAssignedDomainEvent.getOrderId().toString()).build();
                kafkaTemplate.send(topic, orderAssignedDomainEvent.getOrderId().toString(),
                        integrationEvent.toByteArray()).get();
            }
            case OrderCompletedDomainEvent orderCompletedDomainEvent -> {
                var integrationEvent = OrderCompletedIntegrationEvent.newBuilder()
                        .setOrderId(orderCompletedDomainEvent.getOrderId().toString()).build();
                kafkaTemplate.send(topic, orderCompletedDomainEvent.getOrderId().toString(),
                        integrationEvent.toByteArray()).get();
            }
            default -> throw new IllegalArgumentException("Unknown event type: " + event.getClass().getName());
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Kafka publish interrupted", exception);
        } catch (ExecutionException exception) {
            throw new RuntimeException("Kafka publish failed", exception);
        }
    }
}
