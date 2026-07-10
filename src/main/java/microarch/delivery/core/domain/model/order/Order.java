package microarch.delivery.core.domain.model.order;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import microarch.delivery.core.domain.model.order.events.OrderAssignedDomainEvent;
import microarch.delivery.core.domain.model.order.events.OrderCompletedDomainEvent;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
public final class Order extends Aggregate<UUID> {

    @Embedded
    @AttributeOverrides({ @AttributeOverride(name = "x", column = @Column(name = "location_x", nullable = false)),
            @AttributeOverride(name = "y", column = @Column(name = "location_y", nullable = false)) })
    private final Location location;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "volume", nullable = false))
    private final Volume volume;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    private Order(UUID id, Location location, Volume volume) {
        super(id);
        this.location = location;
        this.volume = volume;
        this.status = OrderStatus.CREATED;
    }

    public static Result<Order, Error> create(UUID id, Location location, Volume volume) {
        Objects.requireNonNull(location, "location");
        Objects.requireNonNull(volume, "volume");

        var err = Guard.againstNullOrEmpty(id, "id");
        if (err != null) {
            return Result.failure(err);
        }

        return Result.success(new Order(id, location, volume));
    }

    public static Order mustCreate(UUID id, Location location, Volume volume) {
        return create(id, location, volume).getValueOrThrow();
    }

    public UnitResult<Error> assign() {
        if (this.status != OrderStatus.CREATED) {
            return UnitResult.failure(Errors.orderMustBeCreated());
        }

        this.status = OrderStatus.ASSIGNED;
        raiseDomainEvent(new OrderAssignedDomainEvent(this));
        return UnitResult.success();
    }

    public UnitResult<Error> complete() {
        if (this.status != OrderStatus.ASSIGNED) {
            return UnitResult.failure(Errors.orderMustBeAssigned());
        }

        this.status = OrderStatus.COMPLETED;
        raiseDomainEvent(new OrderCompletedDomainEvent(this));
        return UnitResult.success();
    }

    public static final class Errors {
        public static Error orderMustBeCreated() {
            return Error.of("order.must.be.created", "Order must be in Created status");
        }

        public static Error orderMustBeAssigned() {
            return Error.of("order.must.be.assigned", "Order must be in Assigned status");
        }

        public static Error dispatchingFailed() {
            return Error.of("order.dispatching.failed", "Order dispatching failed");
        }
    }
}
