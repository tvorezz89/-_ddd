package microarch.delivery.core.domain.service;

import java.util.List;

import libs.errs.Error;
import libs.errs.GeneralErrors;
import libs.errs.Result;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.model.order.OrderStatus;
import org.springframework.stereotype.Service;

@Service
public class OrderDispatchServiceImpl implements OrderDispatchService {
    @Override
    public Result<Courier, Error> dispatch(Order order, List<Courier> couriers) {
        if (order == null) {
            return Result.failure(GeneralErrors.valueIsRequired("order"));
        }

        if (order.getStatus() != OrderStatus.CREATED) {
            return Result.failure(GeneralErrors.valueIsInvalid("order.status", order.getStatus()));
        }

        if (couriers == null || couriers.isEmpty()) {
            return Result.failure(Error.of("courier.not.available", "No available courier for dispatch"));
        }

        Courier winner = null;
        Integer winnerDistance = null;
        for (var courier : couriers) {
            if (!courier.canTakeAdditionalOrder(order.getVolume())) {
                continue;
            }

            var distance = courier.distanceTo(order.getLocation());
            if (winnerDistance == null || distance < winnerDistance) {
                winner = courier;
                winnerDistance = distance;
            }
        }

        if (winner == null) {
            return Result.failure(Error.of("courier.not.available", "No available courier for dispatch"));
        }

        winner.assignOrder(order.getId(), order.getVolume(), order.getLocation());
        order.assign();
        return Result.success(winner);
    }
}
