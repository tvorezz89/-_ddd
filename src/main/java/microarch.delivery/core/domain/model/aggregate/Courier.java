package microarch.delivery.core.domain.model.aggregate;

import libs.ddd.Aggregate;
import libs.errs.Error;
import libs.errs.GeneralErrors;
import libs.errs.Result;
import libs.errs.UnitResult;
import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.courier.StoragePlace;
import microarch.delivery.core.domain.model.courier.Volume;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Courier extends Aggregate<UUID> {

    private String name;
    private Integer speed;
    private Location location;
    private List<StoragePlace> storagePlaces;

    private Courier() {
    }

    private Courier(String name, int speed, Location location) {
        this.name = name;
        this.speed = speed;
        this.location = location;
        this.storagePlaces = List.of(StoragePlace.create("Сумка", Volume.create(10).getValue()).getValue());
    }

    private Result<Courier, Error> create(String name, Integer speed, Location location) {
        if (name == null || name.isEmpty())
            return Result.failure(GeneralErrors.valueIsRequired("name"));
        if (speed == null || speed == 0)
            return Result.failure(GeneralErrors.valueIsRequired("speed"));
        if (location == null)
            return Result.failure(GeneralErrors.valueIsRequired("location"));

        return Result.success(new Courier(name, speed, location));
    }

    private Result<Void, Error> addStoragePlace(String name, Integer vol) {
        Result<Volume, Error> volume = Volume.create(vol);
        if (volume.isSuccess()) {
            Result<StoragePlace, Error> storagePlace = StoragePlace.create(name, volume.getValue());
            if (storagePlace.isSuccess())
                storagePlaces.add(storagePlace.getValue());
            else
                return Result.failure(storagePlace.getError());
        } else {
            return Result.failure(volume.getError());
        }
        return Result.success();
    }

    private Boolean canTakeOrder(Volume volume) {
        for (StoragePlace sp : storagePlaces) {
            if (sp.canStore(volume.getVolume()).isSuccess()) {
                return true;
            }
        }
        return false;
    }

    private UnitResult<Error> takeOrder(Order order) {
        for (StoragePlace sp : storagePlaces) {
            if (canTakeOrder(order.getVolume())) {
                sp.store(order.getId(), order.getVolume().getVolume());
                return UnitResult.success();
            }
        }
        return UnitResult.failure(Errors.noAvailableStorage());
    }

    private Result<Void, Error> completeOrder(UUID orderId) {
        for (StoragePlace sp : storagePlaces) {
            if (sp.getOrderId().equals(orderId)) {
                return sp.reset();
            }
        }
        return Result.failure(Errors.orderNotFound());
    }

    private static class Errors {
        public static Error noAvailableStorage() {
            return Error.of(Courier.class.getSimpleName().toLowerCase() + "no.available.storage",
                    "Нет доступных мест для хранения заказа");
        }

        public static Error orderNotFound() {
            return Error.of(Courier.class.getSimpleName().toLowerCase() + "order.not.found", "Заказ не найден");
        }
    }

    private Integer howFarFromTarget(Location target) {
        Integer dist = location.distanceTo(target).getValue();
        return dist / speed + dist % speed == 0 ? 0 : 1;
    }

    public void move(Location target) {
        Objects.requireNonNull(target, "target");

        int difX = target.getX() - location.getX();
        int difY = target.getY() - location.getY();
        int cruisingRange = speed;

        int moveX = Math.max(-cruisingRange, Math.min(cruisingRange, difX));
        cruisingRange -= Math.abs(moveX);
        int moveY = Math.max(-cruisingRange, Math.min(cruisingRange, difY));

        this.location = Location.create(location.getX() + moveX, location.getY() + moveY).getValueOrThrow();
    }

    @Override
    public UUID getId() {
        return id;
    }
}
