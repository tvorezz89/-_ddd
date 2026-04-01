package microarch.delivery.core.application.commands;

import libs.errs.Error;
import libs.errs.GeneralErrors;
import libs.errs.UnitResult;
import microarch.delivery.core.domain.services.OrderDispatchDomainService;
import microarch.delivery.core.ports.CourierRepository;
import microarch.delivery.core.ports.OrderRepository;
import org.springframework.stereotype.Service;

@Service
public class AssignOrderCommandHandlerImpl implements AssignOrderCommandHandler {

    private final OrderRepository orderRepository;
    private final CourierRepository courierRepository;
    private final OrderDispatchDomainService dispatchService;

    public AssignOrderCommandHandlerImpl(OrderRepository orderRepository, CourierRepository courierRepository,
            OrderDispatchDomainService dispatchService) {
        this.orderRepository = orderRepository;
        this.courierRepository = courierRepository;
        this.dispatchService = dispatchService;
    }

    @Override
    public UnitResult<Error> handle(AssignOrderCommand command) {
        var order = orderRepository.getNewOrder().orElse(null);
        if (order == null)
            return UnitResult.failure(GeneralErrors.notFound("Order", "new"));

        var couriers = courierRepository.findAll();

        var result = dispatchService.dispatchOrder(order, couriers);
        if (result.isFailure())
            return UnitResult.failure(result.getError());

        courierRepository.save(result.getValue());
        orderRepository.save(order);

        return UnitResult.success();
    }
}
