package microarch.delivery.core.domain.model.courier;

import libs.errs.Result;
import libs.errs.UnitResult;
import microarch.delivery.core.domain.Location;
import microarch.delivery.core.domain.Speed;
import microarch.delivery.core.domain.Volume;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.model.order.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тесты для доменного агрегата Courier")
class CourierTest {

    private final String courierName = "Alice";
    private final Speed speed = Speed.mustCreate(2);
    private final Location startLocation = Location.mustCreate(1, 1);
    private final Volume smallVolume = Volume.mustCreate(5);
    private final Volume largeVolume = Volume.mustCreate(100);

    @DisplayName("Создание курьера успешно при валидных данных")
    @Test
    void create_shouldSuccess_whenDataIsValid() {
        Result<Courier, libs.errs.Error> result = Courier.create(courierName, speed, startLocation);

        assertTrue(result.isSuccess());
        Courier courier = result.getValue();
        assertEquals(courierName, courier.getName());
        assertEquals(speed, courier.getSpeed());
        assertEquals(startLocation, courier.getLocation());
        assertEquals(1, courier.getStoragePlaces().size());
    }

    @DisplayName("Создание курьера неудачно при null параметрах")
    @Test
    void create_shouldFail_whenAnyParamIsNull() {

        assertThrows(IllegalArgumentException.class,
                () -> Courier.create(null, speed, startLocation));

        assertThrows(IllegalArgumentException.class,
                () -> Courier.create(courierName, null, startLocation));

        assertThrows(IllegalArgumentException.class,
                () -> Courier.create(courierName, speed, null));
    }

    @DisplayName("Добавление места хранения успешно")
    @Test
    void addStoragePlace_shouldSuccess_whenVolumeIsValid() {
        Courier courier = Courier.mustCreate(courierName, speed, startLocation);
        int initialSize = courier.getStoragePlaces().size();

        UnitResult<libs.errs.Error> result = courier.addStoragePlace("Big Box", largeVolume);

        assertTrue(result.isSuccess());
        assertEquals(initialSize + 1, courier.getStoragePlaces().size());
    }

    @DisplayName("Поиск места для заказа: успех, если место есть")
    @Test
    void canTakeOrder_shouldReturnSuccess_whenPlaceExists() {
        Courier courier = Courier.mustCreate(courierName, speed, startLocation);

        Result<StoragePlace, libs.errs.Error> result = courier.canTakeOrder(smallVolume);

        assertTrue(result.isSuccess());
    }

    @DisplayName("Поиск места для заказа: неудача, если места нет")
    @Test
    void canTakeOrder_shouldReturnFailure_whenNoPlaceFits() {
        Courier courier = Courier.mustCreate(courierName, speed, startLocation);
        Result<StoragePlace, libs.errs.Error> result = courier.canTakeOrder(largeVolume);

        assertTrue(result.isFailure());
    }


    @DisplayName("Прием заказа: успех")
    @Test
    void takeOrder_shouldSuccess_whenOrderIsValidAndPlaceExists() {
        Courier courier = Courier.mustCreate(courierName, speed, startLocation);
        courier.addStoragePlace("Big Box", Volume.mustCreate(20));

        Order order = Order.mustCreate(UUID.randomUUID(), startLocation, smallVolume);

        UnitResult<libs.errs.Error> result = courier.takeOrder(order);

        assertTrue(result.isSuccess());
        assertEquals(OrderStatus.ASSIGNED, order.getStatus());
        assertEquals(courier.getId(), order.getCourierId());
    }

    @DisplayName("Прием заказа: неудача, если нет подходящего места")
    @Test
    void takeOrder_shouldFail_whenNoStoragePlace() {
        Courier courier = Courier.mustCreate(courierName, speed, startLocation);

        Order order = Order.mustCreate(UUID.randomUUID(), startLocation, largeVolume);

        UnitResult<libs.errs.Error> result = courier.takeOrder(order);

        assertTrue(result.isFailure());
        assertEquals(Courier.Errors.noSuitableStoragePlace(), result.getError());

        assertEquals(OrderStatus.CREATED, order.getStatus());
        assertNull(order.getCourierId());
    }


    @DisplayName("Завершение заказа: успех")
    @Test
    void completeOrder_shouldSuccess() {
        Courier courier = Courier.mustCreate(courierName, speed, startLocation);
        Order order = Order.mustCreate(UUID.randomUUID(), startLocation, smallVolume);
        courier.takeOrder(order);

        UnitResult<libs.errs.Error> result = courier.completeOrder(order);

        assertTrue(result.isSuccess());
    }


    @DisplayName("Завершение заказа: неудача, если заказ не найден")
    @Test
    void completeOrder_shouldFail_whenOrderNotFound() {
        Courier courier = Courier.mustCreate(courierName, speed, startLocation);
        Order order = Order.mustCreate(UUID.randomUUID(), startLocation, smallVolume);

        UnitResult<libs.errs.Error> result = courier.completeOrder(order);

        assertTrue(result.isFailure());
        assertEquals(Courier.Errors.orderNotFoundOnCourier(), result.getError());
    }

    @DisplayName("Расчет шагов до локации, Скорость 2. Локация (1,1). Цель (6,1). Дистанция 3. Шаги = ceil(5/2) = 3.")
    @Test
    void calculateStepsToLocation_shouldReturnCorrectSteps() {

        Courier courier = Courier.mustCreate(courierName, speed, startLocation);
        Location target = Location.mustCreate(6, 1);

        assertEquals(3, courier.calculateStepsToLocation(target));
    }

    @DisplayName("Перемещение: успех")
    @Test
    void move_shouldSuccess_andUpdateLocation() {
        Courier courier = Courier.mustCreate(courierName, speed, startLocation);
        Location target = Location.mustCreate(2, 1);

        UnitResult<libs.errs.Error> result = courier.move(target);

        assertTrue(result.isSuccess());
        assertEquals(target, courier.getLocation());
    }

    @DisplayName("Перемещение: частичное перемещение Старт (1,1), Скорость 2. Цель (5, 1).")
    @Test
    void move_shouldMovePartially_whenTargetIsFar() {
        Courier courier = Courier.mustCreate(courierName, speed, startLocation);
        Location farTarget = Location.mustCreate(5, 1);

        UnitResult<libs.errs.Error> result = courier.move(farTarget);

        assertTrue(result.isSuccess());
        assertEquals(Location.mustCreate(3, 1), courier.getLocation());
        assertNotEquals(farTarget, courier.getLocation());
    }

    @DisplayName("Перемещение: неудача при null целевой локации")
    @Test
    void move_shouldThrow_whenTargetIsNull() {
        Courier courier = Courier.mustCreate(courierName, speed, startLocation);

        assertThrows(NullPointerException.class, () -> courier.move(null));
    }
}