package microarch.delivery.adapters.in.http;

import lombok.RequiredArgsConstructor;
import microarch.delivery.adapters.in.http.api.GetOrdersApi;
import microarch.delivery.adapters.in.http.model.Order;
import microarch.delivery.core.application.GetNotCompletedOrdersQuery;
import microarch.delivery.core.application.GetNotCompletedOrdersQueryHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GetOrdersController implements GetOrdersApi {
    private final GetNotCompletedOrdersQueryHandler getNotCompletedOrdersQueryHandler;

    @Override
    public ResponseEntity<List<Order>> getOrders() {
        var response = getNotCompletedOrdersQueryHandler.handle(GetNotCompletedOrdersQuery.create());
        var orders = response.orders().stream().map(order -> new Order(order.id(), HttpMapper.toHttp(order.location())))
                .toList();

        return ResponseEntity.ok(orders);
    }
}
