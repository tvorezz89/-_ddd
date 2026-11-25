package microarch.delivery.core.domain.model.aggregate;

import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.OrderStatus;
import microarch.delivery.core.domain.model.Status;
import microarch.delivery.core.domain.model.Volume;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class CourierTest {
    private final Location origin = Location.create(1, 1);

    @Test
    void createsCourierWithTwentyLiterCapacity() {
        Courier courier = Courier.create(origin, "Alex");
        assertNotNull(courier.getId());
        assertEquals("Alex", courier.getName());
        assertEquals(origin, courier.getLocation());
        assertEquals(20.0, courier.getMaxVolume().getAmount());
        assertTrue(courier.getAssignments().isEmpty());
    }

    @Test
    void rejectsBlankNameOrMissingLocation() {
        assertThrows(IllegalArgumentException.class, () -> Courier.create(null, "Alex"));
        assertThrows(IllegalArgumentException.class, () -> Courier.create(origin, null));
        assertThrows(IllegalArgumentException.class, () -> Courier.create(origin, " "));
    }

    @Test
    void rejectsOverlargeOrderEvenWhenEmpty() {
        Courier courier = newCourier();
        assertThrows(IllegalArgumentException.class, () -> courier.takeOrder(newOrder(21, origin)));
        assertTrue(courier.getAssignments().isEmpty());
    }

    @Test
    void createsAssignmentWithCorrectOrderId() {
        Courier courier = newCourier();
        Order order = newOrder(7, Location.create(2, 2));
        courier.takeOrder(order);
        var assignment = courier.getAssignments().get(0);
        assertEquals(order.getId(), assignment.getOrderId());
        assertEquals(order.getVolume(), assignment.getVolume());
        assertEquals(order.getLocation(), assignment.getLocation());
        assertEquals(Status.ASSIGNED, assignment.getStatus());
    }

    @Test
    void cannotTakeSameOrderTwice() {
        Courier courier = newCourier();
        Order order = newOrder(2, origin);
        courier.takeOrder(order);
        assertEquals(1, courier.getAssignments().size());
    }

    @Test
    void cannotTakeNonCreatedOrder() {
        Courier courier = newCourier();
        Order order = newOrder(2, origin);
        order.assign();
        assertEquals(OrderStatus.ASSIGNED, order.getStatus());
        assertFalse(courier.canTake(order));
    }

    @Test
    void completesAssignmentWithinOneCellAndReleasesCapacity() {
        Courier courier = newCourier();
        Order first = newOrder(20, Location.create(2, 1));
        courier.takeOrder(first);
        courier.completeAssignment(first);
        assertEquals(Status.COMPLETED, courier.getAssignments().get(0).getStatus());
        assertTrue(courier.canTake(newOrder(20, origin)));
    }

    @Test
    void cannotCompleteAssignmentTwoCellsAway() {
        Courier courier = newCourier();
        Order order = newOrder(3, Location.create(3, 1));
        courier.takeOrder(order);
        assertEquals(Status.ASSIGNED, courier.getAssignments().get(0).getStatus());
    }

    @Test
    void movesOneCellOrRemainsInPlace() {
        Courier courier = newCourier();
        courier.moveTo(Location.create(2, 1));
        assertEquals(Location.create(2, 1), courier.getLocation());
        courier.moveTo(Location.create(2, 1));
        assertEquals(Location.create(2, 1), courier.getLocation());
    }

    @Test
    void refusesDiagonalAndTwoCellMoves() {
        Courier courier = newCourier();
        assertThrows(IllegalArgumentException.class, () -> courier.moveTo(Location.create(2, 2)));
        assertThrows(IllegalArgumentException.class, () -> courier.moveTo(Location.create(3, 1)));
        assertEquals(origin, courier.getLocation());
    }

    @Test
    void refusesNullMoveAndNullOrder() {
        Courier courier = newCourier();
        assertThrows(IllegalArgumentException.class, () -> courier.moveTo(null));
        assertThrows(IllegalArgumentException.class, () -> courier.canTake(null));
        assertThrows(IllegalArgumentException.class, () -> courier.takeOrder(null));
    }

    private Courier newCourier() {
        return Courier.create(origin, "Courier");
    }

    private Order newOrder(double amount, Location location) {
        return Order.create(location, Volume.create(amount));
    }
}
