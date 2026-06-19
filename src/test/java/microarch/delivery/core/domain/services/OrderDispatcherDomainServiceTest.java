package microarch.delivery.core.domain.services;

import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Volume;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.model.order.OrderStatus;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OrderDispatcherDomainServiceTest {

    @Test
    void shouldAssignCreatedOrderToNearestCourier() {
        var order = orderAt(5, 5, 3);
        var nearest = Courier.mustCreate("Nearest", Location.mustCreate(4, 5));
        var farther = Courier.mustCreate("Farther", Location.mustCreate(1, 1));
        var service = new OrderDispatcherDomainServiceImpl();

        var result = service.dispatch(order, List.of(farther, nearest));

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue()).isEqualTo(nearest);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.ASSIGNED);
        assertThat(nearest.getAssignments()).hasSize(1);
        assertThat(nearest.getAssignments().getFirst().getOrderId()).isEqualTo(order.getId());
        assertThat(farther.getAssignments()).isEmpty();
    }

    @Test
    void shouldSkipCourierWhenOrderWouldExceedCapacity() {
        var order = orderAt(3, 3, 10);
        var overloadedNearest = Courier.mustCreate("Overloaded nearest", Location.mustCreate(3, 4));
        overloadedNearest.assign(UUID.randomUUID(), Volume.mustCreate(15), Location.mustCreate(2, 2)).getOrElseThrow();
        var availableFarther = Courier.mustCreate("Available farther", Location.mustCreate(10, 10));
        var service = new OrderDispatcherDomainServiceImpl();

        var result = service.dispatch(order, List.of(overloadedNearest, availableFarther));

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue()).isEqualTo(availableFarther);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.ASSIGNED);
        assertThat(overloadedNearest.getAssignments()).hasSize(1);
        assertThat(availableFarther.getAssignments()).hasSize(1);
    }

    @Test
    void shouldReturnBusinessErrorWhenNoCourierCanTakeOrder() {
        var order = orderAt(3, 3, 10);
        var overloaded = Courier.mustCreate("Overloaded", Location.mustCreate(3, 4));
        overloaded.assign(UUID.randomUUID(), Volume.mustCreate(15), Location.mustCreate(2, 2)).getOrElseThrow();
        var service = new OrderDispatcherDomainServiceImpl();

        var result = service.dispatch(order, List.of(overloaded));

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError()).isEqualTo(Order.Errors.dispatchingFailed());
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(overloaded.getAssignments()).hasSize(1);
    }

    @Test
    void shouldReturnBusinessErrorWhenCouriersAreAbsent() {
        var order = orderAt(3, 3, 10);
        var service = new OrderDispatcherDomainServiceImpl();

        var result = service.dispatch(order, List.of());

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError()).isEqualTo(Order.Errors.dispatchingFailed());
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CREATED);
    }

    @Test
    void shouldNotAssignCourierWhenOrderIsNotCreated() {
        var order = orderAt(3, 3, 10);
        order.assign().getOrElseThrow();
        var courier = Courier.mustCreate("Ivan", Location.mustCreate(3, 4));
        var service = new OrderDispatcherDomainServiceImpl();

        var result = service.dispatch(order, List.of(courier));

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError()).isEqualTo(Order.Errors.orderMustBeCreated());
        assertThat(courier.getAssignments()).isEmpty();
    }

    private static Order orderAt(int x, int y, int volume) {
        return Order.mustCreate(UUID.randomUUID(), Location.mustCreate(x, y), Volume.mustCreate(volume));
    }
}
