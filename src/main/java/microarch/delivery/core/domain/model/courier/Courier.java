package microarch.delivery.core.domain.model.courier;

import libs.ddd.Aggregate;
import libs.errs.DomainInvariantException;
import libs.errs.Error;
import libs.errs.GeneralErrors;
import libs.errs.Guard;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import microarch.delivery.core.domain.model.order.Location;
import microarch.delivery.core.domain.model.order.Volume;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Courier extends Aggregate<UUID> {
    private static final Volume MAX_VOLUME = Volume.of(20);

    private String name;
    private Location location;
    private Volume maxVolume;
    private List<Assignment> assignments;

    private Courier(UUID id, String name, Location location) {
        super(id);
        validate(id, name, location);
        this.name = name;
        this.location = location;
        this.maxVolume = MAX_VOLUME;
        this.assignments = new ArrayList<>();
    }

    public static Courier create(String name, Location location) {
        return new Courier(UUID.randomUUID(), name, location);
    }

    public boolean canTakeAdditionalOrder(Volume volume) {
        if (volume == null) {
            throw new DomainInvariantException(GeneralErrors.valueIsRequired("volume"));
        }

        var assignmentsVolume = assignments.stream()
                .filter(assignment -> assignment.getStatus() == AssignmentStatus.ASSIGNED)
                .map(assignment -> assignment.getVolume().getValue()).reduce(0, Integer::sum);

        return assignmentsVolume + volume.getValue() <= maxVolume.getValue();
    }

    public void assignOrder(UUID orderId, Volume volume, Location orderLocation) {
        Error.throwIf(Guard.againstNullOrEmpty(orderId, "orderId"));

        if (orderLocation == null) {
            throw new DomainInvariantException(GeneralErrors.valueIsRequired("orderLocation"));
        }

        if (!canTakeAdditionalOrder(volume)) {
            throw new DomainInvariantException(GeneralErrors.valueIsInvalid("volume", volume.getValue()));
        }

        assignments.add(Assignment.create(UUID.randomUUID(), orderId, volume, orderLocation));
    }

    public void completeAssignment(UUID orderId) {
        Error.throwIf(Guard.againstNullOrEmpty(orderId, "orderId"));
        var assignment = assignments.stream()
                .filter(currentAssignment -> currentAssignment.getOrderId().equals(orderId)).findFirst()
                .orElseThrow(() -> new DomainInvariantException(GeneralErrors.notFound("assignment", orderId)));
        assignment.complete(location);
    }

    public void move(Location newLocation) {
        if (newLocation == null) {
            throw new DomainInvariantException(GeneralErrors.valueIsRequired("location"));
        }
        location = newLocation;
    }

    public int distanceTo(Location location) {
        if (location == null) {
            throw new DomainInvariantException(GeneralErrors.valueIsRequired("location"));
        }

        var xDistance = Math.abs(this.location.getX() - location.getX());
        var yDistance = Math.abs(this.location.getY() - location.getY());
        return xDistance + yDistance;
    }

    private static void validate(UUID id, String name, Location location) {
        Error.throwIf(Guard.againstNullOrEmpty(id, "id"));
        Error.throwIf(Guard.againstNullOrEmpty(name, "name"));

        if (location == null) {
            throw new DomainInvariantException(GeneralErrors.valueIsRequired("location"));
        }
    }
}
