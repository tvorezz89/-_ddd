package microarch.delivery.core.domain.model.courier;

import libs.ddd.BaseEntity;
import libs.errs.*;
import libs.errs.Error;
import lombok.Getter;

import java.util.UUID;

@Getter
public class StoragePlace extends BaseEntity<UUID> {

    private String name;

    private Integer totalVolume;

    private UUID orderId;

    private StoragePlace() {
    }

    private StoragePlace(String name, Integer totalVolume) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.totalVolume = totalVolume;
        this.orderId = null;
    }

    public static Result<StoragePlace, Error> create(String name, int totalVolume) {
        var validatedName = Guard.againstNullOrEmpty(name, "name");
        if (validatedName != null) {
            return Result.failure(Error.of(validatedName.getCode(), validatedName.getMessage()));
        }

        var validatedTotalVolume = Guard.againstLessOrEqual(totalVolume, 0, "totalVolume");
        if (validatedTotalVolume != null) {
            return Result.failure(Error.of(validatedTotalVolume.getCode(), validatedTotalVolume.getMessage()));
        }

        return Result.success(new StoragePlace(name, totalVolume));
    }

    public Result<Boolean, Error> canStore(int volume) {
        if (isOccupied() || volume > getTotalVolume()) {
            return Result.success(false);
        }

        return Result.success(true);
    }

    public UnitResult<Error> store(UUID orderId, int volume) {
        if (orderId == null || !canStore(volume).getValue()) {
            return UnitResult.failure(Error.of("Unable.store",
                    "Unable to store an order. Already stored or volume is too large %s".formatted(volume)));
        }

        this.orderId = orderId;

        return UnitResult.success();
    }

    public UnitResult<Error> clear(UUID orderId) {
        if (getOrderId() == null || !getOrderId().equals(orderId)) {
            return UnitResult.failure(GeneralErrors.valueIsInvalid("orderId", orderId));
        }

        this.orderId = null;

        return UnitResult.success();
    }

    private boolean isOccupied() {
        return getOrderId() != null;
    }

}
