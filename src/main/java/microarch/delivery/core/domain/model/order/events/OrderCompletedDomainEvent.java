package microarch.delivery.core.domain.model.order.events;

import java.util.UUID;

import libs.ddd.DomainEvent;
import lombok.Getter;

@Getter
public final class OrderCompletedDomainEvent extends DomainEvent {
    private final UUID orderId;

    public OrderCompletedDomainEvent(UUID orderId) {
        super(orderId);
        this.orderId = orderId;
    }
}
