package microarch.delivery.core.domain.model.order.events;

import libs.ddd.DomainEvent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import microarch.delivery.core.domain.kernel.Volume;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.model.order.OrderStatus;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class OrderCreatedDomainEvent extends DomainEvent {

    private final UUID orderId;
    private final Volume volume;
    private final OrderStatus status;
    private final UUID courierId;

    public OrderCreatedDomainEvent(Order order) {
        super(order);
        this.orderId = order.getId();
        this.volume = order.getVolume();
        this.status = order.getStatus();
        this.courierId = order.getCourierId();
    }
}
