package microarch.delivery.core.domain.model.courier;

import libs.errs.DomainInvariantException;
import microarch.delivery.core.domain.model.order.Location;
import microarch.delivery.core.domain.model.order.Volume;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CourierTest {

    @Test
    void createShouldUseMaxVolumeTwenty() {
        var courier = Courier.create("Ivan", Location.of(1, 1));
        assertEquals(20, courier.getMaxVolume().getValue());
    }

    @Test
    void canTakeOneMoreOrderShouldReturnTrueWhenWithinLimit() {
        var courier = Courier.create("Ivan", Location.of(1, 1));
        assertTrue(courier.canTakeAdditionalOrder(Volume.of(5)));
    }

    @Test
    void canTakeOneMoreOrderShouldReturnFalseWhenLimitExceeded() {
        var courier = Courier.create("Ivan", Location.of(1, 1));
        courier.assignOrder(UUID.randomUUID(), Volume.of(20), Location.of(2, 2));
        assertFalse(courier.canTakeAdditionalOrder(Volume.of(1)));
    }

    @Test
    void assignOrderShouldAddAssignment() {
        var courier = Courier.create("Ivan", Location.of(1, 1));

        courier.assignOrder(UUID.randomUUID(), Volume.of(4), Location.of(2, 2));

        assertEquals(1, courier.getAssignments().size());
    }

    @Test
    void assignOrderShouldThrowWhenLimitExceeded() {
        var courier = Courier.create("Ivan", Location.of(1, 1));
        courier.assignOrder(UUID.randomUUID(), Volume.of(20), Location.of(2, 2));

        assertThrows(DomainInvariantException.class,
                () -> courier.assignOrder(UUID.randomUUID(), Volume.of(1), Location.of(2, 2)));
    }

    @Test
    void completeAssignmentShouldSetAssignmentCompletedWhenCourierIsClose() {
        var courier = Courier.create("Ivan", Location.of(1, 1));
        var orderId = UUID.randomUUID();
        courier.assignOrder(orderId, Volume.of(4), Location.of(2, 2));

        courier.completeAssignment(orderId);

        assertEquals(AssignmentStatus.COMPLETED, courier.getAssignments().getFirst().getStatus());
    }

    @Test
    void completeAssignmentShouldThrowWhenCourierIsFar() {
        var courier = Courier.create("Ivan", Location.of(1, 1));
        var orderId = UUID.randomUUID();
        courier.assignOrder(orderId, Volume.of(4), Location.of(5, 5));

        assertThrows(DomainInvariantException.class, () -> courier.completeAssignment(orderId));
    }

    @Test
    void moveShouldUpdateLocation() {
        var courier = Courier.create("Ivan", Location.of(1, 1));

        courier.move(Location.of(2, 3));

        assertEquals(Location.of(2, 3), courier.getLocation());
    }
}
