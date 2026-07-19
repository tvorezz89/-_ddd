package microarch.delivery.core.application;

import java.util.UUID;

import libs.ddd.DomainEventPublisher;
import libs.errs.DomainInvariantException;
import libs.errs.GeneralErrors;
import microarch.delivery.core.ports.CourierRepository;
import microarch.delivery.core.ports.OrderRepository;
import microarch.delivery.core.ports.UnitOfWork;
import org.springframework.stereotype.Service;

@Service
public class CompleteOrderUseCase {
    private final CourierRepository couriers;
    private final OrderRepository orders;
    private final UnitOfWork unitOfWork;
    private final DomainEventPublisher domainEventPublisher;

    public CompleteOrderUseCase(CourierRepository couriers1, OrderRepository orders1, UnitOfWork unitOfWork1,
            DomainEventPublisher domainEventPublisher1) {
        couriers = couriers1;
        orders = orders1;
        unitOfWork = unitOfWork1;
        domainEventPublisher = domainEventPublisher1;
    }

    public void execute(UUID courierId, UUID orderId) {
        unitOfWork.execute(() -> {
            var courier = couriers.getById(courierId)
                    .orElseThrow(() -> new DomainInvariantException(GeneralErrors.notFound("courier", courierId)));
            var order = orders.getById(orderId)
                    .orElseThrow(() -> new DomainInvariantException(GeneralErrors.notFound("order", orderId)));
            courier.completeAssignment(orderId);
            order.complete();
            couriers.update(courier);
            orders.update(order);
            domainEventPublisher.publish(java.util.List.of(order));
        });
    }
}
