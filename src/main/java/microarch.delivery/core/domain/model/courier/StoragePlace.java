package microarch.delivery.core.domain.model.courier;

import libs.ddd.BaseEntity;
import libs.errs.GeneralErrors;
import libs.errs.Result;
import libs.errs.Error;
import libs.errs.UnitResult;

import java.util.UUID;

public class StoragePlace extends BaseEntity<UUID> {
    private String name;
    private Volume totalVolume;
    private UUID orderId;

    private StoragePlace() {
    }

    private StoragePlace(String name, Volume totalVolume) {
        super(UUID.randomUUID());
        this.name = name;
        this.totalVolume = totalVolume;
    }

    public static Result<StoragePlace, Error> create(String name, Volume totalVolume) {
        if (name == null)
            return Result.failure(GeneralErrors.valueIsRequired("name"));
        if (totalVolume == null)
            return Result.failure(GeneralErrors.valueIsRequired("totalVolume"));

        return Result.success(new StoragePlace(name, totalVolume));
    }

    public Result<Boolean, Error> canStore(int volume) {
        if (volume > this.totalVolume.getVolume())
            return Result.failure(Error.of("volume.should.be.less.than.totalVolume",
                    "Volume should be less than totalVolume of storage place"));
        if (isOccupied())
            return Result.failure(Error.of("storage.place.is.not.empty", "Storage place has an order"));

        return Result.success(true);
    }

    public UnitResult<Error> store(UUID orderId, Integer volume) {
        Result<Boolean, Error> canStore = canStore(volume);
        if (canStore.isSuccess()) {
            this.orderId = orderId;
            return UnitResult.success();
        }
        return UnitResult.failure(canStore.getError());
    }

    private Boolean isOccupied() {
        return orderId != null;
    }

    public Result<Void, Error> reset() {
        this.orderId = null;
        return Result.success();
    }

    public UUID getId() {
        return id;
    }

    public UUID getOrderId() {
        return orderId;
    }

}
