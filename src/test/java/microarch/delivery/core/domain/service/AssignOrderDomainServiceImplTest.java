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

class AssignOrderDomainServiceImplTest {

    private final AssignOrderDomainService service = new AssignOrderDomainServiceImpl();


    @DisplayName("Прием заказа: успех")
    @Test
    void takeOrder_shouldSuccess_whenOrderIsValidAndPlaceExists() {
        Courier courier = Courier.mustCreate("Ваня", Speed.mustCreate(1), Location.mustCreate(1, 1));
        Order order = Order.mustCreate(UUID.randomUUID(), Location.mustCreate(2, 2), Volume.mustCreate(2));

        UnitResult<Error> result = service.assignOrder(order, courier);

        assertTrue(result.isSuccess());
        assertEquals(courier.getId(), order.getCourierId());
    }

    @DisplayName("Прием заказа: неудача, если нет подходящего места")
    @Test
    void takeOrder_shouldFail_whenNoStoragePlace() {
        Courier courier = Courier.mustCreate("Ваня", Speed.mustCreate(1), Location.mustCreate(1, 1));
        Order order = Order.mustCreate(UUID.randomUUID(), Location.mustCreate(2, 2), Volume.mustCreate(11));

        UnitResult<Error> result = service.assignOrder(order, courier);

        assertTrue(result.isFailure());
        assertEquals(Courier.Errors.couldNotTakeOrder(), result.getError());
        assertNull(order.getCourierId());
    }
}
