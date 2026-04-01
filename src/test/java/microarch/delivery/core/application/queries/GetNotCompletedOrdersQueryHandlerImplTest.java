package microarch.delivery.core.application.queries;

import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Volume;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.ports.OrderRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetNotCompletedOrdersQueryHandlerImplTest {

    private final OrderRepository orderRepository = mock(OrderRepository.class);
    private final GetNotCompletedOrdersQueryHandlerImpl handler = new GetNotCompletedOrdersQueryHandlerImpl(
            orderRepository);

    private static final Location LOCATION = Location.create(3, 7).getValue();

    @Test
    void handle_shouldReturnNotCompletedOrders() {
        var order1 = Order.create(UUID.randomUUID(), LOCATION, Volume.create(5).getValue()).getValue();
        var order2 = Order.create(UUID.randomUUID(), Location.create(1, 1).getValue(), Volume.create(10).getValue())
                .getValue();
        when(orderRepository.findAllNotCompleted()).thenReturn(List.of(order1, order2));

        var result = handler.handle(new GetNotCompletedOrdersQuery());

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getLocationX()).isEqualTo(3);
        assertThat(result.get(0).getLocationY()).isEqualTo(7);
        assertThat(result.get(1).getLocationX()).isEqualTo(1);
    }

    @Test
    void handle_shouldReturnEmptyList_whenAllOrdersCompleted() {
        when(orderRepository.findAllNotCompleted()).thenReturn(List.of());

        var result = handler.handle(new GetNotCompletedOrdersQuery());

        assertThat(result).isEmpty();
    }
}
