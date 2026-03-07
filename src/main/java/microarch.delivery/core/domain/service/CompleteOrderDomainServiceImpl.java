package microarch.delivery.core.domain.service;

import libs.errs.Error;
import libs.errs.UnitResult;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Order;
import org.springframework.stereotype.Service;

@Service
public class CompleteOrderDomainServiceImpl implements CompleteOrderDomainService {
    @Override
    public UnitResult<Error> completeOrder(Order order, Courier courier) {
        var courierCompleteOrderResult = courier.completeOrder(order);
        if (courierCompleteOrderResult.isFailure()) {
            return UnitResult.failure(courierCompleteOrderResult.getError());
        }
        var orderCompleteResult = order.complete();
        if (orderCompleteResult.isFailure()) {
            return UnitResult.failure(orderCompleteResult.getError());
        }
        return UnitResult.success();
    }
}
