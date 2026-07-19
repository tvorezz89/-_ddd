package microarch.delivery.core.domain.service;

import java.util.List;
import java.util.UUID;

import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Location;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.model.order.OrderStatus;
import microarch.delivery.core.domain.model.order.Volume;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderDispatchServiceTest {
    private final OrderDispatchService service = new OrderDispatchServiceImpl();

    @Test
    void dispatchShouldAssignToNearestAvailableCourier() {
        var order = Order.create(UUID.randomUUID(), Location.of(5, 5), Volume.of(2));
        var farCourier = Courier.create("Far", Location.of(1, 1));
        var nearCourier = Courier.create("Near", Location.of(5, 4));

        var result = service.dispatch(order, List.of(farCourier, nearCourier));

        assertTrue(result.isSuccess());
        assertEquals(nearCourier.getId(), result.getValue().getId());
        assertEquals(OrderStatus.ASSIGNED, order.getStatus());
        assertEquals(1, nearCourier.getAssignments().size());
    }

    @Test
    void dispatchShouldSkipOverloadedCourier() {
        var order = Order.create(UUID.randomUUID(), Location.of(5, 5), Volume.of(2));
        var overloadedCourier = Courier.create("Overloaded", Location.of(5, 4));
        overloadedCourier.assignOrder(UUID.randomUUID(), Volume.of(20), Location.of(3, 3));
        var availableCourier = Courier.create("Available", Location.of(8, 8));

        var result = service.dispatch(order, List.of(overloadedCourier, availableCourier));

        assertTrue(result.isSuccess());
        assertEquals(availableCourier.getId(), result.getValue().getId());
    }

    @Test
    void dispatchShouldReturnFailureWhenAllCouriersAreOverloaded() {
        var order = Order.create(UUID.randomUUID(), Location.of(5, 5), Volume.of(2));
        var overloadedCourier = Courier.create("Overloaded", Location.of(5, 4));
        overloadedCourier.assignOrder(UUID.randomUUID(), Volume.of(20), Location.of(3, 3));

        var result = service.dispatch(order, List.of(overloadedCourier));

        assertTrue(result.isFailure());
        assertEquals("courier.not.available", result.getError().getCode());
        assertEquals(OrderStatus.CREATED, order.getStatus());
    }
}
