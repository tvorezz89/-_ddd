package microarch.delivery.core.domain.model.order;

import libs.ddd.Aggregate;
import libs.errs.DomainInvariantException;
import libs.errs.Error;
import libs.errs.GeneralErrors;
import libs.errs.Guard;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends Aggregate<UUID> {
    private Location location;
    private Volume volume;
    private OrderStatus status;

    private Order(UUID id, Location location, Volume volume, OrderStatus status) {
        super(id);
        validate(id, location, volume, status);
        this.location = location;
        this.volume = volume;
        this.status = status;
    }

    public static Order create(UUID id, Location location, Volume volume) {
        return new Order(id, location, volume, OrderStatus.CREATED);
    }

    public void assign() {
        if (status != OrderStatus.CREATED) {
            throw new DomainInvariantException(GeneralErrors.valueIsInvalid("status", status));
        }
        status = OrderStatus.ASSIGNED;
    }

    public void complete() {
        if (status != OrderStatus.ASSIGNED) {
            throw new DomainInvariantException(GeneralErrors.valueIsInvalid("status", status));
        }
        status = OrderStatus.COMPLETED;
    }

    private static void validate(UUID id, Location location, Volume volume, OrderStatus status) {
        Error.throwIf(Guard.againstNullOrEmpty(id, "id"));

        if (location == null) {
            throw new DomainInvariantException(GeneralErrors.valueIsRequired("location"));
        }

        if (volume == null) {
            throw new DomainInvariantException(GeneralErrors.valueIsRequired("volume"));
        }

        if (status != OrderStatus.CREATED) {
            throw new DomainInvariantException(GeneralErrors.valueIsInvalid("status", status));
        }
    }
}
