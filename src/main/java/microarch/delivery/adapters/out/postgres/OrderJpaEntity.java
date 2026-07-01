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
import microarch.delivery.core.domain.model.order.Location;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.model.order.OrderStatus;
import microarch.delivery.core.domain.model.order.Volume;

@Getter
@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class OrderJpaEntity {
    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "location_x", nullable = false)
    private int locationX;

    @Column(name = "location_y", nullable = false)
    private int locationY;

    @Column(name = "volume", nullable = false)
    private int volume;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    private OrderJpaEntity(UUID id, int locationX, int locationY, int volume, OrderStatus status) {
        this.id = id;
        this.locationX = locationX;
        this.locationY = locationY;
        this.volume = volume;
        this.status = status;
    }

    static OrderJpaEntity fromDomain(Order order) {
        return new OrderJpaEntity(
                order.getId(),
                order.getLocation().getX(),
                order.getLocation().getY(),
                order.getVolume().getValue(),
                order.getStatus());
    }

    Order toDomain() {
        return Order.restore(id, Location.of(locationX, locationY), Volume.of(volume), status);
    }
}
