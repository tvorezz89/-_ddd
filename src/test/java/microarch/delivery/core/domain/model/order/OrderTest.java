package microarch.delivery.core.domain.model.order;

import libs.errs.Result;
import libs.errs.UnitResult;
import microarch.delivery.core.domain.Location;
import microarch.delivery.core.domain.Volume;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


class OrderTest {

    private final UUID orderId = UUID.randomUUID();
    private final Location location = Location.mustCreate(5, 5);
    private final Volume volume = Volume.mustCreate(5);

    @DisplayName("Создание заказа успешно при валидных данных")
    @Test
    void create_shouldSuccess_whenDataIsValid() {
        Result<Order, libs.errs.Error> result = Order.create(orderId, location, volume);

        assertTrue(result.isSuccess());
        Order order = result.getValue();
        assertEquals(OrderStatus.CREATED, order.getStatus());
        assertNull(order.getCourierId());
    }

    @DisplayName("Создание заказа неудачно, если ID null")
    @Test
    void create_shouldFail_whenIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> Order.create(null, location, volume));
    }

    @DisplayName("Создание заказа неудачно, если локация null")
    @Test
    void create_shouldFail_whenLocationIsNull() {
        assertThrows(IllegalArgumentException.class, () -> Order.create(orderId, null, volume));
    }

    @DisplayName("Создание заказа неудачно, если объем null")
    @Test
    void create_shouldFail_whenVolumeIsNull() {
        assertThrows(IllegalArgumentException.class, () -> Order.create(orderId, location, null));
    }

    @DisplayName("mustCreate возвращает объект при валидных данных")
    @Test
    void mustCreate_shouldReturnOrder_whenDataIsValid() {
        Order order = Order.mustCreate(orderId, location, volume);

        assertNotNull(order);
        assertEquals(OrderStatus.CREATED, order.getStatus());
    }

    @DisplayName("Назначение курьера успешно, если заказ в статусе CREATED")
    @Test
    void assignToCourier_shouldSuccess_whenOrderIsCreated() {
        Order order = Order.mustCreate(orderId, location, volume);
        UUID courierId = UUID.randomUUID();

        UnitResult<libs.errs.Error> result = order.assignToCourier(courierId);

        assertTrue(result.isSuccess());
        assertEquals(courierId, order.getCourierId());
        assertEquals(OrderStatus.ASSIGNED, order.getStatus());
        assertTrue(order.isAssigned());
        assertFalse(order.isCreated());
    }

    @DisplayName("Назначение курьера неудачно, если courierId null")
    @Test
    void assignToCourier_shouldFail_whenCourierIdIsNull() {
        Order order = Order.mustCreate(orderId, location, volume);
        assertThrows(NullPointerException.class, () -> order.assignToCourier(null));
    }

    @DisplayName("Назначение курьера неудачно, если заказ уже назначен")
    @Test
    void assignToCourier_shouldFail_whenOrderIsAlreadyAssigned() {
        Order order = Order.mustCreate(orderId, location, volume);
        order.assignToCourier(UUID.randomUUID());

        UnitResult<libs.errs.Error> result = order.assignToCourier(UUID.randomUUID());

        assertTrue(result.isFailure());
        assertEquals(Order.Errors.canOnlyAssignCreatedOrder(), result.getError());
    }

    @DisplayName("Назначение курьера неудачно, если заказ уже завершен")
    @Test
    void assignToCourier_shouldFail_whenOrderIsCompleted() {
        Order order = Order.mustCreate(orderId, location, volume);
        order.assignToCourier(UUID.randomUUID());
        order.complete();

        UnitResult<libs.errs.Error> result = order.assignToCourier(UUID.randomUUID());

        assertTrue(result.isFailure());
        assertEquals(Order.Errors.canOnlyAssignCreatedOrder(), result.getError());
    }

    @DisplayName("Завершение заказа успешно, если заказ назначен")
    @Test
    void complete_shouldSuccess_whenOrderIsAssigned() {
        Order order = Order.mustCreate(orderId, location, volume);
        order.assignToCourier(UUID.randomUUID());

        UnitResult<libs.errs.Error> result = order.complete();

        assertTrue(result.isSuccess());
        assertEquals(OrderStatus.COMPLETED, order.getStatus());
        assertTrue(order.isCompleted());
        assertFalse(order.isAssigned());
    }

    @DisplayName("Завершение заказа неудачно, если заказ еще не назначен")
    @Test
    void complete_shouldFail_whenOrderIsNotAssigned() {
        Order order = Order.mustCreate(orderId, location, volume);
        UnitResult<libs.errs.Error> result = order.complete();

        assertTrue(result.isFailure());
        assertEquals(Order.Errors.canOnlyCompleteAssignedOrder(), result.getError());
    }

    @DisplayName("Завершение заказа неудачно, если заказ уже завершен")
    @Test
    void complete_shouldFail_whenOrderIsAlreadyCompleted() {
        Order order = Order.mustCreate(orderId, location, volume);
        order.assignToCourier(UUID.randomUUID());
        order.complete();

        UnitResult<libs.errs.Error> result = order.complete();

        assertTrue(result.isFailure());
        assertEquals(Order.Errors.canOnlyCompleteAssignedOrder(), result.getError());
    }

    @DisplayName("Проверка статусов заказа")
    @Test
    void statusCheckers_shouldReturnCorrectBoolean() {
        Order order = Order.mustCreate(orderId, location, volume);

        assertTrue(order.isCreated());
        assertFalse(order.isAssigned());
        assertFalse(order.isCompleted());

        order.assignToCourier(UUID.randomUUID());
        assertFalse(order.isCreated());
        assertTrue(order.isAssigned());
        assertFalse(order.isCompleted());

        order.complete();
        assertFalse(order.isCreated());
        assertFalse(order.isAssigned());
        assertTrue(order.isCompleted());
    }
}