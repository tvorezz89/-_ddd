package microarch.delivery.core.domain.model.aggregate;

import jakarta.persistence.*;
import libs.ddd.Aggregate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import microarch.delivery.core.domain.model.Assignment;
import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.OrderStatus;
import microarch.delivery.core.domain.model.Status;
import microarch.delivery.core.domain.model.Volume;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "courier")
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
@Getter
public class Courier extends Aggregate<UUID> {
    private static final double VOLUME_MAX = 20.0;

    @Column(name = "name")
    private final String name;

    @Embedded
    @Column(name = "max_volume")
    private final Volume maxVolume;

    @Embedded
    @Column(name = "location")
    private Location location;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "courier_id", nullable = false)
    private List<Assignment> assignments = new ArrayList<>();

    private Courier(UUID id, String name, Location location) {
        super(id);
        this.name = name;
        this.maxVolume = Volume.create(VOLUME_MAX);
        this.location = location;
    }

    public static Courier create(Location location, String name) {
        if (location == null)
            throw new IllegalArgumentException("Courier location must not be null");
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Courier name must not be blank");
        return new Courier(UUID.randomUUID(), name, location);
    }

    public boolean canTake(Order order) {
        if (order == null)
            throw new IllegalArgumentException("Order must not be null");
        if (order.getStatus() != OrderStatus.CREATED)
            return false;
        double occupied = assignments.stream().filter(a -> a.getStatus() == Status.ASSIGNED)
                .mapToDouble(a -> a.getVolume().getAmount()).sum();
        return occupied + order.getVolume().getAmount() <= maxVolume.getAmount();
    }

    public void takeOrder(Order order) {
        if (!canTake(order)) {
            throw new IllegalArgumentException("Courier can t take this order!");
        }
        assignments.add(Assignment.create(order.getId(), order.getVolume(), order.getLocation(), Status.ASSIGNED));
    }

    public void completeAssignment(Order order) {
        if (order == null)
            throw new IllegalArgumentException("Order must not be null");
        Assignment assignment = findAssignment(order);
        if (assignment == null)
            throw new IllegalArgumentException("Assignment not found for order: " + order.getId());
        assignment.complete(location);
    }

    public void moveTo(Location destination) {
        if (destination == null)
            throw new IllegalArgumentException("Destination must not be null");
        if (location.calculateDistanceTo(destination) > 1) {
            throw new IllegalArgumentException("Courier can move at most one cell");
        }
        this.location = destination;
    }

    private Assignment findAssignment(Order order) {
        return assignments.stream().filter(a -> a.getOrderId().equals(order.getId())).findFirst().orElse(null);
    }
}
