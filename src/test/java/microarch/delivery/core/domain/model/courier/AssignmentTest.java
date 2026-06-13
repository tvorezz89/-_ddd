package microarch.delivery.core.domain.model.courier;

import libs.errs.DomainInvariantException;
import microarch.delivery.core.domain.model.order.Location;
import microarch.delivery.core.domain.model.order.Volume;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AssignmentTest {

    @Test
    void createShouldSetAssignedStatus() {
        var assignment = Assignment.create(UUID.randomUUID(), UUID.randomUUID(), Volume.of(2), Location.of(10, 10));
        assertEquals(AssignmentStatus.ASSIGNED, assignment.getStatus());
    }

    @Test
    void completeShouldSetCompletedWhenCourierIsClose() {
        var assignment = Assignment.create(UUID.randomUUID(), UUID.randomUUID(), Volume.of(2), Location.of(10, 10));

        assignment.complete(Location.of(9, 10));

        assertEquals(AssignmentStatus.COMPLETED, assignment.getStatus());
    }

    @Test
    void completeShouldThrowWhenCourierIsFar() {
        var assignment = Assignment.create(UUID.randomUUID(), UUID.randomUUID(), Volume.of(2), Location.of(10, 10));

        var exception = assertThrows(DomainInvariantException.class, () -> assignment.complete(Location.of(12, 10)));

        assertTrue(exception.getMessage().contains("invalid"));
        assertEquals(AssignmentStatus.ASSIGNED, assignment.getStatus());
    }

    @Test
    void equalsShouldUseOnlyId() {
        var id = UUID.randomUUID();
        var first = Assignment.create(id, UUID.randomUUID(), Volume.of(1), Location.of(1, 1));
        var second = Assignment.create(id, UUID.randomUUID(), Volume.of(5), Location.of(50, 50));

        assertEquals(first, second);
    }
}
