package microarch.delivery.core.domain.model.aggregate;

import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.OrderStatus;
import microarch.delivery.core.domain.model.Volume;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {
    private final Location destination = Location.create(2, 3);
    private final Volume volume = Volume.create(5);

    @Test
    void createsOrderWithProvidedIdAndCreatedStatus() {
        Order order = Order.create(destination, volume);
        assertEquals(destination, order.getLocation());
        assertEquals(volume, order.getVolume());
        assertEquals(OrderStatus.CREATED, order.getStatus());
    }

    @Test
    void rejectsMissingArguments() {
        assertThrows(IllegalArgumentException.class, () -> Order.create(null, volume));
        assertThrows(IllegalArgumentException.class, () -> Order.create(destination, null));
    }

    @Test
    void allowsOnlyCorrectStatusSequence() {
        Order order = newOrder();
        order.assign();
        assertEquals(OrderStatus.ASSIGNED, order.getStatus());
        order.complete();
        assertEquals(OrderStatus.COMPLETED, order.getStatus());
    }

    @Test
    void cannotCompleteCreatedOrder() {
        Order order = newOrder();
        assertThrows(IllegalStateException.class, order::complete);
        assertEquals(OrderStatus.CREATED, order.getStatus());
    }

    @Test
    void cannotAssignTwice() {
        Order order = newOrder();
        order.assign();
        assertThrows(IllegalStateException.class, order::assign);
    }

    @Test
    void cannotChangeCompletedOrder() {
        Order order = newOrder();
        order.assign();
        order.complete();
        assertThrows(IllegalStateException.class, order::assign);
        assertThrows(IllegalStateException.class, order::complete);
    }

    private Order newOrder() {
        return Order.create(destination, volume);
    }
}
