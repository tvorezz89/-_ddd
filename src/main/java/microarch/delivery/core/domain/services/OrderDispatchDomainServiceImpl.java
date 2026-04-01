package microarch.delivery.core.domain.services;

import libs.errs.Error;
import libs.errs.Result;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Order;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class OrderDispatchDomainServiceImpl implements OrderDispatchDomainService {
    @Override
    public Result<Courier, Error> dispatchOrder(Order order, List<Courier> couriers) {
        Courier winner = couriers.stream().filter(courier -> courier.canTakeOrder(order))
                .min(Comparator.comparingInt(courier -> courier.getLocation().calculateDistance(order.getLocation())))
                .orElse(null);
        if (winner == null)
            return Result.failure(Error.of("no.courier", "Подходящий курьер не найден"));
        return winner.takeOrder(order);
    }
}
