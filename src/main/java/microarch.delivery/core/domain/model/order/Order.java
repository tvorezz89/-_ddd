package microarch.delivery.core.domain.model.order;

import jakarta.persistence.*;
import libs.ddd.Aggregate;
import libs.errs.Error;
import libs.errs.Result;
import libs.errs.UnitResult;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import microarch.delivery.core.domain.kernel.Location;
import microarch.delivery.core.domain.kernel.Volume;
import microarch.delivery.core.domain.model.order.events.OrderCompletedDomainEvent;
import microarch.delivery.core.domain.model.order.events.OrderCreatedDomainEvent;

import java.util.UUID;

@Table(name = "orders")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends Aggregate<UUID> {

    @Embedded
    private Location location;

    @Embedded
    private Volume volume;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(name = "courier_id")
    private UUID courierId;

    private Order(UUID id, Location location, Volume volume) {
        this.id = id;
        this.location = location;
        this.volume = volume;
        this.status = OrderStatus.CREATED;

        raiseDomainEvent(new OrderCreatedDomainEvent(this));
    }

    public static Result<Order, Error> create(UUID id, Location location, Volume volume) {
        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }
        if (location == null) {
            throw new IllegalArgumentException("location is null");
        }
        if (volume == null) {
            throw new IllegalArgumentException("volume is null");
        }
        return Result.success(new Order(id, location, volume));
    }

    public UnitResult<Error> manageToCourier(UUID courierId) {
        if (courierId == null) {
            throw new IllegalArgumentException("courierId is null");
        }
        if (status != OrderStatus.CREATED) {
            return UnitResult.failure(INVALID_CREATED_STATUS_TO_MANAGE_COURIER);
        }
        if (getCourierId() != null) {
            return UnitResult.failure(COURIER_IS_ALREADY_MANAGED);
        }

        changeStatus(OrderStatus.CREATED);
        this.courierId = courierId;

        return UnitResult.success();
    }

    public UnitResult<Error> complete() {
        if (status != OrderStatus.ASSIGNED) {
            return UnitResult.failure(INVALID_ASSIGNED_STATUS_TO_MANAGE_COURIER);
        }
        if (getCourierId() == null) {
            return UnitResult.failure(COURIER_IS_NOT_MANAGED);
        }

        changeStatus(OrderStatus.ASSIGNED);

        raiseDomainEvent(new OrderCompletedDomainEvent(this));
        return UnitResult.success();
    }

    private void changeStatus(OrderStatus status) {
        switch (status) {
        case CREATED -> this.status = OrderStatus.ASSIGNED;
        case ASSIGNED -> this.status = OrderStatus.COMPLETED;
        case COMPLETED -> throw new IllegalStateException("Unable to change status on completed order");
        }
    }

    private static final Error INVALID_CREATED_STATUS_TO_MANAGE_COURIER = Error.of("invalid.status.to.manage.courier",
            "Unable to manage courier, status is not CREATED");
    private static final Error INVALID_ASSIGNED_STATUS_TO_MANAGE_COURIER = Error.of("invalid.status.to.finish.order",
            "Unable to manage courier, status is not ASSIGNED");
    private static final Error COURIER_IS_ALREADY_MANAGED = Error.of("courier.already.managed",
            "Courier is already managed to order");
    private static final Error COURIER_IS_NOT_MANAGED = Error.of("courier.is.not.managed",
            "Courier is not managed to order");
}
