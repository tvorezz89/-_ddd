package microarch.delivery.adapters.in.http;

import java.util.List;
import java.util.UUID;

import microarch.delivery.adapters.in.http.api.CompleteOrderApi;
import microarch.delivery.adapters.in.http.api.CreateCourierApi;
import microarch.delivery.adapters.in.http.api.CreateOrderApi;
import microarch.delivery.adapters.in.http.api.GetCouriersApi;
import microarch.delivery.adapters.in.http.api.GetOrdersApi;
import microarch.delivery.adapters.in.http.api.MoveCourierApi;
import microarch.delivery.adapters.in.http.model.Courier;
import microarch.delivery.adapters.in.http.model.CreateCourierResponse;
import microarch.delivery.adapters.in.http.model.CreateOrderResponse;
import microarch.delivery.adapters.in.http.model.Location;
import microarch.delivery.adapters.in.http.model.NewCourier;
import microarch.delivery.adapters.in.http.model.NewOrder;
import microarch.delivery.adapters.in.http.model.Order;
import microarch.delivery.core.application.CompleteOrderUseCase;
import microarch.delivery.core.application.CreateCourierUseCase;
import microarch.delivery.core.application.CreateOrderUseCase;
import microarch.delivery.core.application.GetCouriersUseCase;
import microarch.delivery.core.application.GetOrdersUseCase;
import microarch.delivery.core.application.MoveCourierUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeliveryController
        implements CreateOrderApi, CompleteOrderApi, MoveCourierApi, GetOrdersApi, CreateCourierApi, GetCouriersApi {
    private final CreateOrderUseCase createOrderUseCase;
    private final CompleteOrderUseCase completeOrderUseCase;
    private final MoveCourierUseCase moveCourierUseCase;
    private final GetOrdersUseCase getOrdersUseCase;
    private final CreateCourierUseCase createCourierUseCase;
    private final GetCouriersUseCase getCouriersUseCase;

    public DeliveryController(CreateOrderUseCase createOrderUseCase1, CompleteOrderUseCase completeOrderUseCase1,
            MoveCourierUseCase moveCourierUseCase1, GetOrdersUseCase getOrdersUseCase1,
            CreateCourierUseCase createCourierUseCase1, GetCouriersUseCase getCouriersUseCase1) {
        createOrderUseCase = createOrderUseCase1;
        completeOrderUseCase = completeOrderUseCase1;
        moveCourierUseCase = moveCourierUseCase1;
        getOrdersUseCase = getOrdersUseCase1;
        createCourierUseCase = createCourierUseCase1;
        getCouriersUseCase = getCouriersUseCase1;
    }

    @Override
    public ResponseEntity<CreateOrderResponse> createOrder(NewOrder newOrder) {
        var orderId = createOrderUseCase.execute(newOrder.getId(), newOrder.getAddress().getStreet(),
                newOrder.getVolume());
        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateOrderResponse(orderId));
    }

    @Override
    public ResponseEntity<Void> completeOrder(UUID courierId, UUID orderId) {
        completeOrderUseCase.execute(courierId, orderId);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> moveCourier(UUID courierId, Location location) {
        moveCourierUseCase.execute(courierId, location.getX(), location.getY());
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<Order>> getOrders() {
        var orders = getOrdersUseCase.execute().stream()
                .map(order -> new Order(order.id(), new Location(order.x(), order.y())))
                .toList();
        return ResponseEntity.ok(orders);
    }

    @Override
    public ResponseEntity<CreateCourierResponse> createCourier(NewCourier newCourier) {
        var courierId = createCourierUseCase.execute(newCourier.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateCourierResponse(courierId));
    }

    @Override
    public ResponseEntity<List<Courier>> getCouriers() {
        var couriers = getCouriersUseCase.execute().stream()
                .map(courier -> new Courier(courier.id(), courier.name(), new Location(courier.x(), courier.y())))
                .toList();
        return ResponseEntity.ok(couriers);
    }
}
