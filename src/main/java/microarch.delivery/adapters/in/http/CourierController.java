package microarch.delivery.adapters.in.http;

import lombok.RequiredArgsConstructor;
import microarch.delivery.adapters.in.http.api.ApiApi;
import microarch.delivery.adapters.in.http.model.*;
import microarch.delivery.core.application.command.CreateCourierCommand;
import microarch.delivery.core.application.command.CreateCourierCommandHandler;
import microarch.delivery.core.application.command.CreateOrderCommand;
import microarch.delivery.core.application.command.CreateOrderCommandHandler;
import microarch.delivery.core.application.query.GetAllCouriersQuery;
import microarch.delivery.core.application.query.GetAllCouriersQueryHandler;
import microarch.delivery.core.application.query.GetAllNotCompletedOrdersQuery;
import microarch.delivery.core.application.query.GetAllNotCompletedOrdersQueryHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("${openapi.swaggerDelivery.base-path:}")
public class CourierController implements ApiApi {

    private final CreateCourierCommandHandler createCourierCommandHandler;
    private final CreateOrderCommandHandler createOrderCommandHandler;
    private final GetAllCouriersQueryHandler getAllCouriersQueryHandler;
    private final GetAllNotCompletedOrdersQueryHandler getAllNotCompletedOrdersQueryHandler;
    private final NativeWebRequest request;

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    @Override
    public ResponseEntity<CreateCourierResponse> createCourier(NewCourier newCourier) {
        if (newCourier == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        var speedMock = 1;
        var command = CreateCourierCommand.create(newCourier.getName(), speedMock);
        if (command.isFailure()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        var result = createCourierCommandHandler.handle(command.getValue());
        if (result.isFailure()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<CreateOrderResponse> createOrder() {
        var createCommandResult = CreateOrderCommand.create(UUID.randomUUID(), "Россия", "Москва", "Айтишная", "1", "2",
                5);

        if (createCommandResult.isFailure()) {
            return ResponseEntity.badRequest().build();
        }
        var command = createCommandResult.getValue();

        // Обрабатываем команду
        var handleResult = this.createOrderCommandHandler.handle(command);
        if (handleResult.isFailure()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<Courier>> getCouriers() {
        var query = GetAllCouriersQuery.create();

        if (query.isFailure()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        var result = getAllCouriersQueryHandler.handle(query.getValue());
        if (result.isFailure()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        var response = result.getValue().couriers().stream().map(courier -> new Courier(courier.id(), courier.name(),
                new Location(courier.location().getX(), courier.location().getY()))).toList();

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<List<Order>> getOrders() {
        var query = GetAllNotCompletedOrdersQuery.create();
        if (query.isFailure()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        var result = getAllNotCompletedOrdersQueryHandler.handle(query.getValue());

        if (result.isFailure()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        var response = result.getValue().orders().stream()
                .map(order -> new Order(order.id(), new Location(order.location().getX(), order.location().getY())))
                .toList();
        return ResponseEntity.ok(response);
    }
}
