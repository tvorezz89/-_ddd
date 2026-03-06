package microarch.delivery.core.domain.model.order;

import libs.ddd.Aggregate;
import libs.errs.*;
import libs.errs.Error;
import lombok.Getter;
import microarch.delivery.core.domain.Location;
import microarch.delivery.core.domain.Volume;

import java.util.Objects;
import java.util.UUID;

@Getter
public class Order extends Aggregate<UUID> {

    private final Location location;
    private final Volume volume;
    private OrderStatus status;
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

        public static Error canOnlyCompleteAssignedOrder() {
            return Error.of(
                    "order.can.only.complete.assigned",
                    "Завершить можно только заказ со статусом ASSIGNED"
            );
        }
    }
}