package microarch.delivery.core.application.command;

import jakarta.transaction.Transactional;
import libs.errs.UnitResult;
import libs.errs.Error;
import lombok.RequiredArgsConstructor;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.service.OrderDispatcherDomainService;
import microarch.delivery.core.ports.CourierRepository;
import microarch.delivery.core.ports.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DispatchOrdersCommandHandlerImpl implements DispatchOrdersCommandHandler {

    private final OrderRepository orderRepository;
    private final CourierRepository courierRepository;
    private final OrderDispatcherDomainService orderDispatcherDomainService;

    @Override
    @Transactional
    public UnitResult<Error> handle() {
        var createdOrderOpt = orderRepository.findAnyCreated();
        if (createdOrderOpt.isEmpty()) {
            return UnitResult.failure(Error.of("no.any.created.orders", "zero created orders"));
        }

        Order order = createdOrderOpt.get();
        List<Courier> couriers = courierRepository.findAllFreeCouriers();

        if (couriers.isEmpty()) {
            return UnitResult.failure(Error.of("no.any.free.couriers", "all couriers are busy"));
        }

        var dispatchResult = orderDispatcherDomainService.dispatch(order, couriers);
        if (dispatchResult.isFailure()) {
            return UnitResult.failure(dispatchResult.getError());
        }

        var assignedCourier = dispatchResult.getValue();
        courierRepository.update(assignedCourier);
        orderRepository.update(order);

        return UnitResult.success();
    }
}
