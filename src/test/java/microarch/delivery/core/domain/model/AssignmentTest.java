package microarch.delivery.core.domain.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AssignmentTest {

    @Test
    void complete() {
        var assignment = Assignment.create(UUID.randomUUID(), Volume.create(1), Location.create(1, 1), Status.ASSIGNED);
        assignment.complete(Location.create(1, 1));
        assertEquals(Status.COMPLETED, assignment.getStatus());
    }

    @Test
    void complete_too_big_distance() {
        var assignment = Assignment.create(UUID.randomUUID(), Volume.create(1), Location.create(1, 1), Status.ASSIGNED);
        assertThrows(IllegalArgumentException.class, () -> assignment.complete(Location.create(1, 6)));
        assertEquals(Status.ASSIGNED, assignment.getStatus());
    }
}