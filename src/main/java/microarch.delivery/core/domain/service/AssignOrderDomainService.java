package microarch.delivery.core.domain.service;

import libs.errs.UnitResult;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Order;

import javax.validation.constraints.NotNull;

public interface AssignOrderDomainService {

    UnitResult<libs.errs.Error> assignOrder(@NotNull Order order,@NotNull Courier courier);

}
