package microarch.delivery.core.domain.service;

import libs.errs.Error;
import libs.errs.Result;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Order;

import java.util.List;

public interface OrderManageService {

    Result<Courier, Error> manage(Order order, List<Courier> couriers);
}
