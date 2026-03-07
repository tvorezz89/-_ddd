package microarch.delivery.core.domain.model.courier;

import libs.ddd.BaseEntity;
import libs.errs.*;
import libs.errs.Error;
import lombok.Getter;
import microarch.delivery.core.domain.Volume;

import java.util.Objects;
import java.util.UUID;


@Getter
public class StoragePlace extends BaseEntity<UUID> {

    private final String name;
    private final Volume totalVolume;
    private UUID orderId;

    private StoragePlace(String name, Volume totalVolume) {
        super(UUID.randomUUID());
        this.name = name;
        this.totalVolume = totalVolume;
    }

    public static Result<StoragePlace, Error> create(String name, Volume totalVolume) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(totalVolume, "totalVolume");
        if (name.trim().isEmpty()) {
            return Result.failure(Errors.nameShouldBeNotEmpty());
        }
        return Result.success(new StoragePlace(name, totalVolume));
    }

    public static StoragePlace mustCreate(String name, Volume totalVolume) {
        return create(name, totalVolume).getValueOrThrow();
    }

    public static StoragePlace getDefault() {
        return mustCreate("Сумка", Volume.mustCreate(10));
    }

    public boolean canPutOrder(Volume orderVolume) {
        return isEmpty() && this.totalVolume.accommodates(orderVolume);
    }

    public UnitResult<Error> putOrder(UUID orderId, Volume orderVolume) {
        Objects.requireNonNull(orderId, "orderId");
        if (!canPutOrder(orderVolume)) {
            return UnitResult.failure(Errors.couldNotPutOrder());
        }
        this.orderId = orderId;
        return UnitResult.success();
    }

    public Result<UUID, Error> extractOrder() {
        if (isEmpty()) {
            return Result.failure(Errors.storageAlreadyEmpty());
        }
        var extractedOrderId = this.orderId;
        this.orderId = null;
        return Result.success(extractedOrderId);
    }

    public boolean isEmpty() {
        return Objects.isNull(orderId);
    }

    public boolean containsOrder(UUID orderId) {
        Objects.requireNonNull(orderId, "orderId");
        return !isEmpty() && this.orderId.equals(orderId);
    }

    public static final class Errors {

        public static Error couldNotPutOrder() {
            return Error.of(
                    "could.not.put.order",
                    "Невозможно поместить заказ: место хранения уже занято или некорректный вес заказа"
            );
        }

        public static Error storageAlreadyEmpty() {
            return Error.of(
                    "could.not.extract.order",
                    "Невозможно извлечь заказ: место хранения уже пусто"
            );
        }

        public static Error nameShouldBeNotEmpty() {
            return Error.of(
                    "name.should.not.be.empty",
                    "Название не может быть пустым"
            );
        }
    }
}
