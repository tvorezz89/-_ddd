package microarch.delivery.core.domain.model.courier;

import libs.ddd.BaseEntity;
import libs.errs.*;
import libs.errs.Error;
import lombok.Getter;
import java.util.Objects;
import java.util.UUID;


@Getter
public class StoragePlace extends BaseEntity<UUID> {

    private final String name;
    private final Integer totalVolume;
    private UUID orderId;


    private StoragePlace(String name, Integer totalVolume) {
        super(UUID.randomUUID());
        this.name = name;
        this.totalVolume = totalVolume;
    }

    public static Result<StoragePlace, Error> create(String name, Integer totalVolume) {
        if (Objects.isNull(name) || name.trim().isEmpty()) {
            return Result.failure(Errors.nameShouldBeNotEmpty());
        }
        if (Objects.isNull(totalVolume) || totalVolume <= 0) {
            return Result.failure(Errors.totalVolumeShouldBePositive());
        }
        return Result.success(new StoragePlace(name, totalVolume));
    }

    public static StoragePlace mustCreate(String name, Integer totalVolume) {
        return create(name, totalVolume).getValueOrThrow();
    }

    public boolean canPutOrder(Integer orderVolume) {
        Objects.requireNonNull(orderVolume, "orderVolume");
        return isEmpty() && orderVolume > 0 && orderVolume <= this.totalVolume;
    }

    public UnitResult<Error> putOrder(UUID orderId, Integer orderVolume) {
        Objects.requireNonNull(orderId, "orderId");
        if (!canPutOrder(orderVolume)) {
            return UnitResult.failure(Errors.storageAlreadyOccupied());
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

    public static final class Errors {

        public static Error storageAlreadyOccupied() {
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

        public static Error totalVolumeShouldBePositive() {
            return Error.of(
                    "totalVolume.should.be.positve",
                    "Вес груза должен быть положительным"
            );
        }
    }
}
