package microarch.delivery.core.domain.model.order;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import libs.ddd.BaseEntity;
import libs.errs.GeneralErrors;
import libs.errs.Result;
import libs.errs.Error;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Status;
import microarch.delivery.core.domain.model.Volume;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "assignments")
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
public final class Assignment extends BaseEntity<UUID> {

    @Column(name = "order_id")
    @Getter
    private final UUID orderId;

    @Column(name = "volume")
    @Getter
    private final Volume volume;

    @Column(name = "location")
    @Getter
    private final Location location;

    @Column(name = "status")
    @Getter
    private Status status;

    private Assignment(UUID orderId, Volume volume, Location location) {
        super(UUID.randomUUID());
        this.orderId = orderId;
        this.volume = volume;
        this.location = location;
        this.status = Status.ASSIGNED;
    }

    public static Result<Assignment, Error> create(UUID orderId, Volume volume, Location location) {
        Objects.requireNonNull(orderId, "orderId");
        Objects.requireNonNull(volume, "volume");
        Objects.requireNonNull(location, "location");

        var assignment = new Assignment(orderId, volume, location);
        return Result.success(assignment);
    }

    public static Assignment mustCreate(UUID orderId, Volume volume, Location location) {
        return create(orderId, volume, location).getValueOrThrow();
    }

    public Result<Void, Error> completeOrderByCourierLocation(Location courierLocation) {
        if (this.status == Status.COMPLETED) {
            return Result.failure(Errors.assignmentAlreadyCompleted(this.id));
        }
        if (courierLocation == null) {
            return Result.failure(GeneralErrors.valueIsRequired("courierLocation"));
        }
        if (!courierLocation.isInReachingDistance(this.location)) {
            return Result.failure(Errors.locationTooFar(courierLocation));
        }
        this.status = Status.COMPLETED;
        return Result.success();
    }

    public static class Errors {
        public static Error assignmentAlreadyCompleted(UUID id) {
            return Error.of("assignment.already.completed",
                    String.format("Assignment with id %s already completed", id));
        }

        public static Error locationTooFar(Location location) {
            return Error.of("location.too.far.for.completion",
                    String.format("Courier location %s is too far for assignment completion", location));
        }
    }
}


