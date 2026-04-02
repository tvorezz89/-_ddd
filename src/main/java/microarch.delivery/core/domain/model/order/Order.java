package microarch.delivery.core.domain.model.order;

import jakarta.persistence.*;
import libs.ddd.Aggregate;
import libs.errs.*;
import libs.errs.Error;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import microarch.delivery.core.domain.Location;
import microarch.delivery.core.domain.Volume;
import microarch.delivery.core.domain.model.order.events.OrderCompletedDomainEvent;

import java.util.Objects;
import java.util.UUID;

@Getter
@Table(name = "orders")
@Entity
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
public class Order extends Aggregate<UUID> {

    @Embedded
    private final Location location;
    @Embedded
    private final Volume volume;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    @Column(name = "courier_id")
    private UUID courierId;

    private Order(UUID id, Location location, Volume volume) {
        super(id);
        this.location = location;
        this.volume = volume;
        this.status = OrderStatus.CREATED;
        this.courierId = null;
    }

    public static Result<Order, Error> create(UUID id, Location location, Volume volume) {
        var errors = Guard.combine(
                GeneralErrors.valueIsRequired(id, "id"),
                GeneralErrors.valueIsRequired(location, "location"),
                GeneralErrors.valueIsRequired(volume, "volume")
        );
        if (errors != null) Result.failure(errors);
        return Result.success(new Order(id, location, volume));
    }

    public static Order mustCreate(UUID id, Location location, Volume volume) {
        return create(id, location, volume).getValueOrThrow();
    }

    public UnitResult<Error> assignToCourier(UUID courierId) {
        Objects.requireNonNull(courierId, "courierId");
        if (!isCreated()) {
            return UnitResult.failure(Errors.canOnlyAssignCreatedOrder());
        }
        this.courierId = courierId;
        this.status = OrderStatus.ASSIGNED;
        return UnitResult.success();
    }

    public UnitResult<Error> complete() {
        if (!isAssigned()) {
            return UnitResult.failure(Errors.canOnlyCompleteAssignedOrder());
        }
        this.status = OrderStatus.COMPLETED;
        raiseDomainEvent(new OrderCompletedDomainEvent(this));
        return UnitResult.success();
    }

    public boolean isCreated() {
        return this.status == OrderStatus.CREATED;
    }

    public boolean isAssigned() {
        return this.status == OrderStatus.ASSIGNED;
    }

    public boolean isCompleted() {
        return this.status == OrderStatus.COMPLETED;
    }

    public static final class Errors {

        public static Error canOnlyAssignCreatedOrder() {
            return Error.of(
                    "order.can.only.assign.created",
                    "Назначить курьера можно только заказу со статусом CREATED"
            );
        }

        public static Error canOnlDispatchCreatedOrder() {
            return Error.of(
                    "order.can.only.dispatch.created",
                    "Распределить заказ между курьерами можно только со статусом CREATED"
            );
        }

        public static Error canOnlyCompleteAssignedOrder() {
            return Error.of(
                    "order.can.only.complete.assigned",
                    "Завершить можно только заказ со статусом ASSIGNED"
            );
        }
    }
}