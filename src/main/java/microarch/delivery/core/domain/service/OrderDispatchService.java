package microarch.delivery.core.domain.service;

import java.util.List;

import libs.errs.Error;
import libs.errs.Result;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Order;

public interface OrderDispatchService {
    Result<Courier, Error> dispatch(Order order, List<Courier> couriers);
}
