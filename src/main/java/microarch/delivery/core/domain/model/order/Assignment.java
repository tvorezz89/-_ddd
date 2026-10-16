package microarch.delivery.core.domain.model.order;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import libs.ddd.BaseEntity;
import libs.errs.Error;
import libs.errs.GeneralErrors;
import libs.errs.Guard;
import libs.errs.Result;
import libs.errs.UnitResult;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import microarch.delivery.core.domain.model.Location;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "assignments")
@Getter
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
public final class Assignment extends BaseEntity<UUID> {

    @Column(name = "order_id", nullable = false, unique = true)
    private final UUID orderId;

    @Column(name = "volume", nullable = false)
    private final Volume volume;

    @Column(name = "location", nullable = false)
    private final Location location;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AssignmentStatus status;

    private Assignment(UUID id, UUID orderId, Volume volume, Location location) {
        super(id);
        this.orderId = orderId;
        this.volume = volume;
        this.location = location;
        this.status = AssignmentStatus.ASSIGNED;
    }

    public static Result<Assignment, Error> create(UUID orderId, Volume volume, Location location) {
        var err = Guard.combine(Guard.againstNullOrEmpty(orderId, "orderId"),
                volume == null ? GeneralErrors.valueIsRequired("volume") : null,
                location == null ? GeneralErrors.valueIsRequired("location") : null);
        if (err != null)
            return Result.failure(err);

        var assignment = new Assignment(UUID.randomUUID(), orderId, volume, location);
        return Result.success(assignment);
    }

    public static Assignment mustCreate(UUID orderId, Volume volume, Location location) {
        return create(orderId, volume, location).getValueOrThrow();
    }

    /**
     * Завершить задание на доставку можно только если курьер находится в одной клетке или ближе от Location заказа.
     */
    public UnitResult<Error> complete(Location courierLocation) {
        Objects.requireNonNull(courierLocation, "courierLocation");

        if (this.status == AssignmentStatus.COMPLETED)
            return UnitResult.failure(Errors.alreadyCompleted(this.id));

        if (this.location.distanceTo(courierLocation) > 1)
            return UnitResult.failure(Errors.tooFarFromOrder(this.location, courierLocation));

        this.status = AssignmentStatus.COMPLETED;
        return UnitResult.success();
    }

    public static final class Errors {
        public static Error tooFarFromOrder(Location orderLocation, Location courierLocation) {
            return Error.of("assignment.too.far.from.order",
                    "Курьер не может завершить задание: он находится слишком далеко от места доставки. "
                            + "Локация заказа: " + orderLocation + ", локация курьера: " + courierLocation);
        }

        public static Error alreadyCompleted(UUID assignmentId) {
            return Error.of("assignment.already.completed", "Задание " + assignmentId + " уже завершено");
        }
    }
}
