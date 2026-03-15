package microarch.delivery.core.application.command;

import libs.errs.Error;
import libs.errs.UnitResult;
import lombok.RequiredArgsConstructor;
import microarch.delivery.core.domain.Location;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.ports.OrderRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateOrderCommandHandlerImpl implements CreateOrderCommandHandler {


    private final OrderRepository orderRepository;

    @Override
    public UnitResult<Error> handle(CreateOrderCommand command) {

        var locationResult = Location.create(5, 5); // todo implement integration with Geo
        if (locationResult.isFailure()) {
            return UnitResult.failure(locationResult.getError());
        }

        var orderResult = Order.create(command.getOrderID(), locationResult.getValue(), command.getVolume());
        if (orderResult.isFailure()) {
            return UnitResult.failure(orderResult.getError());
        }

        orderRepository.save(orderResult.getValue());

        return UnitResult.success();
    }


}
