package microarch.delivery.core.application;

import libs.errs.Error;
import libs.errs.GeneralErrors;
import libs.errs.UnitResult;
import lombok.RequiredArgsConstructor;
import microarch.delivery.core.ports.CourierRepository;
import microarch.delivery.core.ports.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CompleteOrderCommandHandlerImpl implements CompleteOrderCommandHandler {

    private final CourierRepository courierRepository;
    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public UnitResult<Error> handle(CompleteOrderCommand command) {
        var courierOpt = courierRepository.findById(command.getCourierId());
        if (courierOpt.isEmpty()) {
            return UnitResult.failure(GeneralErrors.notFound("courier", command.getCourierId()));
        }

        var orderOpt = orderRepository.findById(command.getOrderId());
        if (orderOpt.isEmpty()) {
            return UnitResult.failure(GeneralErrors.notFound("order", command.getOrderId()));
        }

        var courier = courierOpt.get();
        var completeAssignmentResult = courier.completeAssignment(command.getOrderId());
        if (completeAssignmentResult.isFailure()) {
            return completeAssignmentResult;
        }

        var order = orderOpt.get();
        var completeOrderResult = order.complete();
        if (completeOrderResult.isFailure()) {
            return completeOrderResult;
        }

        courierRepository.update(courier);
        orderRepository.update(order);
        return UnitResult.success();
    }
}
