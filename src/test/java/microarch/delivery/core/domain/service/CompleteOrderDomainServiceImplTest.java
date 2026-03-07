package microarch.delivery.core.domain.service;

import libs.errs.Error;
import libs.errs.UnitResult;
import microarch.delivery.core.domain.Location;
import microarch.delivery.core.domain.Speed;
import microarch.delivery.core.domain.Volume;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Order;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CompleteOrderDomainServiceImplTest {

    private final CompleteOrderDomainService service = new CompleteOrderDomainServiceImpl();


    @DisplayName("Завершение заказа: успех")
    @Test
    void completeOrder_shouldSuccess_whenOrderIsLoaded() {
        Courier courier = Courier.mustCreate("Ваня", Speed.mustCreate(1), Location.mustCreate(1, 1));
        Order order = Order.mustCreate(UUID.randomUUID(), Location.mustCreate(2, 2), Volume.mustCreate(2));

        order.assignToCourier(courier.getId());
        courier.takeOrder(order);

        UnitResult<Error> result = service.completeOrder(order, courier);

        assertTrue(result.isSuccess());
        assertNotNull(order.getCourierId());
        assertTrue(order.isCompleted());
    }

    @DisplayName("Завершение заказа: неудача, если заказ не найден")
    @Test
    void completeOrder_shouldFail_whenOrderNotFound() {
        Courier courier = Courier.mustCreate("Ваня", Speed.mustCreate(1), Location.mustCreate(1, 1));
        Order order = Order.mustCreate(UUID.randomUUID(), Location.mustCreate(2, 2), Volume.mustCreate(2));

        UnitResult<Error> result = service.completeOrder(order, courier);

        assertTrue(result.isFailure());
        assertEquals(Courier.Errors.orderNotFoundOnCourier(), result.getError());
        assertNull(order.getCourierId());
    }
}
