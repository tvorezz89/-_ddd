package microarch.delivery.adapters.out.postgres;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Location;
import microarch.delivery.core.domain.model.order.Volume;

@Getter
@Entity
@Table(name = "couriers")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class CourierJpaEntity {
    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "location_x", nullable = false)
    private int locationX;

    @Column(name = "location_y", nullable = false)
    private int locationY;

    @Column(name = "max_volume", nullable = false)
    private int maxVolume;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "courier_id", nullable = false)
    private List<AssignmentJpaEntity> assignments;

    private CourierJpaEntity(
            UUID id,
            String name,
            int locationX,
            int locationY,
            int maxVolume,
            List<AssignmentJpaEntity> assignments) {
        this.id = id;
        this.name = name;
        this.locationX = locationX;
        this.locationY = locationY;
        this.maxVolume = maxVolume;
        this.assignments = assignments;
    }

    static CourierJpaEntity fromDomain(Courier courier) {
        var assignments = courier.getAssignments().stream()
                .map(AssignmentJpaEntity::fromDomain)
                .toList();

        return new CourierJpaEntity(
                courier.getId(),
                courier.getName(),
                courier.getLocation().getX(),
                courier.getLocation().getY(),
                courier.getMaxVolume().getValue(),
                assignments);
    }

    Courier toDomain() {
        var domainAssignments = assignments.stream()
                .map(AssignmentJpaEntity::toDomain)
                .toList();

        return Courier.restore(
                id,
                name,
                Location.of(locationX, locationY),
                Volume.of(maxVolume),
                domainAssignments);
    }
}
