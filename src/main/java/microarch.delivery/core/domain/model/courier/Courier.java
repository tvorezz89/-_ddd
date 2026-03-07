package microarch.delivery.core.domain.model.courier;

import libs.ddd.Aggregate;
import libs.errs.*;
import libs.errs.Error;
import lombok.Getter;
import microarch.delivery.core.domain.Location;
import microarch.delivery.core.domain.Speed;
import microarch.delivery.core.domain.Volume;
import microarch.delivery.core.domain.model.order.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
public class Courier extends Aggregate<UUID> {

    private final String name;
    private final Speed speed;
    private Location location;
    private final List<StoragePlace> storagePlaces = new ArrayList<>();

    private Courier(String name, Speed speed, Location location) {
        super(UUID.randomUUID());
        this.name = name;
        this.speed = speed;
        this.location = location;
        storagePlaces.add(StoragePlace.getDefault());
    }


    public static Result<Courier, Error> create(String name, Speed speed, Location location) {
        var errors = Guard.combine(
                GeneralErrors.valueIsRequired(name),
                GeneralErrors.valueIsRequired(speed, "speed"),
                GeneralErrors.valueIsRequired(location, "location")
        );
        if (errors != null) Result.failure(errors);
        return Result.success(new Courier(name, speed, location));
    }

    public static Courier mustCreate(String name, Speed speed, Location location) {
        return create(name, speed, location).getValueOrThrow();
    }

    public UnitResult<Error> addStoragePlace(String name, Volume volume) {
        var creationResult = StoragePlace.create(name, volume);
        if (creationResult.isFailure()) {
            return UnitResult.failure(creationResult.getError());
        }
        this.storagePlaces.add(creationResult.getValue());
        return UnitResult.success();
    }

    public Boolean canTakeOrder(Volume orderVolume) {
        for (StoragePlace place : this.storagePlaces) {
            if (place.canPutOrder(orderVolume)) {
                return true;
            }
        }
        return false;
    }

    public Result<StoragePlace, Error> getFreeStoragePlace(Volume orderVolume) {
        for (StoragePlace place : this.storagePlaces) {
            if (place.canPutOrder(orderVolume)) {
                return Result.success(place);
            }
        }
        return Result.failure(Errors.noSuitableStoragePlace());
    }

    public UnitResult<Error> takeOrder(Order order) {
        Objects.requireNonNull(order, "order");

        if (!canTakeOrder(order.getVolume())) {
            return UnitResult.failure(Errors.couldNotTakeOrder());
        }

        var putResult = getFreeStoragePlace(order.getVolume()).getValue().putOrder(order.getId(), order.getVolume());
        if (putResult.isFailure()) {
            return UnitResult.failure(putResult.getError());
        }

        return UnitResult.success();
    }

    public UnitResult<Error> completeOrder(Order order) {
        Objects.requireNonNull(order, "order");

        var targetPlaceResult = findStorageContainingOrder(order.getId());
        if (targetPlaceResult.isFailure()) {
            return UnitResult.failure(targetPlaceResult.getError());
        }

        var extractResult = targetPlaceResult.getValue().extractOrder();
        if (extractResult.isFailure()) {
            return UnitResult.failure(extractResult.getError());
        }

        return UnitResult.success();
    }

    private Result<StoragePlace, Error> findStorageContainingOrder(UUID orderId) {
        for (StoragePlace place : this.storagePlaces) {
            if (place.containsOrder(orderId)) {
                return Result.success(place);
            }
        }
        return Result.failure(Errors.orderNotFoundOnCourier());
    }

    public Integer calculateStepsToLocation(Location orderLocation) {
        var distanceToOrderLocation = this.location.distanceTo(orderLocation);
        return this.speed.calculateStepsForDistance(distanceToOrderLocation);
    }

    public UnitResult<Error> move(Location target) {
        Objects.requireNonNull(target, "target");

        int difX = target.getX() - this.location.getX();
        int difY = target.getY() - this.location.getY();

        int cruisingRange = this.speed.getSpeed();

        int moveX = Math.max(-cruisingRange, Math.min(difX, cruisingRange));
        cruisingRange -= Math.abs(moveX);

        int moveY = Math.max(-cruisingRange, Math.min(difY, cruisingRange));

        Result<Location, Error> locationCreateResult = Location.create(
                this.location.getX() + moveX,
                this.location.getY() + moveY
        );

        if (locationCreateResult.isFailure()) {
            return UnitResult.failure(locationCreateResult.getError());
        }

        this.location = locationCreateResult.getValue();
        return UnitResult.success();
    }

    public static final class Errors {

        public static Error noSuitableStoragePlace() {
            return Error.of(
                    "courier.no.suitable.storage",
                    "Не найдено свободного места хранения, способного вместить этот заказ"
            );
        }

        public static Error couldNotTakeOrder() {
            return Error.of(
                    "could.not.take.order",
                    "Не может взять заказа"
            );
        }

        public static Error orderNotFoundOnCourier() {
            return Error.of(
                    "courier.order.not.found",
                    "Заказ с указанным ID не найден у курьера"
            );
        }
    }


}


