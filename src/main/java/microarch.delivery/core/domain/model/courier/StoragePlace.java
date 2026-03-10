package microarch.delivery.core.domain.model.courier;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import libs.ddd.BaseEntity;
import libs.errs.*;
import libs.errs.Error;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import microarch.delivery.core.domain.kernel.Volume;

import java.util.UUID;

@Entity
@Table(name = "storage_places")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoragePlace extends BaseEntity<UUID> {

    @Column(name = "name")
    private String name;

    @Embedded
    private Volume totalVolume;

    @Column(name = "order_id")
    private UUID orderId;

    private StoragePlace(String name, Volume totalVolume) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.totalVolume = totalVolume;
        this.orderId = null;
    }

    public static Result<StoragePlace, Error> create(String name, Volume volume) {
        var validatedName = Guard.againstNullOrEmpty(name, "name");
        if (validatedName != null) {
            return Result.failure(Error.of(validatedName.getCode(), validatedName.getMessage()));
        }

        return Result.success(new StoragePlace(name, volume));
    }

    public static StoragePlace getCreated(String name, Volume volume) {
        return create(name, volume).getValue();
    }

    public Result<Boolean, Error> canStore(Volume volume) {
        if (isOccupied() || volume.getVolume() > getTotalVolume().getVolume()) {
            return Result.success(false);
        }

        return Result.success(true);
    }

    public UnitResult<Error> store(UUID orderId, Volume volume) {
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
