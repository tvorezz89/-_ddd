package microarch.delivery.core.domain.model.courier;

import static org.junit.jupiter.api.Assertions.*;

import libs.errs.Error;
import libs.errs.Result;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;


class StoragePlaceTest {

    @Test
    @DisplayName("Создание места хранения с валидными данными")
    void create_withValidData_success() {
        Result<StoragePlace, Error> result = StoragePlace.create("Рюкзак", 50);

        assertTrue(result.isSuccess());
        StoragePlace place = result.getValue();
        assertEquals("Рюкзак", place.getName());
        assertEquals(50, place.getTotalVolume());
        assertTrue(place.isEmpty());
        assertNotNull(place.getId());
    }

    @Test
    @DisplayName("Ошибка создания: пустое имя")
    void create_withEmptyName_failure() {
        Result<StoragePlace, Error> result = StoragePlace.create("  ", 50);

        assertTrue(result.isFailure());
    }

    @Test
    @DisplayName("Ошибка создания: null имя")
    void create_withNullName_failure() {
        Result<StoragePlace, Error> result = StoragePlace.create(null, 50);

        assertTrue(result.isFailure());
    }

    @Test
    @DisplayName("Ошибка создания: объем равен 0")
    void create_withZeroVolume_failure() {
        Result<StoragePlace, Error> result = StoragePlace.create("Рюкзак", 0);

        assertTrue(result.isFailure());
    }

    @Test
    @DisplayName("Ошибка создания: отрицательный объем")
    void create_withNegativeVolume_failure() {
        Result<StoragePlace, Error> result = StoragePlace.create("Рюкзак", -10);

        assertTrue(result.isFailure());
    }

    @Test
    @DisplayName("canPutOrder: возвращает true для пустого места с подходящим объемом")
    void canPutOrder_emptyPlaceAndValidVolume_returnsTrue() {
        StoragePlace place = StoragePlace.create("Багажник", 100).getValue();

        assertTrue(place.canPutOrder(50));
    }

    @Test
    @DisplayName("canPutOrder: возвращает false, если объем больше вместимости")
    void canPutOrder_volumeExceedsCapacity_returnsFalse() {
        StoragePlace place = StoragePlace.create("Багажник", 50).getValue();

        assertFalse(place.canPutOrder(51));
        assertFalse(place.canPutOrder(100));
    }

    @Test
    @DisplayName("canPutOrder: возвращает false, если объем <= 0")
    void canPutOrder_invalidVolume_returnsFalse() {
        StoragePlace place = StoragePlace.create("Багажник", 50).getValue();

        assertFalse(place.canPutOrder(0));
        assertFalse(place.canPutOrder(-5));
    }

    @Test
    @DisplayName("canPutOrder: возвращает false, если место уже занято")
    void canPutOrder_occupiedPlace_returnsFalse() {
        StoragePlace place = StoragePlace.create("Рюкзак", 100).getValue();
        UUID orderId = UUID.randomUUID();
        var result = place.putOrder(orderId, 50);

        assertTrue(result.isSuccess());
        assertFalse(place.canPutOrder(10));
    }

    @Test
    @DisplayName("putOrder: успешное размещение заказа")
    void putOrder_success() {
        StoragePlace place = StoragePlace.create("Сумка", 60).getValue();
        UUID orderId = UUID.randomUUID();
        int orderVolume = 40;

        var result = place.putOrder(orderId, orderVolume);

        assertTrue(result.isSuccess());
        assertFalse(place.isEmpty());
        assertEquals(orderId, place.getOrderId());
    }

    @Test
    @DisplayName("putOrder: ошибка, если место уже занято")
    void putOrder_placeAlreadyOccupied_failure() {
        StoragePlace place = StoragePlace.create("Рюкзак", 100).getValue();
        UUID firstOrderId = UUID.randomUUID();
        place.putOrder(firstOrderId, 50);

        UUID secondOrderId = UUID.randomUUID();
        var result = place.putOrder(secondOrderId, 20);

        assertFalse(result.isSuccess());
        assertEquals(firstOrderId, place.getOrderId());
    }

    @Test
    @DisplayName("putOrder: ошибка, если объем заказа превышает вместимость")
    void putOrder_volumeExceedsCapacity_failure() {
        StoragePlace place = StoragePlace.create("Багажник", 50).getValue();
        UUID orderId = UUID.randomUUID();

        var result = place.putOrder(orderId, 60);

        assertFalse(result.isSuccess());
        assertTrue(place.isEmpty());
    }

    @Test
    @DisplayName("putOrder: ошибка, если объем заказа <= 0")
    void putOrder_invalidVolume_failure() {
        StoragePlace place = StoragePlace.create("Рюкзак", 50).getValue();
        UUID orderId = UUID.randomUUID();

        assertTrue(place.putOrder(orderId, 0).isFailure());
        assertTrue(place.putOrder(orderId, -10).isFailure());
    }

    @Test
    @DisplayName("putOrder: ошибка, если orderId null")
    void putOrder_nullOrderId_failure() {
        StoragePlace place = StoragePlace.create("Рюкзак", 50).getValue();

        assertThrows(NullPointerException.class, () -> place.putOrder(null, 20));
    }

    @Test
    @DisplayName("extractOrder: успешное извлечение заказа")
    void extractOrder_success() {
        StoragePlace place = StoragePlace.create("Сумка", 60).getValue();
        UUID orderId = UUID.randomUUID();
        place.putOrder(orderId, 40);

        Result<UUID, Error> result = place.extractOrder();

        assertTrue(result.isSuccess());
        assertEquals(orderId, result.getValue());
        assertTrue(place.isEmpty());
        assertNull(place.getOrderId());
    }

    @Test
    @DisplayName("extractOrder: ошибка, если место уже пустое")
    void extractOrder_emptyPlace_failure() {
        StoragePlace place = StoragePlace.create("Рюкзак", 50).getValue();

        Result<UUID, Error> result = place.extractOrder();

        assertFalse(result.isSuccess());
        assertTrue(place.isEmpty());
    }

    @Test
    @DisplayName("проверка на разные объекты")
    void objects_does_not_equals() {
        StoragePlace place1 = StoragePlace.create("Рюкзак", 50).getValue();
        StoragePlace place2 = StoragePlace.create("Рюкзак", 50).getValue();

        assertNotEquals(place1, place2);
    }
}