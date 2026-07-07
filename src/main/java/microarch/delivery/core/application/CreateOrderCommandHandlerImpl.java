package microarch.delivery.core.application;

import libs.errs.Error;
import libs.errs.UnitResult;
import lombok.RequiredArgsConstructor;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.ports.GeoClient;
import microarch.delivery.core.ports.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateOrderCommandHandlerImpl implements CreateOrderCommandHandler {

    private final OrderRepository orderRepository;
    private final GeoClient geoClient;

    @Override
    @Transactional
    public UnitResult<Error> handle(CreateOrderCommand command) {
        var location = geoClient.getLocation(command.getStreet());
        var orderResult = Order.create(command.getOrderId(), location, command.getVolume());
        if (orderResult.isFailure()) {
            return UnitResult.failure(orderResult.getError());
        }

        orderRepository.add(orderResult.getValue());
        return UnitResult.success();
    }
}
