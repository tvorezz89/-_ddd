package microarch.delivery.core.application.commands;

import libs.errs.Error;
import libs.errs.GeneralErrors;
import libs.errs.UnitResult;
import microarch.delivery.core.ports.CourierRepository;
import microarch.delivery.core.ports.OrderRepository;
import org.springframework.stereotype.Service;

@Service
public class CompleteOrderCommandHandlerImpl implements CompleteOrderCommandHandler {

    private final CourierRepository courierRepository;
    private final OrderRepository orderRepository;

    public CompleteOrderCommandHandlerImpl(CourierRepository courierRepository, OrderRepository orderRepository) {
        this.courierRepository = courierRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public UnitResult<Error> handle(CompleteOrderCommand command) {
        var courier = courierRepository.findById(command.getCourierId()).orElse(null);
        if (courier == null)
            return UnitResult.failure(GeneralErrors.notFound("Courier", command.getCourierId()));

        var order = orderRepository.findById(command.getOrderId()).orElse(null);
        if (order == null)
            return UnitResult.failure(GeneralErrors.notFound("Order", command.getOrderId()));

        var finishResult = courier.finishAssignment(command.getOrderId());
        if (finishResult.isFailure())
            return UnitResult.failure(finishResult.getError());

        var completeResult = order.complete();
        if (completeResult.isFailure())
            return UnitResult.failure(completeResult.getError());

        courierRepository.save(courier);
        orderRepository.save(order);

        return UnitResult.success();
    }
}
