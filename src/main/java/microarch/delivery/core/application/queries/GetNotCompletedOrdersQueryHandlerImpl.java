package microarch.delivery.core.application.queries;

import microarch.delivery.core.ports.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetNotCompletedOrdersQueryHandlerImpl implements GetNotCompletedOrdersQueryHandler {

    private final OrderRepository orderRepository;

    public GetNotCompletedOrdersQueryHandlerImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public List<OrderDto> handle(GetNotCompletedOrdersQuery query) {
        return orderRepository.findAllNotCompleted().stream()
                .map(order -> new OrderDto(order.getId(), order.getLocation().getX(), order.getLocation().getY()))
                .toList();
    }
}
