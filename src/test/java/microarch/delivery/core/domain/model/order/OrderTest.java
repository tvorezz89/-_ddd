package microarch.delivery.core.domain.model.order;

import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Volume;
import microarch.delivery.core.domain.model.order.events.OrderAssignedDomainEvent;
import microarch.delivery.core.domain.model.order.events.OrderCompletedDomainEvent;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTest {

    @Test
    void shouldCreateOrderInCreatedStatus() {
        var id = UUID.randomUUID();
        var location = Location.mustCreate(4, 6);
        var volume = Volume.mustCreate(7);

        var result = Order.create(id, location, volume);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue().getId()).isEqualTo(id);
        assertThat(result.getValue().getLocation()).isEqualTo(location);
        assertThat(result.getValue().getVolume()).isEqualTo(volume);
        assertThat(result.getValue().getStatus()).isEqualTo(OrderStatus.CREATED);
    }

    @Test
    void shouldReturnErrorWhenIdIsEmpty() {
        var result = Order.create(new UUID(0L, 0L), Location.mustCreate(1, 1), Volume.mustCreate(1));

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError()).isNotNull();
    }

    @Test
    void shouldThrowWhenRequiredArgumentsAreNull() {
        assertThatThrownBy(() -> Order.create(UUID.randomUUID(), null, Volume.mustCreate(1)))
                .isInstanceOf(NullPointerException.class).hasMessage("location");

        assertThatThrownBy(() -> Order.create(UUID.randomUUID(), Location.mustCreate(1, 1), null))
                .isInstanceOf(NullPointerException.class).hasMessage("volume");
    }

    @Test
    void shouldAssignOnlyCreatedOrder() {
        var order = Order.mustCreate(UUID.randomUUID(), Location.mustCreate(3, 3), Volume.mustCreate(4));

        var result = order.assign();

        assertThat(result.isSuccess()).isTrue();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.ASSIGNED);
        assertThat(order.assign().isFailure()).isTrue();
    }

    @Test
    void shouldRaiseOrderAssignedDomainEventWhenOrderIsAssigned() {
        var order = Order.mustCreate(UUID.randomUUID(), Location.mustCreate(3, 3), Volume.mustCreate(4));

        order.assign().getOrElseThrow();

        assertThat(order.getDomainEvents()).hasSize(1);
        assertThat(order.getDomainEvents().getFirst()).isInstanceOf(OrderAssignedDomainEvent.class);
        var event = (OrderAssignedDomainEvent) order.getDomainEvents().getFirst();
        assertThat(event.getOrderId()).isEqualTo(order.getId());
    }

    @Test
    void shouldCompleteOnlyAssignedOrder() {
        var order = Order.mustCreate(UUID.randomUUID(), Location.mustCreate(3, 3), Volume.mustCreate(4));

        assertThat(order.complete().isFailure()).isTrue();

        order.assign().getOrElseThrow();
        var result = order.complete();

        assertThat(result.isSuccess()).isTrue();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        assertThat(order.complete().isFailure()).isTrue();
    }

    @Test
    void shouldRaiseOrderCompletedDomainEventWhenOrderIsCompleted() {
        var order = Order.mustCreate(UUID.randomUUID(), Location.mustCreate(3, 3), Volume.mustCreate(4));

        order.assign().getOrElseThrow();
        order.clearDomainEvents();
        order.complete().getOrElseThrow();

        assertThat(order.getDomainEvents()).hasSize(1);
        assertThat(order.getDomainEvents().getFirst()).isInstanceOf(OrderCompletedDomainEvent.class);
        var event = (OrderCompletedDomainEvent) order.getDomainEvents().getFirst();
        assertThat(event.getOrderId()).isEqualTo(order.getId());
    }

    @Test
    void shouldBeAggregate() {
        assertThat(Order.class.getSuperclass().getSimpleName()).isEqualTo("Aggregate");
    }

    @Test
    void shouldHaveImmutableOrderProperties() throws NoSuchFieldException {
        var locationField = Order.class.getDeclaredField("location");
        var volumeField = Order.class.getDeclaredField("volume");

        assertThat(Modifier.isFinal(locationField.getModifiers())).isTrue();
        assertThat(Modifier.isFinal(volumeField.getModifiers())).isTrue();
    }
}
