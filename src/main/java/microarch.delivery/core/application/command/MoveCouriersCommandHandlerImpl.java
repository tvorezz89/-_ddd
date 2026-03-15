package microarch.delivery.core.application.command;

import libs.errs.Error;
import libs.errs.UnitResult;
import lombok.RequiredArgsConstructor;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.service.CourierMoveService;
import microarch.delivery.core.ports.CourierRepository;
import microarch.delivery.core.ports.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MoveCouriersCommandHandlerImpl implements MoveCouriersCommandHandler {

    private final OrderRepository orderRepository;
    private final CourierRepository courierRepository;
    private final CourierMoveService courierMoveService;

    @Override
    @Transactional
    public UnitResult<Error> handle(MoveCouriersCommand command) {
        var orders = orderRepository.findAllByStatusAssigned();
        var courierIds = orders.stream().map(Order::getCourierId).toList();

        var couriersMap = courierRepository.findAllById(courierIds).stream()
                .collect(Collectors.toMap(Courier::getId, Function.identity()));

        orders.forEach(order -> {
            var courier = couriersMap.get(order.getCourierId());
            courierMoveService.move(order, courier);
        });

        orderRepository.updateAll(orders);
        courierRepository.updateAll(couriersMap.values());

        return UnitResult.success();
    }

    // todo: удалить
    // private static final Error ORDERS_NOT_FOUND = Error.of("orders.not.found", "Orders not found");
}
