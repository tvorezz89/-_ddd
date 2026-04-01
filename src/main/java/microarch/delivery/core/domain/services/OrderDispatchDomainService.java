package microarch.delivery.core.domain.services;

import libs.errs.Error;
import libs.errs.Result;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Order;

import java.util.List;

public interface OrderDispatchDomainService {
    Result<Courier, Error> dispatchOrder(Order order, List<Courier> couriers);
}
