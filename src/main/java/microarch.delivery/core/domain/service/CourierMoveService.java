package microarch.delivery.core.domain.service;

import libs.errs.Error;
import libs.errs.UnitResult;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Order;

public interface CourierMoveService {

    UnitResult<Error> move(Order order, Courier courier);
}
