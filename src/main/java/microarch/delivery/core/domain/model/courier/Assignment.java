package microarch.delivery.core.domain.model.courier;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import libs.ddd.BaseEntity;
import libs.errs.Error;
import libs.errs.GeneralErrors;
import libs.errs.GeneralErrors;
import libs.errs.Guard;
import libs.errs.Result;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import microarch.delivery.core.domain.model.Volume;

import java.util.UUID;

/**
 * Assignment — назначение заказа курьеру (часть агрегата Courier). Хранит Id заказа, который курьер взял в работу.
 */
@Entity
@Table(name = "assignments")
@Getter
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
public final class Assignment extends BaseEntity<UUID> {
    @Column(name = "order_id", nullable = false)
    private final UUID orderId;

    @Embedded
    private final Volume orderVolume;

    private Assignment(UUID orderId, Volume orderVolume) {
        super(UUID.randomUUID());
        this.orderId = orderId;
        this.orderVolume = orderVolume;
    }

    public static Result<Assignment, Error> create(UUID orderId, Volume orderVolume) {
        var err = Guard.combine(Guard.againstNullOrEmpty(orderId, "orderId"),
                orderVolume == null ? GeneralErrors.valueIsRequired("orderVolume") : null);
        if (err != null)
            return Result.failure(err);

        return Result.success(new Assignment(orderId, orderVolume));
    }

    public static Assignment mustCreate(UUID orderId, Volume orderVolume) {
        return create(orderId, orderVolume).getValueOrThrow();
    }
}
