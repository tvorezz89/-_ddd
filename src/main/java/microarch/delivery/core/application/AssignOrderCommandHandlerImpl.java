package microarch.delivery.core.application;

import libs.ddd.DomainEventPublisher;
import libs.errs.Error;
import libs.errs.GeneralErrors;
import libs.errs.UnitResult;
import lombok.RequiredArgsConstructor;
import microarch.delivery.core.domain.services.OrderDispatcherDomainService;
import microarch.delivery.core.ports.CourierRepository;
import microarch.delivery.core.ports.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssignOrderCommandHandlerImpl implements AssignOrderCommandHandler {
    private final OrderRepository orderRepository;
    private final CourierRepository courierRepository;
    private final OrderDispatcherDomainService dispatcher;
    private final DomainEventPublisher domainEventPublisher;

    @Override
    @Transactional
    public UnitResult<Error> handle(AssignOrderCommand command) {
        var orderOpt = orderRepository.findAnyCreated();
        if (orderOpt.isEmpty()) {
            return UnitResult.failure(GeneralErrors.notFound("order", "created"));
        }

        var order = orderOpt.get();
        var dispatchResult = dispatcher.dispatch(order, courierRepository.findAll());
        if (dispatchResult.isFailure()) {
            return UnitResult.failure(dispatchResult.getError());
        }

        orderRepository.update(order);
        courierRepository.update(dispatchResult.getValue());
        domainEventPublisher.publish(List.of(order));
        return UnitResult.success();
    }
}
