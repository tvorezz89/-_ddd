package microarch.delivery.core.domain.model.order;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderStatusTest {

    @Test
    void shouldContainThreeStatuses() {
        var statuses = OrderStatus.values();
        assertEquals(3, statuses.length);
        assertEquals(OrderStatus.CREATED, statuses[0]);
        assertEquals(OrderStatus.ASSIGNED, statuses[1]);
        assertEquals(OrderStatus.COMPLETED, statuses[2]);
    }
}
