package microarch.delivery.core.domain.model.courier;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import libs.ddd.BaseEntity;
import libs.errs.Error;
import libs.errs.Guard;
import libs.errs.Result;
import libs.errs.UnitResult;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Volume;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "assignments")
@Getter
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
public final class Assignment extends BaseEntity<UUID> {

    @Column(name = "order_id", nullable = false)
    private final UUID orderId;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "volume", nullable = false))
    private final Volume volume;

    @Embedded
    @AttributeOverrides({ @AttributeOverride(name = "x", column = @Column(name = "location_x", nullable = false)),
            @AttributeOverride(name = "y", column = @Column(name = "location_y", nullable = false)) })
    private final Location location;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AssignmentStatus status;

    private Assignment(UUID id, UUID orderId, Volume volume, Location location, AssignmentStatus status) {
        super(id);
        this.orderId = orderId;
        this.volume = volume;
        this.location = location;
        this.status = status;
    }

    public static Result<Assignment, Error> create(UUID orderId, Volume volume, Location location) {
        Objects.requireNonNull(volume, "volume");
        Objects.requireNonNull(location, "location");

        var err = Guard.againstNullOrEmpty(orderId, "orderId");
        if (err != null) {
            return Result.failure(err);
        }

        return Result.success(new Assignment(UUID.randomUUID(), orderId, volume, location, AssignmentStatus.ASSIGNED));
    }

    public static Assignment mustCreate(UUID orderId, Volume volume, Location location) {
        return create(orderId, volume, location).getValueOrThrow();
    }

    static Assignment restore(UUID id, UUID orderId, Volume volume, Location location, AssignmentStatus status) {
        Objects.requireNonNull(volume, "volume");
        Objects.requireNonNull(location, "location");

        var err = Guard.combine(Guard.againstNullOrEmpty(id, "id"), Guard.againstNullOrEmpty(orderId, "orderId"));
        if (err != null) {
            return Result.<Assignment, Error> failure(err).getValueOrThrow();
        }

        if (status == null) {
            return Result.<Assignment, Error> failure(Errors.invalidStatus()).getValueOrThrow();
        }

        return new Assignment(id, orderId, volume, location, status);
    }

    public UnitResult<Error> complete(Location courierLocation) {
        Objects.requireNonNull(courierLocation, "courierLocation");

        if (this.status == AssignmentStatus.COMPLETED) {
            return UnitResult.failure(Errors.alreadyCompleted());
        }

        if (!this.location.isWithinOneCellFrom(courierLocation)) {
            return UnitResult.failure(Errors.courierIsTooFar());
        }

        this.status = AssignmentStatus.COMPLETED;
        return UnitResult.success();
    }

    public static final class Errors {
        public static Error invalidStatus() {
            return Error.of("assignment.invalid.status", "Assignment status is invalid");
        }

        public static Error alreadyCompleted() {
            return Error.of("assignment.already.completed", "Assignment is already completed");
        }

        public static Error courierIsTooFar() {
            return Error.of("assignment.courier.is.too.far",
                    "Courier must be within one cell from assignment location");
        }
    }
}
