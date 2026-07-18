package microarch.delivery.core.application;

import java.util.UUID;

import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.model.order.Volume;
import microarch.delivery.core.ports.GeoClient;
import microarch.delivery.core.ports.OrderRepository;
import org.springframework.stereotype.Service;

@Service
public class CreateOrderUseCase {
    private final OrderRepository orders;
    private final GeoClient geoClient;

    public CreateOrderUseCase(OrderRepository orders1, GeoClient geoClient1) {
        orders = orders1;
        geoClient = geoClient1;
    }

    public UUID execute(UUID orderId, String street, int volume) {
        var location = geoClient.getLocation(street);
        var order = Order.create(orderId, location, Volume.of(volume));
        orders.add(order);
        return order.getId();
    }
}
