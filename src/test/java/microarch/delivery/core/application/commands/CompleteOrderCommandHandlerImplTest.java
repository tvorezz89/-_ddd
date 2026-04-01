package microarch.delivery.core.application.commands;

import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Volume;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.ports.CourierRepository;
import microarch.delivery.core.ports.OrderRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CompleteOrderCommandHandlerImplTest {

    private final CourierRepository courierRepository = mock(CourierRepository.class);
    private final OrderRepository orderRepository = mock(OrderRepository.class);
    private final CompleteOrderCommandHandlerImpl handler = new CompleteOrderCommandHandlerImpl(courierRepository,
            orderRepository);

    private static final Location LOCATION = Location.create(5, 5).getValue();

    @Test
    void handle_shouldCompleteOrderSuccessfully() {
        var order = Order.create(UUID.randomUUID(), LOCATION, Volume.create(5).getValue()).getValue();
        var courier = Courier.create("Ivan", LOCATION).getValue();
        courier.takeOrder(order);

        when(courierRepository.findById(courier.getId())).thenReturn(Optional.of(courier));
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        var command = CompleteOrderCommand.create(courier.getId(), order.getId()).getValue();
        var result = handler.handle(command);

        assertThat(result.isSuccess()).isTrue();
        verify(courierRepository).save(courier);
        verify(orderRepository).save(order);
    }

    @Test
    void handle_shouldFail_whenCourierNotFound() {
        var courierId = UUID.randomUUID();
        var orderId = UUID.randomUUID();
        when(courierRepository.findById(courierId)).thenReturn(Optional.empty());

        var command = CompleteOrderCommand.create(courierId, orderId).getValue();
        var result = handler.handle(command);

        assertThat(result.isFailure()).isTrue();
    }

    @Test
    void handle_shouldFail_whenOrderNotFound() {
        var courier = Courier.create("Ivan", LOCATION).getValue();
        var orderId = UUID.randomUUID();
        when(courierRepository.findById(courier.getId())).thenReturn(Optional.of(courier));
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        var command = CompleteOrderCommand.create(courier.getId(), orderId).getValue();
        var result = handler.handle(command);

        assertThat(result.isFailure()).isTrue();
    }

    @Test
    void handle_shouldFail_whenCourierHasNoSuchAssignment() {
        var order = Order.create(UUID.randomUUID(), LOCATION, Volume.create(5).getValue()).getValue();
        var courier = Courier.create("Ivan", LOCATION).getValue();

        when(courierRepository.findById(courier.getId())).thenReturn(Optional.of(courier));
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        var command = CompleteOrderCommand.create(courier.getId(), order.getId()).getValue();
        var result = handler.handle(command);

        assertThat(result.isFailure()).isTrue();
    }
}
