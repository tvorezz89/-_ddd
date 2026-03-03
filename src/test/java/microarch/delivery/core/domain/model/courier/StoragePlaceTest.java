package microarch.delivery.core.domain.model.courier;

import static org.junit.jupiter.api.Assertions.*;

import libs.errs.DomainInvariantException;
import libs.errs.Error;
import libs.errs.Result;
import microarch.delivery.core.domain.Volume;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;


class StoragePlaceTest {

    @Test
    @DisplayName("Создание места хранения с валидными данными")
    void createWithValidDataSuccess() {
        Result<StoragePlace, Error> result = StoragePlace.create("Рюкзак", Volume.mustCreate(50));

        assertTrue(result.isSuccess());
        StoragePlace place = result.getValue();
        assertEquals("Рюкзак", place.getName());
        assertEquals(50, place.getTotalVolume().getValue());
        assertTrue(place.isEmpty());
        assertNotNull(place.getId());
    }

    @Test
    @DisplayName("Ошибка создания: пустое имя")
    void createWithEmptyNameFailure() {
        Result<StoragePlace, Error> result = StoragePlace.create("  ", Volume.mustCreate(50));

        assertTrue(result.isFailure());
    }

    @Test
    @DisplayName("Ошибка создания: null имя")
    void createWithNullNameFailure() {
        assertThrows(NullPointerException.class, () -> StoragePlace.create(null, Volume.mustCreate(50)));
    }

    @Test
    @DisplayName("Ошибка создания: объем равен 0")
    void createWithZeroVolumeFailure() {
        assertThrows(DomainInvariantException.class, () -> StoragePlace.create("Рюкзак", Volume.mustCreate(0)));
    }

    @Test
    @DisplayName("Ошибка создания: отрицательный объем")
    void createWithNegativeVolumeFailure() {
        assertThrows(DomainInvariantException.class, () -> StoragePlace.create("Рюкзак", Volume.mustCreate(-10)));
    }

    @Test
    @DisplayName("canPutOrder: возвращает true для пустого места с подходящим объемом")
    void canPutOrderEmptyPlaceAndValidVolumeReturnsTrue() {
        StoragePlace place = StoragePlace.create("Багажник", Volume.mustCreate(100)).getValue();

        assertTrue(place.canPutOrder(Volume.mustCreate(50)));
    }

    @Test
    @DisplayName("canPutOrder: возвращает false, если объем больше вместимости")
    void canPutOrderVolumeExceedsCapacityReturnsFalse() {
        StoragePlace place = StoragePlace.create("Багажник", Volume.mustCreate(50)).getValue();

        assertFalse(place.canPutOrder(Volume.mustCreate(51)));
        assertFalse(place.canPutOrder(Volume.mustCreate(100)));
    }

    @Test
    @DisplayName("canPutOrder: возвращает false, если объем <= 0")
    void canPutOrderInvalidVolumeReturnsFalse() {
        StoragePlace place = StoragePlace.create("Багажник", Volume.mustCreate(50)).getValue();

        assertThrows(DomainInvariantException.class, () -> place.canPutOrder(Volume.mustCreate(0)));
        assertThrows(DomainInvariantException.class, () -> place.canPutOrder(Volume.mustCreate(-5)));
    }


    @Test
    @DisplayName("canPutOrder: возвращает false, если объем <= 0")
    void canPutOrderInvalidVolumeReturnsNullPointer() {
        StoragePlace place = StoragePlace.create("Багажник", Volume.mustCreate(50)).getValue();
        assertThrows(NullPointerException.class, () -> place.canPutOrder(null));
    }

    @Test
    @DisplayName("canPutOrder: возвращает false, если место уже занято")
    void canPutOrderOccupiedPlaceReturnsFalse() {
        StoragePlace place = StoragePlace.create("Рюкзак", Volume.mustCreate(100)).getValue();
        UUID orderId = UUID.randomUUID();
        var result = place.putOrder(orderId, Volume.mustCreate(50));

        assertTrue(result.isSuccess());
        assertFalse(place.canPutOrder(Volume.mustCreate(10)));
    }

    @Test
    @DisplayName("putOrder: успешное размещение заказа")
    void putOrderSuccess() {
        StoragePlace place = StoragePlace.create("Сумка", Volume.mustCreate(60)).getValue();
        UUID orderId = UUID.randomUUID();

        var result = place.putOrder(orderId, Volume.mustCreate(40));

        assertTrue(result.isSuccess());
        assertFalse(place.isEmpty());
        assertEquals(orderId, place.getOrderId());
    }

    @Test
    @DisplayName("putOrder: ошибка, если место уже занято")
    void putOrderPlaceAlreadyOccupiedFailure() {
        StoragePlace place = StoragePlace.create("Рюкзак", Volume.mustCreate(100)).getValue();
        UUID firstOrderId = UUID.randomUUID();
        place.putOrder(firstOrderId, Volume.mustCreate(50));

        UUID secondOrderId = UUID.randomUUID();
        var result = place.putOrder(secondOrderId, Volume.mustCreate(20));

        assertFalse(result.isSuccess());
        assertEquals(firstOrderId, place.getOrderId());
    }

    @Test
    @DisplayName("putOrder: ошибка, если объем заказа превышает вместимость")
    void putOrderVolumeExceedsCapacityFailure() {
        StoragePlace place = StoragePlace.create("Багажник", Volume.mustCreate(50)).getValue();
        UUID orderId = UUID.randomUUID();

        var result = place.putOrder(orderId, Volume.mustCreate(60));

        assertFalse(result.isSuccess());
        assertTrue(place.isEmpty());
    }

    @Test
    @DisplayName("putOrder: ошибка, если объем заказа <= 0")
    void putOrderInvalidVolumeFailure() {
        StoragePlace place = StoragePlace.create("Рюкзак", Volume.mustCreate(50)).getValue();
        UUID orderId = UUID.randomUUID();


        assertThrows(DomainInvariantException.class, () -> place.putOrder(orderId, Volume.mustCreate(0)));
        assertThrows(DomainInvariantException.class, () -> place.putOrder(orderId, Volume.mustCreate(-10)));
    }

    @Test
    @DisplayName("putOrder: ошибка, если orderId null")
    void putOrderNullOrderIdFailure() {
        StoragePlace place = StoragePlace.create("Рюкзак", Volume.mustCreate(50)).getValue();

        assertThrows(NullPointerException.class, () -> place.putOrder(null, Volume.mustCreate(20)));
    }

    @Test
    @DisplayName("extractOrder: успешное извлечение заказа")
    void extractOrderSuccess() {
        StoragePlace place = StoragePlace.create("Сумка", Volume.mustCreate(60)).getValue();
        UUID orderId = UUID.randomUUID();
        place.putOrder(orderId, Volume.mustCreate(40));

        Result<UUID, Error> result = place.extractOrder();

        assertTrue(result.isSuccess());
        assertEquals(orderId, result.getValue());
        assertTrue(place.isEmpty());
        assertNull(place.getOrderId());
    }

    @Test
    @DisplayName("extractOrder: ошибка, если место уже пустое")
    void extractOrderEmptyPlaceFailure() {
        StoragePlace place = StoragePlace.create("Рюкзак", Volume.mustCreate(50)).getValue();

        Result<UUID, Error> result = place.extractOrder();

        assertFalse(result.isSuccess());
        assertTrue(place.isEmpty());
    }

    @Test
    @DisplayName("проверка эквилалентности на разные объекты")
    void objectsDoesNotEquals() {
        StoragePlace place1 = StoragePlace.create("Рюкзак", Volume.mustCreate(50)).getValue();
        StoragePlace place2 = StoragePlace.create("Рюкзак", Volume.mustCreate(50)).getValue();

        assertNotEquals(place1, place2);
    }

    @Test
    @DisplayName("проверка эквилалентности на одинаковые объекты")
    void objectToItSelfEquals() {
        StoragePlace place1 = StoragePlace.create("Рюкзак", Volume.mustCreate(50)).getValue();

        assertEquals(place1, place1);
    }
}