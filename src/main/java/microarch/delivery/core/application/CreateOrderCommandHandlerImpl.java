package microarch.delivery.core.application;

import libs.errs.Error;
import libs.errs.UnitResult;
import lombok.RequiredArgsConstructor;
import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.ports.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class CreateOrderCommandHandlerImpl implements CreateOrderCommandHandler {

    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public UnitResult<Error> handle(CreateOrderCommand command) {
        var orderResult = Order.create(command.getOrderId(), randomLocation(), command.getVolume());
        if (orderResult.isFailure()) {
            return UnitResult.failure(orderResult.getError());
        }

        orderRepository.add(orderResult.getValue());
        return UnitResult.success();
    }

    private Location randomLocation() {
        var random = ThreadLocalRandom.current();
        return Location.mustCreate(random.nextInt(1, 11), random.nextInt(1, 11));
    }
}
