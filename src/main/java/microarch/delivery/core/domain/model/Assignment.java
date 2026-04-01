package microarch.delivery.core.domain.model;

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
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Assignment extends BaseEntity<UUID> {

    private UUID orderId;
    private Volume volume;
    private Location location;
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
