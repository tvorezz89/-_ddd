package microarch.delivery.core.domain.model.order.events;

import libs.ddd.DomainEvent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import microarch.delivery.core.domain.model.order.Order;

import java.util.UUID;
import microarch.delivery.core.domain.Location;
import microarch.delivery.core.domain.Volume;
import microarch.delivery.core.domain.model.order.OrderStatus;

@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
@Getter
public class OrderCreatedDomainEvent extends DomainEvent {

    private final UUID orderId;
    private final Location location;
    private final Volume volume;
    private final OrderStatus status;

    public OrderCreatedDomainEvent(Order order) {
        super(order);
        this.orderId = order.getId();
        this.location = order.getLocation();
        this.volume = order.getVolume();
        this.status = order.getStatus();
    }
}
