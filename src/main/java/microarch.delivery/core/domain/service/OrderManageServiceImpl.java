package microarch.delivery.core.domain.service;

import libs.errs.Error;
import libs.errs.GeneralErrors;
import libs.errs.Result;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.model.order.OrderStatus;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class OrderManageServiceImpl implements OrderManageService {

    private static final Error COURIER_NOT_FOUND = Error.of("Proper.courier.not.found", "Proper courier is not found");

    @Override
    public Result<Courier, Error> manage(Order order, List<Courier> couriers) {
        if (order == null) {
            throw new IllegalArgumentException("order is null");
        }
        if (couriers == null) {
            throw new IllegalArgumentException("couriers is null");
        }
        if (order.getStatus() != OrderStatus.CREATED) {
            return Result.failure(GeneralErrors.valueIsInvalid("order status", order.getStatus()));
        }

        var foundCourierOpt = couriers.stream()
                .filter(courier -> courier.canTakeAnOrder(order).getValue())
                .min(Comparator.comparing(courier -> courier.movesCount(order.getLocation()).getValue()));

        if (foundCourierOpt.isEmpty()) {
            return Result.failure(COURIER_NOT_FOUND);
        }

        var foundCourier = foundCourierOpt.get();

        var manageOrder = order.manageToCourier(foundCourier.getId());
        if (manageOrder.isFailure()) {
            return Result.failure(manageOrder.getError());
        }

        var takeOrder = foundCourier.takeAnOrder(order);
        if (takeOrder.isFailure()) {
            return Result.failure(takeOrder.getError());
        }

        return Result.success(foundCourier);
    }
}
