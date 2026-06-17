package microarch.delivery.core.domain.services;

import libs.errs.Error;
import libs.errs.Result;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.model.order.OrderStatus;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
public final class OrderDispatcherDomainServiceImpl implements OrderDispatcherDomainService {

    @Override
    public Result<Courier, Error> dispatch(Order order, List<Courier> couriers) {
        Objects.requireNonNull(order, "order");
        Objects.requireNonNull(couriers, "couriers");

        if (order.getStatus() != OrderStatus.CREATED) {
            return Result.failure(Order.Errors.orderMustBeCreated());
        }

        var winner = couriers.stream().filter(courier -> courier.canAssign(order.getVolume()))
                .min(Comparator.comparingInt(courier -> courier.getLocation().distanceTo(order.getLocation())));

        if (winner.isEmpty()) {
            return Result.failure(Order.Errors.dispatchingFailed());
        }

        var courier = winner.get();
        var assignCourierResult = courier.assign(order.getId(), order.getVolume(), order.getLocation());
        if (assignCourierResult.isFailure()) {
            return Result.failure(assignCourierResult.getError());
        }

        var assignOrderResult = order.assign();
        if (assignOrderResult.isFailure()) {
            return Result.failure(assignOrderResult.getError());
        }

        return Result.success(courier);
    }
}
