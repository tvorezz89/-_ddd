package microarch.delivery.adapters.out.kafka;

import microarch.delivery.core.ports.OrderCompleteProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import queues.order.OrderEventsProto;
import org.springframework.kafka.core.KafkaTemplate;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderCompleteKafkaProducer implements OrderCompleteProducer {

    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    @Value("${app.kafka.orders-events-topic}")
    private String TOPIC;

    @Override
    public void publish(OrderEventsProto.OrderCompletedIntegrationEvent event) {
        kafkaTemplate.send(TOPIC, event.getOrderId(), event.toByteArray());
    }
}
