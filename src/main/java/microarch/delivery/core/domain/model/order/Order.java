package microarch.delivery.core.domain.model.order;

import jakarta.persistence.*;
import libs.ddd.Aggregate;
import libs.errs.Error;
import libs.errs.Result;
import libs.errs.UnitResult;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Volume;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends Aggregate<UUID> {

    @Embedded
    private Location location;

    @Embedded
    @AttributeOverride(
            name = "size",
            column = @Column(name = "volume", nullable = false)
    )
    private Volume volume;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderStatus status;

    private Order(UUID id, Location location, Volume volume, OrderStatus status) {
        super(id);
        this.location = location;
        this.volume = volume;
        this.status = status;
    }

    public static Result<Order, Error> create(UUID id, Location location, Volume volume) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(location, "location");

        var order = new Order(id, location, volume, OrderStatus.CREATED);
        return Result.success(order);
    }

    public static Order mustCreate(UUID id, Location location, Volume volume) {
        return create(id, location, volume).getValueOrThrow();
    }

    public UnitResult<Error> changeStatus(OrderStatus newStatus) {
        Objects.requireNonNull(newStatus, "newStatus");
        Result<OrderStatus, Error> changedStatus = this.status.change(newStatus);
        if (changedStatus.isFailure()) {
            return UnitResult.failure(Errors.illegalStatusChange(this.status, newStatus));
        }
        this.status = changedStatus.getValueOrThrow();
        return UnitResult.success();
    }

    public static final class Errors {

        public static Error illegalStatusChange(OrderStatus oldStatus, OrderStatus newStatus) {
            return Error.of(
                    "illegalStatusChange",
                    "Illegal status change, from " + oldStatus + " to " + newStatus
            );
        }
    }
}