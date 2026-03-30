package microarch.delivery.core.domain.model;

import libs.ddd.Aggregate;
import libs.errs.Error;
import libs.errs.GeneralErrors;
import libs.errs.Result;
import lombok.Getter;

import java.util.UUID;

@Getter
public class OrderStatus extends Aggregate<UUID> {

    private Status status;

    private OrderStatus() {}

    private OrderStatus(Status status) {
        this.status = status;
    }

    public static OrderStatus fromName(String statusName) {
        return new OrderStatus(Status.valueOf(statusName.trim().toUpperCase()));
    }

    @Override
    public UUID getId() {
        return id;
    }

    public Status getStatus() { return status; }
}

