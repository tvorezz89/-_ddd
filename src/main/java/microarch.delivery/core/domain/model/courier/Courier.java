package microarch.delivery.core.domain.model.courier;

import jakarta.persistence.*;
import libs.ddd.Aggregate;
import libs.errs.Error;
import libs.errs.Result;
import libs.errs.UnitResult;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import microarch.delivery.core.domain.kernel.Location;
import microarch.delivery.core.domain.kernel.Speed;
import microarch.delivery.core.domain.kernel.Volume;
import microarch.delivery.core.domain.model.order.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "couriers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Courier extends Aggregate<UUID> {

    @Column(name = "name")
    private String name;

    @Embedded
    private Speed speed;

    @Embedded
    private Location location;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "courier_id")
    private List<StoragePlace> storagePlaces;

    private Courier(String name, Speed speed, Location location, List<StoragePlace> storagePlaces) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.speed = speed;
        this.location = location;
        this.storagePlaces = storagePlaces;
    }

    public static Result<Courier, Error> create(String name, Speed speed, Location location) {
        if (name == null) {
            throw new IllegalArgumentException("name is null");
        }
        if (speed == null) {
            throw new IllegalArgumentException("speed is null");
        }
        if (location == null) {
            throw new IllegalArgumentException("location is null");
        }
        var storages = createDefaultStorages();

        return Result.success(new Courier(name, speed, location, storages));
    }

    public UnitResult<Error> addStorage(String name, Volume volume) {
        if (name == null) {
            throw new IllegalArgumentException("name is null");
        }
        if (volume == null) {
            throw new IllegalArgumentException("volume is null");
        }
        var storage = StoragePlace.getCreated(name, volume);
        storagePlaces.add(storage);

        return UnitResult.success();
    }

    public Result<Boolean, Error> canTakeAnOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("order is null");
        }

        return Result.success(findAvailableStorage(order).isPresent());
    }

    public UnitResult<Error> takeAnOrder(Order order) {
        if (!canTakeAnOrder(order).getValue()) {
            return UnitResult.failure(FAIL_TAKE_ORDER);
        }

        var storagePlace = findAvailableStorage(order)
                .orElseThrow(() -> new RuntimeException("Unable to find storage to take an order"));

        var storeResult = storagePlace.store(order.getId(), order.getVolume());
        if (storeResult.isFailure()) {
            return UnitResult.failure(storeResult.getError());
        }
        order.manageToCourier(getId());

        return UnitResult.success();
    }

    public UnitResult<Error> completeAnOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("order is null");
        }

        var storagePlace = storagePlaces.stream().map(storage -> storage.clear(order.getId()))
                .filter(UnitResult::isSuccess).findFirst();

        if (storagePlace.isEmpty()) {
            return UnitResult.failure(ORDER_NOT_FOUND);
        }

        return UnitResult.success();
    }

    public Result<Integer, Error> movesCount(Location target) {
        if (target == null) {
            throw new IllegalArgumentException("target is null");
        }

        var distanceTo = location.distanceTo(target).getValue();
        var result = (int) Math.ceil((double) distanceTo / speed.getSpeed());

        return Result.success(result);
    }

    public UnitResult<Error> move(Location target) {
        if (target == null) {
            throw new IllegalArgumentException("target is null");
        }

        int difX = target.getX() - location.getX();
        int difY = target.getY() - location.getY();
        int cruisingRange = speed.getSpeed();

        int moveX = Math.max(-cruisingRange, Math.min(difX, cruisingRange));
        cruisingRange -= Math.abs(moveX);

        int moveY = Math.max(-cruisingRange, Math.min(difY, cruisingRange));

        Result<Location, Error> locationCreateResult = Location.create(location.getX() + moveX,
                location.getY() + moveY);

        if (locationCreateResult.isFailure()) {
            return UnitResult.failure(locationCreateResult.getError());
        }

        this.location = locationCreateResult.getValue();
        return UnitResult.success();
    }

    private static ArrayList<StoragePlace> createDefaultStorages() {
        var volume = 10;
        var storages = new ArrayList<StoragePlace>();
        var baggage = StoragePlace.getCreated("Сумка", Volume.getCreated(volume));
        storages.add(baggage);
        return storages;
    }

    private Optional<StoragePlace> findAvailableStorage(Order order) {
        var alreadyStored = storagePlaces.stream().anyMatch(storage -> storage.getOrderId() != null);
        if (alreadyStored) {
            return Optional.empty();
        }
        return storagePlaces.stream().filter(storage -> storage.canStore(order.getVolume()).getValue()).findFirst();
    }

    private static final Error FAIL_TAKE_ORDER = Error.of("fail.take.order", "Unable to take an order");
    private static final Error ORDER_NOT_FOUND = Error.of("order.not.found", "An order was not found");
}
