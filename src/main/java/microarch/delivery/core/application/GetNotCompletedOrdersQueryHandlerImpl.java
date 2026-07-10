package microarch.delivery.core.application;

import lombok.RequiredArgsConstructor;
import microarch.delivery.core.ports.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetNotCompletedOrdersQueryHandlerImpl implements GetNotCompletedOrdersQueryHandler {
    private final OrderRepository orderRepository;

    @Override
    @Transactional(readOnly = true)
    public GetNotCompletedOrdersResponse handle(GetNotCompletedOrdersQuery query) {
        var orders = orderRepository.findAllNotCompleted().stream()
                .map(order -> new GetNotCompletedOrdersResponse.OrderDto(order.getId(), order.getLocation())).toList();

        return new GetNotCompletedOrdersResponse(orders);
    }
}
