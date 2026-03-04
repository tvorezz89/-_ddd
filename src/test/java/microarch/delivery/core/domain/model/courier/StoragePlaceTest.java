package microarch.delivery.core.domain.model.courier;

import libs.errs.Error;
import libs.errs.Result;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class StoragePlaceTest {

    @Test
    void create_shouldWorkCorrectly() {
        var name = "baggage";
        var totalVolume = 10;
        var result = getStorageResult(name, totalVolume);

        assertThat(result.getValue().getName()).isEqualTo(name);
        assertThat(result.getValue().getTotalVolume()).isEqualTo(totalVolume);
        assertThat(result.getValue().getOrderId()).isNull();
        assertThat(result.getValue().getId()).isNotNull();
    }

    @Test
    void create_whenValidationNameFailed_shouldReturnFailure() {
        var result = StoragePlace.create(null, 0);
        assertThat(result.isFailure()).isTrue();
    }

    @Test
    void create_whenValidationTotalVolumeFailed_shouldReturnFailure() {
        var result = StoragePlace.create("baggage", -5);
        assertThat(result.isFailure()).isTrue();
    }

    @Test
    void canStore_shouldWorkCorrectly() {
        var storage = getStorageResult().getValue();

        var currentVolume = 10;
        var canStoreResult = storage.canStore(currentVolume);

        assertThat(canStoreResult.isSuccess()).isTrue();
        assertThat(canStoreResult.getValue()).isTrue();
    }

    @Test
    void canStore_whenInvalidVolume_shouldReturnFalse() {
        var storage = getStorageResult().getValue();

        var currentVolume = 20;
        var canStoreResult = storage.canStore(currentVolume);

        assertThat(canStoreResult.isSuccess()).isTrue();
        assertThat(canStoreResult.getValue()).isFalse();
    }

    @Test
    void store_shouldWorkCorrectly() {
        var storage = getStorageResult().getValue();

        var orderId = UUID.randomUUID();
        var currentVolume = 10;

        var result = storage.store(orderId, currentVolume);

        assertThat(result.isSuccess()).isTrue();
        assertThat(storage.getOrderId()).isNotNull();
    }

    @Test
    void store_whenOrderAlreadyStored_shouldReturnFailure() {
        var storage = getStorageResult().getValue();

        var orderId = UUID.randomUUID();
        var currentVolume = 10;

        storage.store(orderId, currentVolume);

        var anotherOrder = UUID.randomUUID();
        var anotherOrderVolume = 5;

        var result = storage.store(anotherOrder, anotherOrderVolume);

        assertThat(result.isFailure()).isTrue();
    }

    @Test
    void clear_shouldWorkCorrectly() {
        var storage = getStorageResult().getValue();

        var orderId = UUID.randomUUID();
        var currentVolume = 10;

        storage.store(orderId, currentVolume);
        var result = storage.clear(orderId);
        assertThat(result.isSuccess()).isTrue();
        assertThat(storage.getOrderId()).isNull();
    }

    @Test
    void equals_shouldWorkProperly() {
        var storage1 = getStorageResult().getValue();
        var storage2 = getStorageResult().getValue();

        assertThat(storage1.equals(storage2)).isFalse();
    }

    private Result<StoragePlace, Error> getStorageResult() {
        return getStorageResult("baggage", 10);
    }

    private Result<StoragePlace, Error> getStorageResult(String name, int totalVolume) {
        var createResult = StoragePlace.create(name, totalVolume);
        assertThat(createResult.isSuccess()).isTrue();
        return createResult;
    }
}
