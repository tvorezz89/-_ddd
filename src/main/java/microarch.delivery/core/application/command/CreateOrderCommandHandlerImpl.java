package microarch.delivery.core.application.command;

import microarch.delivery.core.ports.DomainEventPublisher;
import libs.errs.Error;
import libs.errs.UnitResult;
import lombok.RequiredArgsConstructor;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.ports.GeoClient;
import microarch.delivery.core.ports.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CreateOrderCommandHandlerImpl implements CreateOrderCommandHandler {

    private final OrderRepository orderRepository;
    private final GeoClient geoClient;
    private final DomainEventPublisher eventPublisher;

    @Transactional
    @Override
    public UnitResult<Error> handle(CreateOrderCommand command) {
        var location = geoClient.getGeo(command.getStreet());

        var order = Order.create(command.getOrderId(), location.getValue(), command.getVolume());
        if (order.isFailure()) {
            return UnitResult.failure(order.getError());
        }

        orderRepository.add(order.getValue());

        eventPublisher.publish(List.of(order.getValue()));
        return UnitResult.success();
    }
}
