package microarch.delivery.core.application.commands;

import libs.errs.Error;
import libs.errs.Result;
import libs.errs.UnitResult;
import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Volume;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.ports.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
public class CreateOrderCommandHandlerImpl implements CreateOrderCommandHandler {

    private final OrderRepository orderRepository;

    public CreateOrderCommandHandlerImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public UnitResult<Error> handle(CreateOrderCommand command) {
        var random = ThreadLocalRandom.current();
        var location = Location.create(random.nextInt(1, 11), random.nextInt(1, 11)).getValue();

        Result<Order, Error> order = Order.create(command.getOrderId(), location,
                Volume.create(command.getVolume()).getValue());
        if (order.isFailure())
            return UnitResult.failure(order.getError());

        orderRepository.save(order.getValue());

        return UnitResult.success();
    }
}
