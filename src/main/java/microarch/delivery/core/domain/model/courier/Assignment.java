package microarch.delivery.core.domain.model.courier;

import libs.ddd.BaseEntity;
import libs.errs.DomainInvariantException;
import libs.errs.Error;
import libs.errs.GeneralErrors;
import libs.errs.Guard;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import microarch.delivery.core.domain.model.order.Location;
import microarch.delivery.core.domain.model.order.Volume;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Assignment extends BaseEntity<UUID> {
    private UUID orderId;
    private Volume volume;
    private Location location;
    private AssignmentStatus status;

    private Assignment(UUID id, UUID orderId, Volume volume, Location location, AssignmentStatus status) {
        super(id);
        validate(id, orderId, volume, location, status);
        this.orderId = orderId;
        this.volume = volume;
        this.location = location;
        this.status = status;
    }

    public static Assignment create(UUID id, UUID orderId, Volume volume, Location location) {
        return new Assignment(id, orderId, volume, location, AssignmentStatus.ASSIGNED);
    }

    public static Assignment restore(
            UUID id, UUID orderId, Volume volume, Location location, AssignmentStatus status) {
        return new Assignment(id, orderId, volume, location, status);
    }

    public void complete(Location courierLocation) {
        if (courierLocation == null) {
            throw new DomainInvariantException(GeneralErrors.valueIsRequired("courierLocation"));
        }

        if (status != AssignmentStatus.ASSIGNED) {
            throw new DomainInvariantException(GeneralErrors.valueIsInvalid("status", status));
        }

        if (!courierLocation.isOneCellOrCloserTo(location)) {
            throw new DomainInvariantException(GeneralErrors.valueIsInvalid("courierLocation", courierLocation));
        }

        status = AssignmentStatus.COMPLETED;
    }

    private static void validate(UUID id, UUID orderId, Volume volume, Location location, AssignmentStatus status) {
        Error.throwIf(Guard.againstNullOrEmpty(id, "id"));
        Error.throwIf(Guard.againstNullOrEmpty(orderId, "orderId"));

        if (volume == null) {
            throw new DomainInvariantException(GeneralErrors.valueIsRequired("volume"));
        }

        if (location == null) {
            throw new DomainInvariantException(GeneralErrors.valueIsRequired("location"));
        }

        if (status == null) {
            throw new DomainInvariantException(GeneralErrors.valueIsRequired("status"));
        }
    }
}
