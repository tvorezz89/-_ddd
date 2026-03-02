package microarch.delivery.core.domain.model.courier;

import libs.errs.Result;
import libs.errs.Error;
import libs.errs.UnitResult;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class StoragePlaceTest {

    @Test
    void create_success() {
        Result<StoragePlace, Error> res = StoragePlace.create("A-1", 10);
        assertTrue(res.isSuccess());
        assertNotNull(res.getValue());
    }

    @Test
    void create_create_unique_entities() {
        Result<StoragePlace, Error> res1 = StoragePlace.create("A-1", 10);
        Result<StoragePlace, Error> res2 = StoragePlace.create("A-1", 10);
        assertNotEquals(res1.getValue(), res2.getValue());
    }

    @Test
    void create_fail_name_required() {
        Result<StoragePlace, Error> res = StoragePlace.create(null, 10);
        assertTrue(res.isFailure());
        assertEquals("value.is.required", res.getError().getCode());
        assertTrue(res.getError().getMessage().contains("name"));
    }

    @Test
    void create_fail_totalVolume_required() {
        Result<StoragePlace, Error> res = StoragePlace.create("A-1", null);
        assertTrue(res.isFailure());
        assertEquals("value.is.required", res.getError().getCode());
        assertTrue(res.getError().getMessage().contains("totalVolume"));
    }

    @Test
    void create_fail_totalVolume_must_be_greater_than_zero() {
        Result<StoragePlace, Error> res = StoragePlace.create("A-1", 0);
        assertTrue(res.isFailure());
        assertEquals("value.must.be.greater.than", res.getError().getCode());
    }

    @Test
    void canStore_fail_when_volume_exceeds() {
        StoragePlace sp = StoragePlace.create("A-1", 5).getValue();

        Result<Boolean, Error> res = sp.canStore(6);
        assertTrue(res.isFailure());
        assertEquals("volume.should.be.less.than.totalVolume", res.getError().getCode());
    }

    @Test
    void store_success() {
        StoragePlace sp = StoragePlace.create("A-1", 5).getValue();

        UnitResult<Error> res = sp.store(UUID.randomUUID(), 5);
        assertTrue(res.isSuccess());
    }

    @Test
    void store_fail_when_volume_exceeds() {
        StoragePlace sp = StoragePlace.create("A-1", 5).getValue();

        UnitResult<Error> res = sp.store(UUID.randomUUID(), 6);
        assertTrue(res.isFailure());
        assertEquals("volume.should.be.less.than.totalVolume", res.getError().getCode());
    }

    @Test
    void canStore_fail_when_occupied() {
        StoragePlace sp = StoragePlace.create("A-1", 5).getValue();
        sp.store(UUID.randomUUID(), 5);

        Result<Boolean, Error> res = sp.canStore(1);
        assertTrue(res.isFailure());
        assertEquals("storage.place.is.not.empty", res.getError().getCode());
    }
}