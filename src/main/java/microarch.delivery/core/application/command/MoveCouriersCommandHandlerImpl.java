package microarch.delivery.core.application.command;

import jakarta.transaction.Transactional;
import libs.ddd.DomainEventPublisher;
import libs.errs.UnitResult;
import libs.errs.Error;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.service.CompleteOrderDomainService;
import microarch.delivery.core.ports.CourierRepository;
import microarch.delivery.core.ports.OrderRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MoveCouriersCommandHandlerImpl implements MoveCouriersCommandHandler {

    private final OrderRepository orderRepository;
    private final CourierRepository courierRepository;
    private final CompleteOrderDomainService completeOrderDomainService;
    private final DomainEventPublisher domainEventPublisher;

    @Override
    @Transactional
    public UnitResult<Error> handle() {
        Iterable<Order> assignedOrders = orderRepository.findAllAssigned();

        for (Order order : assignedOrders) {
            var courierOpt = courierRepository.findById(order.getCourierId());
            if (courierOpt.isEmpty()) {
                log.warn("No courier for order with {}", order.getId());
                throw new IllegalArgumentException("Courier not found for order");
            }

            Courier courier = courierOpt.get();
            var moveResult = courier.move(order.getLocation());
            if (moveResult.isFailure()) {
                return UnitResult.failure(moveResult.getError());
            }
            if (courier.hasReached(order.getLocation())) {
                var completeResult = completeOrderDomainService.completeOrder(order, courier);
                if (completeResult.isFailure()) {
                    return UnitResult.failure(completeResult.getError());
                }
            }

            courierRepository.update(courier);
            orderRepository.update(order);
        }
        domainEventPublisher.publish(assignedOrders);

        return UnitResult.success();
    }

}
