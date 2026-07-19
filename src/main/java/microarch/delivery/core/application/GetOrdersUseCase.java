package microarch.delivery.core.application;

import java.util.List;

import microarch.delivery.core.ports.OrderRepository;
import org.springframework.stereotype.Service;

@Service
public class GetOrdersUseCase {
    private final OrderRepository orders;

    public GetOrdersUseCase(OrderRepository orders1) {
        orders = orders1;
    }

    public List<OrderReadModel> execute() {
        return orders.getActive().stream()
                .map(order -> new OrderReadModel(order.getId(), order.getLocation().getX(), order.getLocation().getY()))
                .toList();
    }
}
