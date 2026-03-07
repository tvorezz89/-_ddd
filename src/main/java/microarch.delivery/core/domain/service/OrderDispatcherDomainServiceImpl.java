package microarch.delivery.core.domain.service;

import libs.errs.Error;
import libs.errs.Result;
import lombok.RequiredArgsConstructor;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Order;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

import static microarch.delivery.core.domain.service.OrderDispatcherDomainServiceImpl.Errors.noCouriersCanDispatchOrder;

@Service
@RequiredArgsConstructor
public class OrderDispatcherDomainServiceImpl implements OrderDispatcherDomainService {


    private final AssignOrderDomainService assignOrderDomainService;

    public Result<Courier, Error> dispatch(Order order, List<Courier> couriers) {
        if (!order.isCreated()) {
            return Result.failure(Order.Errors.canOnlDispatchCreatedOrder());
        }
        if (couriers.isEmpty()) {
            return Result.failure(noCouriersCanDispatchOrder());
        }

        var optionalNearbyCourier = couriers.stream()
                .filter(courier -> courier.canTakeOrder(order.getVolume()))
                .min(Comparator.comparingInt(courier -> courier.calculateStepsToLocation(order.getLocation())));

        if (optionalNearbyCourier.isEmpty()) {
            return Result.failure(noCouriersCanDispatchOrder());
        }

        var nearbyCourier = optionalNearbyCourier.get();

        var assignResult = assignOrderDomainService.assignOrder(order, nearbyCourier);

        if (assignResult.isFailure()) {
            return Result.failure(assignResult.getError());
        }

        return Result.success(nearbyCourier);
    }

    public static final class Errors {

        public static Error noCouriersCanDispatchOrder() {
            return Error.of(
                    "no.courier.can.dispatch.order",
                    "Никто из предложенных курьеров не может взять заказ"
            );
        }
    }


}
