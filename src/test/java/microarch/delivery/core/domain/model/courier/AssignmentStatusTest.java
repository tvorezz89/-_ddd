package microarch.delivery.core.domain.model.courier;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AssignmentStatusTest {

    @Test
    void shouldContainTwoStatuses() {
        var statuses = AssignmentStatus.values();
        assertEquals(2, statuses.length);
        assertEquals(AssignmentStatus.ASSIGNED, statuses[0]);
        assertEquals(AssignmentStatus.COMPLETED, statuses[1]);
    }
}
