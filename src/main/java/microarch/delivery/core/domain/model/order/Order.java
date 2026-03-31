package microarch.delivery.core.domain.model.order;

import libs.ddd.Aggregate;
import libs.errs.Error;
import libs.errs.Guard;
import libs.errs.Result;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Volume;

import java.util.Objects;
import java.util.UUID;

@Getter
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Order extends Aggregate<UUID> {

    private Location location;
    private Volume volume;
    private OrderStatus status;

    private Order(UUID id, Location location, Volume volume) {
        super.id = id;
        this.location = location;
        this.volume = volume;
    }

    public static Result<Order, Error> create(UUID id, Location location, Volume volume) {
        var err = Guard.againstNullOrEmpty(id, "id");
        if (err != null)
            return Result.failure(err);

        Objects.requireNonNull(location, "location");
        Objects.requireNonNull(volume, "volume");

        Order order = new Order(id, location, volume);
        order.status = OrderStatus.CREATED;

        return Result.success(order);
    }

    public Result<Order, Error> assign() {
        if (!this.status.equals(OrderStatus.CREATED)) {
            return Result.failure(Errors.orderShouldBeInCreatedStatus());
        }
        this.status = OrderStatus.ASSIGNED;
        return Result.success(this);
    }

    public Result<Order, Error> complete() {
        if (!this.status.equals(OrderStatus.ASSIGNED)) {
            return Result.failure(Errors.orderShouldBeInAssignedStatus());
        }
        this.status = OrderStatus.COMPLETED;
        return Result.success(this);
    }

    public static class Errors {
        public static Error orderShouldBeInCreatedStatus() {
            return Error.of("order.is.not.created.status", "Заказ должен быть в статусе Created");
        }

        public static Error orderShouldBeInAssignedStatus() {
            return Error.of("order.is.not.assigned.status", "Заказ должен быть в статусе Created");
        }

    }

}
