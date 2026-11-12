package microarch.delivery.core.domain.model;

import jakarta.persistence.*;
import libs.ddd.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "assignment")
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
@Getter
public class Assignment extends BaseEntity<UUID> {
    private static final int MIN_DISTANCE = 1;

    @Column(name = "order_id")
    private final UUID orderId;

    @Column(name = "volume")
    @Embedded
    private final Volume volume;

    @Column(name = "location")
    @Embedded
    private final Location location;

    @Enumerated(EnumType.STRING)
    @Column(name = "assignment_status")
    private Status status;

    private Assignment(UUID orderId, Volume volume, Location location, Status status) {
        super(UUID.randomUUID());
        this.orderId = orderId;
        this.volume = volume;
        this.location = location;
        this.status = status;
    }

    public static Assignment create(UUID orderId, Volume volume, Location location, Status status) {
        if (volume == null) {
            throw new IllegalArgumentException("Wrong volume of assignment");
        }
        if (location == null) {
            throw new IllegalArgumentException("Wrong location of assignment");
        }
        if (orderId == null) {
            throw new IllegalArgumentException("Wrong orderId of assignment");
        }
        if (!Status.ASSIGNED.equals(status)) {
            throw new IllegalArgumentException("Wrong status of assignment");
        }
        return new Assignment(orderId, volume, location, status);
    }

    public void complete(Location courierLocation) {
        var distance = courierLocation.calculateDistanceTo(this.location);
        if (distance <= MIN_DISTANCE) {
            this.status = Status.COMPLETED;
        } else {
            throw new IllegalArgumentException("Distance to order is too big");
        }
    }
}
