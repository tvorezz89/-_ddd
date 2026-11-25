package microarch.delivery.core.domain.model.aggregate;

import jakarta.persistence.*;
import libs.ddd.Aggregate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.OrderStatus;
import microarch.delivery.core.domain.model.Volume;

import java.util.UUID;

@Entity
@Table(name = "order")
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
@Getter
public class Order extends Aggregate<UUID> {
    @Column(name = "volume")
    @Embedded
    private Volume volume;

    @Column(name = "location")
    @Embedded
    private Location location;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status")
    private OrderStatus status;

    private Order(UUID id, Location location, Volume volume) {
        super(id);
        this.location = location;
        this.volume = volume;
        this.status = OrderStatus.CREATED;
    }

    public static Order create(Location location, Volume volume) {
        if (location == null)
            throw new IllegalArgumentException("Order location must not be null");
        if (volume == null)
            throw new IllegalArgumentException("Order volume must not be null");
        return new Order(UUID.randomUUID(), location, volume);
    }

    public void assign() {
        if (status != OrderStatus.CREATED) {
            throw new IllegalStateException("Only CREATED order can be assigned");
        }
        status = OrderStatus.ASSIGNED;
    }

    public void complete() {
        if (status != OrderStatus.ASSIGNED) {
            throw new IllegalStateException("Only ASSIGNED order can be completed");
        }
        status = OrderStatus.COMPLETED;
    }
}
