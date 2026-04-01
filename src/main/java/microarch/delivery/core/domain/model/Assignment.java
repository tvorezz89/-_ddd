package microarch.delivery.core.domain.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import libs.ddd.BaseEntity;
import libs.errs.Error;
import libs.errs.GeneralErrors;
import libs.errs.Result;
import libs.errs.Guard;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.UUID;

@Getter
@Entity
@Table(name = "assignments")
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Assignment extends BaseEntity<UUID> {

    @Column(name = "order_id")
    private UUID orderId;

    @Embedded
    @AttributeOverride(name = "volume", column = @Column(name = "volume"))
    private Volume volume;

    @Embedded
    @AttributeOverride(name = "x", column = @Column(name = "location_x"))
    @AttributeOverride(name = "y", column = @Column(name = "location_y"))
    private Location location;

    @Enumerated(EnumType.STRING)
    private Status status;

    public static Result<Assignment, Error> create(UUID orderId, Volume volume, Location location) {
        if (Guard.againstNullOrEmpty(orderId, "orderId") != null)
            return Result.failure(GeneralErrors.valueIsRequired("orderId"));
        if (Objects.isNull(volume))
            return Result.failure(GeneralErrors.valueIsRequired("volume"));
        if (Objects.isNull(location))
            return Result.failure(GeneralErrors.valueIsRequired("location"));
        Assignment assignment = new Assignment(orderId, volume, location, Status.ASSIGNED);
        assignment.id = UUID.randomUUID();
        return Result.success(assignment);
    }

    public Result<Assignment, Error> finish(Location courierLocation) {
        if (!this.canFinish(courierLocation))
            return Result.failure(Error.of("Cannot finish assignment", "Courier is to far away from location"));
        this.status = Status.COMPLETED;
        return Result.success(this);
    }

    private boolean canFinish(Location courierLocation) {
        return this.location.calculateDistance(courierLocation) <= 1;
    }

}
