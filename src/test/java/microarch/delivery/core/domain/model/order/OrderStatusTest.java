package microarch.delivery.core.domain.model.order;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderStatusTest {

    @Test
    void shouldContainBusinessStatuses() {
        assertThat(OrderStatus.values()).containsExactly(OrderStatus.CREATED, OrderStatus.ASSIGNED,
                OrderStatus.COMPLETED);
    }
}
