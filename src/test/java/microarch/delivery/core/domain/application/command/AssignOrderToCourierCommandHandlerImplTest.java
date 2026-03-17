package microarch.delivery.core.domain.application.command;

import libs.errs.Result;
import microarch.delivery.core.application.command.AssignOrderToCourierCommand;
import microarch.delivery.core.application.command.AssignOrderToCourierCommandHandlerImpl;
import microarch.delivery.core.domain.kernel.Location;
import microarch.delivery.core.domain.kernel.Speed;
import microarch.delivery.core.domain.kernel.Volume;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.service.OrderManageService;
import microarch.delivery.core.ports.CourierRepository;
import microarch.delivery.core.ports.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AssignOrderToCourierCommandHandlerImplTest {

    private final OrderRepository orderRepository = Mockito.mock(OrderRepository.class);
    private final CourierRepository courierRepository = Mockito.mock(CourierRepository.class);
    private final OrderManageService orderManageService = Mockito.mock(OrderManageService.class);
    private AssignOrderToCourierCommandHandlerImpl handler;

    @BeforeEach
    void init() {
        handler = new AssignOrderToCourierCommandHandlerImpl(orderRepository, courierRepository, orderManageService);
    }

    @Test
    void handle_shouldWorkProperly() {
        var command = AssignOrderToCourierCommand.create().getValue();

        var order = createOrder();
        var courier = createCourier();
        var couriers = List.of(courier);
        when(courierRepository.findAllFreeCouriers()).thenReturn(couriers);
        when(orderRepository.findAnyByStatusCreated()).thenReturn(Optional.of(order));
        when(orderManageService.manage(order, couriers)).thenReturn(Result.success(courier));

        var result = handler.handle(command);

        assertThat(result.isSuccess()).isTrue();

        verify(courierRepository).findAllFreeCouriers();
        verify(orderRepository).findAnyByStatusCreated();
        verify(orderManageService).manage(order, couriers);
        verify(orderRepository).update(order);
        verify(courierRepository).update(courier);
    }

    private Courier createCourier() {
        var name = "David";
        var location = Location.create(5, 5).getValue();
        var speed = Speed.create(10).getValue();
        return Courier.create(name, speed, location).getValue();
    }

    private Order createOrder() {
        var id = UUID.randomUUID();
        var location = Location.create(5, 5).getValue();
        var volume = Volume.getCreated(10);
        return Order.create(id, location, volume).getValue();
    }
}
