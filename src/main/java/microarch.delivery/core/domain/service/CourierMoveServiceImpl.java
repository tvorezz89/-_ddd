package microarch.delivery.core.domain.service;

import libs.errs.Error;
import libs.errs.UnitResult;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Order;
import org.springframework.stereotype.Service;

@Service
public class CourierMoveServiceImpl implements CourierMoveService {

    @Override
    public UnitResult<Error> move(Order order, Courier courier) {
        if (order == null || courier == null) {
            throw new IllegalArgumentException("Income parameters is null");
        }

        var moveResult = courier.move(order.getLocation());
        if (moveResult.isFailure()) {
            return UnitResult.failure(moveResult.getError());
        }

        if (courier.getLocation().equals(order.getLocation())) {
            var completeResult = order.complete();
            var completeAnOrderResult = courier.completeAnOrder(order);
            if (completeResult.isFailure() || completeAnOrderResult.isFailure()) {
                throw new IllegalStateException("Fail to complete order with orderId: %s and courierId: %s"
                        .formatted(order.getId(), courier.getId()));
            }
        }

        return UnitResult.success();
    }
}
