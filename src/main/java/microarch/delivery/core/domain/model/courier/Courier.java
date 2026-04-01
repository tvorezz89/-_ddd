package microarch.delivery.core.domain.model.courier;

import jakarta.persistence.*;
import libs.ddd.Aggregate;
import libs.errs.Error;
import libs.errs.Guard;
import libs.errs.Result;
import libs.errs.UnitResult;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Volume;
import microarch.delivery.core.domain.model.order.Assignment;
import microarch.delivery.core.domain.model.order.Order;

import java.util.*;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Courier extends Aggregate<UUID> {

    private static final Volume DEFAULT_MAX_VOLUME = Volume.mustCreate(20);

    @Column(name = "name")
    private String name;

    @Embedded
    private Location location;

    @Embedded
    @AttributeOverride(
            name = "size",
            column = @Column(name = "maxVolume", nullable = false)
    )
    private Volume maxVolume;

    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    @JoinColumn(name = "assignment_id", nullable = false)
    private final List<Assignment> assignments = new ArrayList<>();

    private Courier(UUID id, String name, Location location, Volume maxVolume) {
        super(id);
        this.name = name;
        this.location = location;
        this.maxVolume = maxVolume;
    }

    public static Result<Courier, Error> create(String name, Location location) {
        Objects.requireNonNull(location);
        Error error = Guard.againstNullOrEmpty(name, "name");
        if (error != null)
            return Result.failure(error);

        var courier = new Courier(UUID.randomUUID(), name, location, DEFAULT_MAX_VOLUME);
        return Result.success(courier);
    }

    public static Courier mustCreate(String name, Location location) {
        return create(name, location).getValueOrThrow();
    }

    public UnitResult<Error> takeOrder(Order order) {
        Objects.requireNonNull(order);
        int currentVolume = assignments.stream().map(Assignment::getVolume).mapToInt(Volume::getSize).sum();
        if (order.getVolume().getSize() + currentVolume > maxVolume.getSize()) {
            return UnitResult.failure(
                    Errors.orderToHeavyForCourier(currentVolume, maxVolume.getSize(), order.getVolume().getSize())
            );
        }
        assignments.add(Assignment.mustCreate(order.getId(), order.getVolume(), order.getLocation()));
        return UnitResult.success();

    }

    public UnitResult<Error> completeAssignment(Assignment assignment) {
        Objects.requireNonNull(assignment);
        Optional<Assignment> courierAssignment = assignments.stream().filter(a -> a.equals(assignment)).findFirst();
        if (courierAssignment.isEmpty()) {
            return UnitResult.failure(
                    Errors.assignmentNotTakenByCourier(assignment)
            );
        }
        Result<Void, Error> result = assignment.completeOrderByCourierLocation(location);
        if (result.isFailure()) {
            return UnitResult.failure(result.getError());
        }
        assignments.remove(assignment);
        return UnitResult.success();
    }

    public UnitResult<Error> changeLocation(Location newLocation) {
        Objects.requireNonNull(newLocation);
        if (newLocation.equals(this.location) || !newLocation.isInReachingDistance(this.location)) {
            return UnitResult.failure(Errors.illegalMovement(this.location, newLocation));
        }
        this.location = newLocation;
        return UnitResult.success();
    }

    public static final class Errors {

        public static Error orderToHeavyForCourier(int currentVolumeSize, int maxVolumeSize, int addedVolumeSize) {
            return Error.of(
                    "orderToHeavyForCourier",
                    "Courier cannot take this order. Max volume for courier:" + maxVolumeSize +
                            ", current volume: " + currentVolumeSize +
                            ", added volume: " + addedVolumeSize
            );
        }

        public static Error assignmentNotTakenByCourier(Assignment assignment) {
            return Error.of(
                    "assignmentNotTakenByCourier",
                    "Assignment does not belong to this courier: " + assignment
            );
        }

        public static Error illegalMovement(Location from, Location to) {
            return Error.of(
                    "illegalMovement",
                    "Illegal movement for courier: " + from + " to " + to
            );
        }
    }
}
