package microarch.delivery.core.application.command;

import libs.errs.Error;
import libs.errs.UnitResult;
import microarch.delivery.core.domain.Location;
import microarch.delivery.core.domain.Speed;
import microarch.delivery.core.domain.Volume;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.service.CompleteOrderDomainService;
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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MoveCouriersCommandHandlerImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CourierRepository courierRepository;

    @Mock
    private CompleteOrderDomainService completeOrderDomainService;

    @InjectMocks
    private MoveCouriersCommandHandlerImpl handler;

    private Order assignedOrder;
    private Courier courier;

    @BeforeEach
    void setUp() {
        UUID orderId = UUID.randomUUID();
        UUID courierId = UUID.randomUUID();
        
        Location orderLocation = Location.mustCreate(5, 5);
        Volume volume = Volume.mustCreate(5);
        
        assignedOrder = Order.mustCreate(orderId, orderLocation, volume);
        assignedOrder.assignToCourier(courierId);
        
        Location courierLocation = Location.mustCreate(4, 4);
        courier = Courier.mustCreate("Ivan", Speed.mustCreate(1), courierLocation);
    }

    @DisplayName("Перемещение курьеров: успех - нет назначенных заказов")
    @Test
    void handle_shouldSuccess_whenNoAssignedOrders() {
        when(orderRepository.findAllAssigned()).thenReturn(Collections.emptyList());

        UnitResult<Error> result = handler.handle();

        assertTrue(result.isSuccess());
        verify(orderRepository, never()).findById(any());
    }

    @DisplayName("Перемещение курьеров: успех - курьер найден и перемещен")
    @Test
    void handle_shouldSuccess_whenCourierFoundAndMoved() {
        when(orderRepository.findAllAssigned()).thenReturn(List.of(assignedOrder));
        when(courierRepository.findById(any())).thenReturn(Optional.of(courier));

        UnitResult<Error> result = handler.handle();

        assertTrue(result.isSuccess());
        verify(courierRepository, times(1)).update(courier);
        verify(orderRepository, times(1)).update(assignedOrder);
    }

    @DisplayName("Перемещение курьеров: успех - курьер не найден")
    @Test
    void handle_shouldSuccess_whenCourierNotFound() {
        when(orderRepository.findAllAssigned()).thenReturn(List.of(assignedOrder));
        when(courierRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> handler.handle());
    }

    @DisplayName("Перемещение курьеров: успех - курьер достиг точки и заказ завершен")
    @Test
    void handle_shouldSuccess_whenCourierReachedDestination() {
        Location sameLocation = Location.mustCreate(5, 5);
        courier = Courier.mustCreate("Ivan", Speed.mustCreate(10), sameLocation);

        when(orderRepository.findAllAssigned()).thenReturn(List.of(assignedOrder));
        when(courierRepository.findById(any())).thenReturn(Optional.of(courier));
        when(completeOrderDomainService.completeOrder(any(), any())).thenReturn(UnitResult.success());

        UnitResult<Error> result = handler.handle();

        assertTrue(result.isSuccess());
        verify(completeOrderDomainService, times(1)).completeOrder(any(), any());
    }

    @DisplayName("Перемещение курьеров: неудача - ошибка при завершении заказа")
    @Test
    void handle_shouldFail_whenCompleteOrderFails() {
        Location sameLocation = Location.mustCreate(5, 5);
        courier = Courier.mustCreate("Ivan", Speed.mustCreate(10), sameLocation);

        when(orderRepository.findAllAssigned()).thenReturn(List.of(assignedOrder));
        when(courierRepository.findById(any())).thenReturn(Optional.of(courier));
        when(completeOrderDomainService.completeOrder(any(), any()))
                .thenReturn(UnitResult.failure(Error.of("test.error", "test")));

        UnitResult<Error> result = handler.handle();

        assertTrue(result.isFailure());
    }
}
