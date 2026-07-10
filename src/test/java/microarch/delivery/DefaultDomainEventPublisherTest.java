package microarch.delivery;

import libs.ddd.DomainEvent;
import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Volume;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.model.order.events.OrderAssignedDomainEvent;
import microarch.delivery.core.ports.DomainEventProducer;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultDomainEventPublisherTest {

    @Test
    void shouldProduceDomainEventsAndClearThem() {
        var producer = new RecordingDomainEventProducer();
        var publisher = new DefaultDomainEventPublisher(producer);
        var order = Order.mustCreate(UUID.randomUUID(), Location.mustCreate(3, 4), Volume.mustCreate(2));
        order.assign().getOrElseThrow();

        publisher.publish(List.of(order));

        assertThat(producer.events).hasSize(1);
        assertThat(producer.events.getFirst()).isInstanceOf(OrderAssignedDomainEvent.class);
        assertThat(order.getDomainEvents()).isEmpty();
    }

    private static final class RecordingDomainEventProducer implements DomainEventProducer {
        private final List<DomainEvent> events = new ArrayList<>();

        @Override
        public void produce(DomainEvent event) {
            events.add(event);
        }
    }
}
