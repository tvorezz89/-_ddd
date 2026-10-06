package microarch.delivery.core.domain.model.order;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import libs.ddd.Aggregate;
import libs.errs.Error;
import libs.errs.GeneralErrors;
import libs.errs.Guard;
import libs.errs.Result;
import libs.errs.UnitResult;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Volume;

import java.util.UUID;

/**
 * Order — агрегат «Заказ».
 * <p>
 * Бизнес-правила:
 * <ul>
 * <li>заказ создается с переданными Id, Location и Volume, статус при создании — CREATED;</li>
 * <li>заказ может перейти в статус ASSIGNED только из статуса CREATED;</li>
 * <li>заказ может перейти в статус COMPLETED только из статуса ASSIGNED.</li>
 * </ul>
 */
@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class Order extends Aggregate<UUID> {
    @Embedded
    private Location location;

    @Embedded
    private Volume volume;

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
        var err = Guard.combine(Guard.againstNullOrEmpty(id, "id"),
                location == null ? GeneralErrors.valueIsRequired("location") : null,
                volume == null ? GeneralErrors.valueIsRequired("volume") : null);
        if (err != null)
            return Result.failure(err);

        return Result.success(new Order(id, location, volume));
    }

    public static Order mustCreate(UUID id, Location location, Volume volume) {
        return create(id, location, volume).getValueOrThrow();
    }

    /**
     * Назначить заказ: CREATED -> ASSIGNED.
     */
    public UnitResult<Error> assign() {
        if (this.status != OrderStatus.CREATED)
            return UnitResult.failure(Errors.orderHasWrongStatus(OrderStatus.CREATED, this.status));

        this.status = OrderStatus.ASSIGNED;
        return UnitResult.success();
    }

    /**
     * Завершить заказ: ASSIGNED -> COMPLETED.
     */
    public UnitResult<Error> complete() {
        if (this.status != OrderStatus.ASSIGNED)
            return UnitResult.failure(Errors.orderHasWrongStatus(OrderStatus.ASSIGNED, this.status));

        this.status = OrderStatus.COMPLETED;
        return UnitResult.success();
    }

    public static class Errors {
        public static Error orderHasWrongStatus(OrderStatus expected, OrderStatus current) {
            return Error.of("order.has.wrong.status",
                    String.format("Неверный статус заказа: ожидается %s, текущий %s", expected, current));
        }
    }
}
