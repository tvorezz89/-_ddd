package microarch.delivery.core.domain.model.aggregate;

import libs.ddd.Aggregate;
import libs.errs.Error;
import libs.errs.GeneralErrors;
import libs.errs.Result;
import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.OrderStatus;
import microarch.delivery.core.domain.model.Status;
import microarch.delivery.core.domain.model.courier.Volume;

import java.util.UUID;

public class Order extends Aggregate<UUID> {
    private Location location;
    private OrderStatus status;
    private UUID courierId;
    private Volume volume;

    private Order() {
    }

    private Order(UUID id, Location location, Volume volume) {
        this.id = id;
        this.location = location;
        this.volume = volume;
        this.status = OrderStatus.fromName("CREATED");
    }

    public Result<Order, Error> create(UUID id, Location location, Volume volume) {
        if (id == null)
            return Result.failure(GeneralErrors.valueIsRequired("id"));
        if (location == null)
            return Result.failure(GeneralErrors.valueIsRequired("location"));
        if (volume == null)
            return Result.failure(GeneralErrors.valueIsRequired("volume"));

        return Result.success(new Order(id, location, volume));
    }

    public Result<Boolean, Error> assign(UUID courierId) {
        if (status.getStatus() == Status.COMPLETED)
            return Result.failure(Errors.orderIsCompleted());
        this.courierId = courierId;
        return Result.success(Boolean.TRUE);
    }

    public Result<Boolean, Error> complete() {
        if (status.getStatus() != Status.ASSIGNED)
            return Result.failure(Errors.orderWasNotAssigned());
        this.status = OrderStatus.fromName("COMPLETED");

        return Result.success(Boolean.TRUE);
    }

    @Override
    public UUID getId() {
        return id;
    }

    private static class Errors {
        public static Error orderIsCompleted() {
            return Error.of(Order.class.getSimpleName().toLowerCase() + "has.been.completed", "Заказ уже завершён");
        }

        public static Error orderWasNotAssigned() {
            return Error.of(Order.class.getSimpleName().toLowerCase() + "was.not.assigned",
                    "Заказ не был назначен на курьера");
        }
    }

    public Volume getVolume() {
        return volume;
    }
}
