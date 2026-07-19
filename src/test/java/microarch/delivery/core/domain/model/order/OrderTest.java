package microarch.delivery.core.domain.model.order;

import libs.errs.DomainInvariantException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderTest {

    @Test
    void createShouldSetCreatedStatus() {
        var order = Order.create(UUID.randomUUID(), Location.of(1, 1), Volume.of(3));
        assertEquals(OrderStatus.CREATED, order.getStatus());
    }

    @Test
    void assignShouldSetAssignedStatusWhenCreated() {
        var order = Order.create(UUID.randomUUID(), Location.of(1, 1), Volume.of(3));

        order.assign();

        assertEquals(OrderStatus.ASSIGNED, order.getStatus());
    }

    @Test
    void assignShouldThrowWhenOrderIsNotCreated() {
        var order = Order.create(UUID.randomUUID(), Location.of(1, 1), Volume.of(3));
        order.assign();

        assertThrows(DomainInvariantException.class, order::assign);
    }

    @Test
    void completeShouldSetCompletedStatusWhenAssigned() {
        var order = Order.create(UUID.randomUUID(), Location.of(1, 1), Volume.of(3));
        order.assign();

        order.complete();

        assertEquals(OrderStatus.COMPLETED, order.getStatus());
    }

    @Test
    void completeShouldThrowWhenOrderIsNotAssigned() {
        var order = Order.create(UUID.randomUUID(), Location.of(1, 1), Volume.of(3));

        assertThrows(DomainInvariantException.class, order::complete);
    }
}
