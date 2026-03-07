package microarch.delivery.core.domain.service;

import libs.errs.UnitResult;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Order;
import org.springframework.stereotype.Service;

@Service
public class AssignOrderDomainServiceImpl implements AssignOrderDomainService {

    @Override
    public UnitResult<libs.errs.Error> assignOrder(Order order, Courier courier) {
        var takeOrderResult = courier.takeOrder(order);
        if (takeOrderResult.isFailure()) {
            return UnitResult.failure(takeOrderResult.getError());
        }
        var assignResult = order.assignToCourier(courier.getId());
        if (assignResult.isFailure()) {
            return UnitResult.failure(assignResult.getError());
        }
        return UnitResult.success();
    }

}
