package microarch.delivery.core.domain.model.courier;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "couriers")
@Getter
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
public final class Courier extends Aggregate<UUID> {
    private static final Volume DEFAULT_MAX_VOLUME = Volume.mustCreate(20);

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "courier_id", nullable = false)
    private final List<Assignment> assignments = new ArrayList<>();

    @Column(name = "name", nullable = false)
    private final String name;

    @Embedded
    @AttributeOverrides({ @AttributeOverride(name = "x", column = @Column(name = "location_x", nullable = false)),
            @AttributeOverride(name = "y", column = @Column(name = "location_y", nullable = false)) })
    private Location location;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "max_volume", nullable = false))
    private final Volume maxVolume;

    private Courier(UUID id, String name, Location location) {
        super(id);
        this.name = name;
        this.location = location;
        this.maxVolume = DEFAULT_MAX_VOLUME;
    }

    public static Result<Courier, Error> create(String name, Location location) {
        Objects.requireNonNull(location, "location");

        var err = Guard.againstNullOrEmpty(name, "name");
        if (err != null) {
            return Result.failure(err);
        }

        return Result.success(new Courier(UUID.randomUUID(), name, location));
    }

    public static Courier mustCreate(String name, Location location) {
        return create(name, location).getValueOrThrow();
    }

    public List<Assignment> getAssignments() {
        return List.copyOf(this.assignments);
    }

    public boolean canAssign(Volume volume) {
        Objects.requireNonNull(volume, "volume");

        return currentAssignedVolume().add(volume).isLessThanOrEqualTo(this.maxVolume);
    }

    public UnitResult<Error> assign(UUID orderId, Volume volume, Location location) {
        Objects.requireNonNull(volume, "volume");
        Objects.requireNonNull(location, "location");

        if (!canAssign(volume)) {
            return UnitResult.failure(Errors.capacityExceeded());
        }

        var assignmentResult = Assignment.create(orderId, volume, location);
        if (assignmentResult.isFailure()) {
            return UnitResult.failure(assignmentResult.getError());
        }

        this.assignments.add(assignmentResult.getValue());
        return UnitResult.success();
    }

    public UnitResult<Error> completeAssignment(UUID orderId) {
        var err = Guard.againstNullOrEmpty(orderId, "orderId");
        if (err != null) {
            return UnitResult.failure(err);
        }

        var assignment = this.assignments.stream().filter(a -> a.getOrderId().equals(orderId)).findFirst();

        if (assignment.isEmpty()) {
            return UnitResult.failure(Errors.assignmentNotFound());
        }

        return assignment.get().complete(this.location);
    }

    public UnitResult<Error> moveTo(Location location) {
        Objects.requireNonNull(location, "location");

        this.location = location;
        return UnitResult.success();
    }

    private Volume currentAssignedVolume() {
        return this.assignments.stream().filter(a -> a.getStatus() == AssignmentStatus.ASSIGNED)
                .map(Assignment::getVolume).reduce(Volume.zero(), Volume::add);
    }

    public static final class Errors {
        public static Error capacityExceeded() {
            return Error.of("courier.capacity.exceeded", "Courier capacity exceeded");
        }

        public static Error assignmentNotFound() {
            return Error.of("courier.assignment.not.found", "Assignment not found");
        }
    }
}
