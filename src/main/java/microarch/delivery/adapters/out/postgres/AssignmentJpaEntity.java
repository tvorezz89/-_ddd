package microarch.delivery.adapters.out.postgres;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import microarch.delivery.core.domain.model.courier.Assignment;
import microarch.delivery.core.domain.model.courier.AssignmentStatus;
import microarch.delivery.core.domain.model.order.Location;
import microarch.delivery.core.domain.model.order.Volume;

@Getter
@Entity
@Table(name = "assignments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class AssignmentJpaEntity {
    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "volume", nullable = false)
    private int volume;

    @Column(name = "location_x", nullable = false)
    private int locationX;

    @Column(name = "location_y", nullable = false)
    private int locationY;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AssignmentStatus status;

    private AssignmentJpaEntity(
            UUID id,
            UUID orderId,
            int volume,
            int locationX,
            int locationY,
            AssignmentStatus status) {
        this.id = id;
        this.orderId = orderId;
        this.volume = volume;
        this.locationX = locationX;
        this.locationY = locationY;
        this.status = status;
    }

    static AssignmentJpaEntity fromDomain(Assignment assignment) {
        return new AssignmentJpaEntity(
                assignment.getId(),
                assignment.getOrderId(),
                assignment.getVolume().getValue(),
                assignment.getLocation().getX(),
                assignment.getLocation().getY(),
                assignment.getStatus());
    }

    Assignment toDomain() {
        return Assignment.restore(
                id, orderId, Volume.of(volume), Location.of(locationX, locationY), status);
    }
}
