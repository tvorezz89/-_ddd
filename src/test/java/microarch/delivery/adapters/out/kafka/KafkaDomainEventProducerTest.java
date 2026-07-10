package microarch.delivery.adapters.out.kafka;

import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Volume;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.model.order.events.OrderAssignedDomainEvent;
import microarch.delivery.core.domain.model.order.events.OrderCompletedDomainEvent;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import queues.order.events.OrderEventsProto;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class KafkaDomainEventProducerTest {

    private static final String TOPIC = "order.events";

    private final KafkaTemplate<String, byte[]> kafkaTemplate = kafkaTemplate();
    private final KafkaDomainEventProducer producer = new KafkaDomainEventProducer(kafkaTemplate);

    @Test
    void shouldProduceOrderAssignedIntegrationEvent() throws Exception {
        ReflectionTestUtils.setField(producer, "topic", TOPIC);
        when(kafkaTemplate.send(eq(TOPIC), anyString(), any(byte[].class)))
                .thenReturn(CompletableFuture.completedFuture(null));
        var order = Order.mustCreate(UUID.randomUUID(), Location.mustCreate(1, 2), Volume.mustCreate(3));
        var event = new OrderAssignedDomainEvent(order);

        producer.produce(event);

        var payload = ArgumentCaptor.forClass(byte[].class);
        verify(kafkaTemplate).send(eq(TOPIC), eq(order.getId().toString()), payload.capture());
        var integrationEvent = OrderEventsProto.OrderAssignedIntegrationEvent.parseFrom(payload.getValue());
        assertThat(integrationEvent.getOrderId()).isEqualTo(order.getId().toString());
    }

    @Test
    void shouldProduceOrderCompletedIntegrationEvent() throws Exception {
        ReflectionTestUtils.setField(producer, "topic", TOPIC);
        when(kafkaTemplate.send(eq(TOPIC), anyString(), any(byte[].class)))
                .thenReturn(CompletableFuture.completedFuture(null));
        var order = Order.mustCreate(UUID.randomUUID(), Location.mustCreate(1, 2), Volume.mustCreate(3));
        var event = new OrderCompletedDomainEvent(order);

        producer.produce(event);

        var payload = ArgumentCaptor.forClass(byte[].class);
        verify(kafkaTemplate).send(eq(TOPIC), eq(order.getId().toString()), payload.capture());
        var integrationEvent = OrderEventsProto.OrderCompletedIntegrationEvent.parseFrom(payload.getValue());
        assertThat(integrationEvent.getOrderId()).isEqualTo(order.getId().toString());
    }

    @SuppressWarnings("unchecked")
    private static KafkaTemplate<String, byte[]> kafkaTemplate() {
        return mock(KafkaTemplate.class);
    }
}
