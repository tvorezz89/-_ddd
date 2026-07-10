package microarch.delivery.adapters.out.postgres.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Volume;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.model.order.events.OrderAssignedDomainEvent;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OutboxDomainEventPublisherTest {

    private final OutboxJpaRepository jpa = mock(OutboxJpaRepository.class);
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final OutboxDomainEventPublisher publisher = new OutboxDomainEventPublisher(jpa, objectMapper);

    @Test
    void shouldPersistDomainEventsToOutboxAndClearAggregateEvents() throws Exception {
        var order = Order.mustCreate(UUID.randomUUID(), Location.mustCreate(3, 4), Volume.mustCreate(2));
        order.assign().getOrElseThrow();
        var domainEvent = order.getDomainEvents().getFirst();
        var savedMessage = org.mockito.ArgumentCaptor.forClass(OutboxMessage.class);
        when(jpa.save(any(OutboxMessage.class))).thenAnswer(invocation -> invocation.getArgument(0));

        publisher.publish(List.of(order));

        verify(jpa).save(savedMessage.capture());
        var message = savedMessage.getValue();
        assertThat(message.getId()).isEqualTo(domainEvent.getEventId());
        assertThat(message.getEventType()).isEqualTo(OrderAssignedDomainEvent.class.getName());
        assertThat(message.getAggregateId()).isEqualTo(order.getId().toString());
        assertThat(message.getAggregateType()).isEqualTo(Order.class.getSimpleName());
        assertThat(message.getOccurredOnUtc()).isNotNull();
        assertThat(message.getProcessedOnUtc()).isNull();

        var event = objectMapper.readValue(message.getPayload(), OrderAssignedDomainEvent.class);
        assertThat(event.getOrderId()).isEqualTo(order.getId());
        assertThat(order.getDomainEvents()).isEmpty();
    }
}
