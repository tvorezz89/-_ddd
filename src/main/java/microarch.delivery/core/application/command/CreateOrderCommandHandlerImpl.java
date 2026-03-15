package microarch.delivery.core.application.command;

import libs.errs.Error;
import libs.errs.UnitResult;
import lombok.RequiredArgsConstructor;
import microarch.delivery.core.domain.kernel.Location;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.ports.OrderRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateOrderCommandHandlerImpl implements CreateOrderCommandHandler {

    private final OrderRepository orderRepository;

    @Override
    public UnitResult<Error> handle(CreateOrderCommand command) {
        // todo: убрать, когда будет реализован geo сервис
        var mockLocation = Location.create(5, 5);

        var order = Order.create(command.getOrderId(), mockLocation.getValue(), command.getVolume());
        if (order.isFailure()) {
            return UnitResult.failure(order.getError());
        }

        orderRepository.add(order.getValue());
        return UnitResult.success();
    }
}
