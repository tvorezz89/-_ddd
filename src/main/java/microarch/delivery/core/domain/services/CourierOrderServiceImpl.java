package microarch.delivery.core.domain.services;

import libs.errs.Error;
import libs.errs.GeneralErrors;
import libs.errs.Result;
import libs.errs.UnitResult;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.model.order.OrderStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public final class CourierOrderServiceImpl implements CourierOrderService {

    @Override
    public Result<Courier, Error> dispatch(Order order, List<Courier> couriers) {
        if (order == null)
            return Result.failure(GeneralErrors.valueIsRequired("order"));
        if (couriers == null)
            return Result.failure(GeneralErrors.valueIsRequired("couriers"));

        if (order.getStatus() != OrderStatus.CREATED)
            return Result.failure(Order.Errors.orderHasWrongStatus(OrderStatus.CREATED, order.getStatus()));

        Courier bestCourier = null;
        int bestDistance = Integer.MAX_VALUE;
        for (var courier : couriers) {
            // null в списке — нарушение контракта (баг вызывающего кода), а не бизнес-ситуация:
            // fail-fast вместо молчаливого исключения из диспетчеризации
            Objects.requireNonNull(courier, "couriers must not contain null elements");

            // Переполненный курьер в диспетчеризации не участвует
            if (!courier.canTakeOrder(order.getVolume()))
                continue;

            var distance = courier.getLocation().distanceTo(order.getLocation());
            if (distance < bestDistance) {
                bestDistance = distance;
                bestCourier = courier;
            }
        }

        if (bestCourier == null)
            return Result.failure(Errors.noAvailableCourier(order.getId()));

        var assignResult = assignOrder(bestCourier, order);
        if (assignResult.isFailure())
            return Result.failure(assignResult.getError());

        return Result.success(bestCourier);
    }

    @Override
    public UnitResult<Error> assignOrder(Courier courier, Order order) {
        if (courier == null)
            return UnitResult.failure(GeneralErrors.valueIsRequired("courier"));
        if (order == null)
            return UnitResult.failure(GeneralErrors.valueIsRequired("order"));

        if (!courier.canTakeOrder(order.getVolume()))
            return UnitResult.failure(Courier.Errors.courierCannotTakeOrder(courier.getName(), order.getId()));

        var assignResult = order.assign();
        if (assignResult.isFailure())
            return assignResult;

        return courier.addAssignment(order.getId(), order.getVolume());
    }

    @Override
    public UnitResult<Error> completeOrder(Courier courier, Order order) {
        if (courier == null)
            return UnitResult.failure(GeneralErrors.valueIsRequired("courier"));
        if (order == null)
            return UnitResult.failure(GeneralErrors.valueIsRequired("order"));

        if (!courier.hasAssignment(order.getId()))
            return UnitResult.failure(Courier.Errors.assignmentNotFound(courier.getName(), order.getId()));

        if (!courier.isWithinReach(order.getLocation()))
            return UnitResult.failure(Courier.Errors.courierTooFarFromOrder(courier.getName(), order.getId()));

        var completeResult = order.complete();
        if (completeResult.isFailure())
            return completeResult;

        return courier.removeAssignment(order.getId());
    }

    public static class Errors {
        public static Error noAvailableCourier(UUID orderId) {
            return Error.of("dispatch.no.available.courier",
                    String.format("Диспетчеризация не состоялась: нет свободного курьера для заказа %s", orderId));
        }
    }
}
