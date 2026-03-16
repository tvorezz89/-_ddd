package microarch.delivery.core.application.command;

import libs.errs.Error;
import libs.errs.Result;
import libs.errs.UnitResult;
import microarch.delivery.core.domain.Location;
import microarch.delivery.core.domain.Speed;
import microarch.delivery.core.domain.Volume;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.service.OrderDispatcherDomainService;
import microarch.delivery.core.ports.CourierRepository;
import microarch.delivery.core.ports.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DispatchOrdersCommandHandlerImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CourierRepository courierRepository;

    @Mock
    private OrderDispatcherDomainService orderDispatcherDomainService;

    @InjectMocks
    private DispatchOrdersCommandHandlerImpl handler;

    private Order createdOrder;
    private Courier freeCourier;

    @BeforeEach
    void setUp() {
        UUID orderId = UUID.randomUUID();
        Location orderLocation = Location.mustCreate(5, 5);
        Volume volume = Volume.mustCreate(5);
        
        createdOrder = Order.mustCreate(orderId, orderLocation, volume);
        
        Location courierLocation = Location.mustCreate(1, 1);
        freeCourier = Courier.mustCreate("Ivan", Speed.mustCreate(10), courierLocation);
    }

    @DisplayName("Диспетчеризация: неудача - нет созданных заказов")
    @Test
    void handle_shouldFail_whenNoCreatedOrders() {
        when(orderRepository.findAnyCreated()).thenReturn(Optional.empty());

        UnitResult<Error> result = handler.handle();

        assertTrue(result.isFailure());
        assertEquals("no.any.created.orders", result.getError().getCode());
    }

    @DisplayName("Диспетчеризация: неудача - нет свободных курьеров")
    @Test
    void handle_shouldFail_whenNoFreeCouriers() {
        when(orderRepository.findAnyCreated()).thenReturn(Optional.of(createdOrder));
        when(courierRepository.findAllFreeCouriers()).thenReturn(Collections.emptyList());

        UnitResult<Error> result = handler.handle();

        assertTrue(result.isFailure());
        assertEquals("no.any.free.couriers", result.getError().getCode());
    }

    @DisplayName("Диспетчеризация: успех - заказ назначен на курьера")
    @Test
    void handle_shouldSuccess_whenOrderDispatched() {
        when(orderRepository.findAnyCreated()).thenReturn(Optional.of(createdOrder));
        when(courierRepository.findAllFreeCouriers()).thenReturn(List.of(freeCourier));
        when(orderDispatcherDomainService.dispatch(any(), anyList()))
                .thenReturn(Result.success(freeCourier));

        UnitResult<Error> result = handler.handle();

        assertTrue(result.isSuccess());
        verify(courierRepository, times(1)).update(freeCourier);
        verify(orderRepository, times(1)).update(createdOrder);
    }

    @DisplayName("Диспетчеризация: неудача - диспетчер не смог подобрать курьера")
    @Test
    void handle_shouldFail_whenDispatcherFails() {
        when(orderRepository.findAnyCreated()).thenReturn(Optional.of(createdOrder));
        when(courierRepository.findAllFreeCouriers()).thenReturn(List.of(freeCourier));
        when(orderDispatcherDomainService.dispatch(any(), anyList()))
                .thenReturn(Result.failure(Error.of("test.error", "no courier")));

        UnitResult<Error> result = handler.handle();

        assertTrue(result.isFailure());
    }
}
