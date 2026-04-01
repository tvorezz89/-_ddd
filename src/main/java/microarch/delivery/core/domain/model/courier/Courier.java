package microarch.delivery.core.domain.model.courier;

import libs.ddd.Aggregate;
import libs.errs.Error;
import libs.errs.Guard;
import libs.errs.Result;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import microarch.delivery.core.domain.model.Assignment;
import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Volume;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.model.order.OrderStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Courier extends Aggregate<UUID> {

    private String name;
    private Location location;
    private Volume maxVolume;
    private List<Assignment> assignments;

    private Courier(String name, Location location) {
        super.id = UUID.randomUUID();
        this.name = name;
        this.location = location;
        this.maxVolume = Volume.create(20).getValue();
        this.assignments = new ArrayList<>();
    }

    public static Result<Courier, Error> create(String name, Location location) {
        var err = Guard.againstNullOrEmpty(name, "name");
        if (err != null)
            return Result.failure(err);

        Objects.requireNonNull(location, "location");

        Courier courier = new Courier(name, location);

        return Result.success(courier);
    }

    public Result<Courier, Error> takeOrder(Order order) {
        if (!canTakeOrder(order)) {
            return Result.failure(Errors.cantTakeOrder());
        }
        var assignment = Assignment.create(order.getId(), order.getVolume(), order.getLocation());
        if (assignment.isFailure()) {
            return Result.failure(assignment.getError());
        }

        this.assignments.add(assignment.getValue());
        return Result.success(this);
    }

    public Result<Courier, Error> finishAssignment(UUID orderId) {
        var assignment = this.assignments.stream().filter(a -> a.getOrderId().equals(orderId)).findFirst().orElse(null);
        if (assignment == null) {
            return Result.failure(Errors.assignmentNotFound());
        }
        var result = assignment.finish(this.location);
        if (result.isFailure()) {
            return Result.failure(result.getError());
        }
        this.assignments.remove(assignment);
        return Result.success(this);
    }

    public void changeLocation(Location location) {
        this.location = location;
    }

    private boolean canTakeOrder(Order order) {
        return this.assignments.stream().map(Assignment::getVolume).mapToInt(Volume::getVolume).sum()
                + order.getVolume().getVolume() <= this.maxVolume.getVolume();
    }

    public static class Errors {
        public static Error cantTakeOrder() {
            return Error.of("courier.can.not.take.order", "Не может взять заказ");
        }

        public static Error assignmentNotFound() {
            return Error.of("courier.assignment.not.found", "Назначение не найдено");
        }

    }
}
