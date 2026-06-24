package microarch.delivery.core.application;

import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.services.OrderDispatcherDomainService;
import microarch.delivery.core.ports.CourierRepository;
import microarch.delivery.core.ports.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class DispatchOrderUseCase {
    private final OrderRepository orderRepository;
    private final CourierRepository courierRepository;
    private final OrderDispatcherDomainService dispatcher;

    public DispatchOrderUseCase(OrderRepository orderRepository, CourierRepository courierRepository,
            OrderDispatcherDomainService dispatcher) {
        this.orderRepository = orderRepository;
        this.courierRepository = courierRepository;
        this.dispatcher = dispatcher;
    }

    @Transactional
    public void dispatch(Order order, List<Courier> couriers) {
        Objects.requireNonNull(order, "order");
        Objects.requireNonNull(couriers, "couriers");

        var courier = dispatcher.dispatch(order, couriers).getValueOrThrow();

        orderRepository.update(order);
        courierRepository.update(courier);
    }
}
