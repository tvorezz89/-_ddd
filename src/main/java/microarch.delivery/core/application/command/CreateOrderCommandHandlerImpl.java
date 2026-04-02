package microarch.delivery.core.application.command;

import libs.ddd.DomainEventPublisher;
import libs.errs.Error;
import libs.errs.Result;
import lombok.RequiredArgsConstructor;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.model.order.events.OrderCreatedDomainEvent;
import microarch.delivery.core.ports.GeoClient;
import microarch.delivery.core.ports.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateOrderCommandHandlerImpl implements CreateOrderCommandHandler {


    private final OrderRepository orderRepository;
    private final GeoClient geoClient;
    private final DomainEventPublisher domainEventPublisher;

    @Override
    public Result<UUID, Error> handle(CreateOrderCommand command) {

        var location = geoClient.getGeo(command.getAddress());

        var orderResult = Order.create(command.getOrderID(), location, command.getVolume());
        if (orderResult.isFailure()) {
            return Result.failure(orderResult.getError());
        }

        var order = orderRepository.save(orderResult.getValue());
        order.raiseDomainEvent(new OrderCreatedDomainEvent(order));
        domainEventPublisher.publish(List.of(order));

        return Result.success(order.getId());
    }


}
