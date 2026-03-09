package microarch.delivery.core.domain.service;

import libs.errs.Error;
import libs.errs.UnitResult;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Order;

import javax.validation.constraints.NotNull;

public interface CompleteOrderDomainService {

    UnitResult<Error> completeOrder(@NotNull Order order,@NotNull Courier courier);

}
