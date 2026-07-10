package microarch.delivery.adapters.out.postgres.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import libs.ddd.DomainEvent;
import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Volume;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.model.order.events.OrderCompletedDomainEvent;
import microarch.delivery.core.ports.DomainEventProducer;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OutboxJobTest {

    private final DomainEventProducer producer = mock(DomainEventProducer.class);
    private final OutboxJpaRepository jpa = mock(OutboxJpaRepository.class);
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final OutboxJob job = new OutboxJob(producer, jpa, objectMapper);

    @Test
    void shouldPublishUnprocessedOutboxMessagesAndMarkThemProcessed() throws Exception {
        var order = Order.mustCreate(UUID.randomUUID(), Location.mustCreate(1, 2), Volume.mustCreate(3));
        order.assign().getOrElseThrow();
        order.clearDomainEvents();
        order.complete().getOrElseThrow();
        var event = order.getDomainEvents().getFirst();
        var message = new OutboxMessage(event.getEventId(), event.getClass().getName(), order.getId().toString(),
                Order.class.getSimpleName(), objectMapper.writeValueAsString(event), event.getOccurredOnUtc());
        when(jpa.findUnprocessedMessages()).thenReturn(List.of(message));
        when(jpa.save(any(OutboxMessage.class))).thenAnswer(invocation -> invocation.getArgument(0));

        job.run();

        var eventCaptor = ArgumentCaptor.forClass(DomainEvent.class);
        verify(producer).produce(eventCaptor.capture());
        assertThat(eventCaptor.getValue()).isInstanceOf(OrderCompletedDomainEvent.class);
        assertThat(((OrderCompletedDomainEvent) eventCaptor.getValue()).getOrderId()).isEqualTo(order.getId());
        assertThat(message.getProcessedOnUtc()).isNotNull();
        verify(jpa).save(message);
    }
}
