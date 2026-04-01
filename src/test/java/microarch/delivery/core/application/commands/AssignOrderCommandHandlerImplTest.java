package microarch.delivery.core.application.commands;

import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Volume;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.services.OrderDispatchDomainServiceImpl;
import microarch.delivery.core.ports.CourierRepository;
import microarch.delivery.core.ports.OrderRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AssignOrderCommandHandlerImplTest {

    private final OrderRepository orderRepository = mock(OrderRepository.class);
    private final CourierRepository courierRepository = mock(CourierRepository.class);
    private final OrderDispatchDomainServiceImpl dispatchService = new OrderDispatchDomainServiceImpl();
    private final AssignOrderCommandHandlerImpl handler = new AssignOrderCommandHandlerImpl(orderRepository,
            courierRepository, dispatchService);

    private static final Location LOCATION = Location.create(5, 5).getValue();

    @Test
    void handle_shouldAssignOrderToCourier() {
        var order = Order.create(UUID.randomUUID(), LOCATION, Volume.create(5).getValue()).getValue();
        var courier = Courier.create("Ivan", LOCATION).getValue();

        when(orderRepository.getNewOrder()).thenReturn(Optional.of(order));
        when(courierRepository.findAll()).thenReturn(List.of(courier));

        var command = AssignOrderCommand.create().getValue();
        var result = handler.handle(command);

        assertThat(result.isSuccess()).isTrue();
        verify(courierRepository).save(any());
        verify(orderRepository).save(order);
    }

    @Test
    void handle_shouldFail_whenNoNewOrders() {
        when(orderRepository.getNewOrder()).thenReturn(Optional.empty());

        var command = AssignOrderCommand.create().getValue();
        var result = handler.handle(command);

        assertThat(result.isFailure()).isTrue();
    }

    @Test
    void handle_shouldFail_whenNoCouriersAvailable() {
        var order = Order.create(UUID.randomUUID(), LOCATION, Volume.create(5).getValue()).getValue();
        when(orderRepository.getNewOrder()).thenReturn(Optional.of(order));
        when(courierRepository.findAll()).thenReturn(List.of());

        var command = AssignOrderCommand.create().getValue();
        var result = handler.handle(command);

        assertThat(result.isFailure()).isTrue();
    }
}
