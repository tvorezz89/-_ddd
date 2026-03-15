package microarch.delivery.core.application.command;

import libs.errs.Error;
import libs.errs.UnitResult;
import lombok.RequiredArgsConstructor;
import microarch.delivery.core.domain.service.OrderManageService;
import microarch.delivery.core.ports.CourierRepository;
import microarch.delivery.core.ports.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AssignOrderToCourierCommandHandlerImpl implements AssignOrderToCourierCommandHandler {

    private static final Error CREATED_ORDER_NOT_FOUND = Error.of("created.order.not.found", "Created orders is empty");
    private final OrderRepository orderRepository;
    private final CourierRepository courierRepository;
    private final OrderManageService orderManageService;

    @Override
    @Transactional
    public UnitResult<Error> handle(AssignOrderToCourierCommand command) {
        var couriers = courierRepository.findAllFreeCouriers();
        return orderRepository.findAnyByStatusCreated().map(order -> {
            var courier = orderManageService.manage(order, couriers);
            if (courier.isFailure()) {
                throw new IllegalStateException("Unable to manage order to courier");
            }

            orderRepository.update(order);
            courierRepository.update(courier.getValue());

            return UnitResult.success();
        }).orElse(UnitResult.failure(CREATED_ORDER_NOT_FOUND));
    }
}
