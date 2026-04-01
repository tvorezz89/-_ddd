package microarch.delivery.adapters.in.http;

import libs.errs.Error;
import libs.errs.Result;
import lombok.RequiredArgsConstructor;
import microarch.delivery.adapters.in.http.api.ApiApi;
import microarch.delivery.adapters.in.http.model.*;
import microarch.delivery.core.application.commands.*;
import microarch.delivery.core.application.queries.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CourierController implements ApiApi {

    private final CompleteOrderCommandHandler completeOrderCommandHandler;
    private final CreateCourierCommandHandler createCourierCommandHandler;
    private final CreateOrderCommandHandler createOrderCommandHandler;
    private final MoveCourierCommandHandler moveCourierCommandHandler;
    private final GetAllCouriersQueryHandler getAllCouriersQueryHandler;
    private final GetNotCompletedOrdersQueryHandler getNotCompletedOrdersQueryHandler;

    @Override
    public ResponseEntity<Void> completeOrder(UUID courierId, UUID orderId) {
        Result<CompleteOrderCommand, Error> result = CompleteOrderCommand.create(courierId, orderId);
        if (result.isFailure())
            return ResponseEntity.badRequest().build();
        var command = result.getValue();

        var handlerResult = completeOrderCommandHandler.handle(command);
        if (handlerResult.isFailure())
            return ResponseEntity.status(HttpStatus.CONFLICT).build();

        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<CreateCourierResponse> createCourier(NewCourier newCourier) {
        var result = CreateCourierCommand.create(newCourier.getName());
        if (result.isFailure())
            return ResponseEntity.badRequest().build();

        var handlerResult = createCourierCommandHandler.handle(result.getValue());
        if (handlerResult.isFailure())
            return ResponseEntity.status(HttpStatus.CONFLICT).build();

        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateCourierResponse());
    }

    @Override
    public ResponseEntity<CreateOrderResponse> createOrder() {
        var orderId = UUID.randomUUID();
        var result = CreateOrderCommand.create(orderId, "Russia", "street", "Moscow", "1", "1", 1);
        if (result.isFailure())
            return ResponseEntity.badRequest().build();

        var handlerResult = createOrderCommandHandler.handle(result.getValue());
        if (handlerResult.isFailure())
            return ResponseEntity.status(HttpStatus.CONFLICT).build();

        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateOrderResponse().orderId(orderId));
    }

    @Override
    public ResponseEntity<List<Courier>> getCouriers() {
        var couriers = getAllCouriersQueryHandler.handle(new GetAllCouriersQuery());
        var response = couriers.stream().map(dto -> new Courier().id(dto.getId()).name(dto.getName())
                .location(new Location().x(dto.getLocationX()).y(dto.getLocationY()))).toList();
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<List<Order>> getOrders() {
        var orders = getNotCompletedOrdersQueryHandler.handle(new GetNotCompletedOrdersQuery());
        var response = orders.stream().map(
                dto -> new Order().id(dto.getId()).location(new Location().x(dto.getLocationX()).y(dto.getLocationY())))
                .toList();
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> moveCourier(UUID courierId, Location location) {
        var domainLocation = microarch.delivery.core.domain.model.Location.create(location.getX(), location.getY());
        if (domainLocation.isFailure())
            return ResponseEntity.badRequest().build();

        var result = MoveCourierCommand.create(courierId, domainLocation.getValue());
        if (result.isFailure())
            return ResponseEntity.badRequest().build();

        var handlerResult = moveCourierCommandHandler.handle(result.getValue());
        if (handlerResult.isFailure())
            return ResponseEntity.status(HttpStatus.CONFLICT).build();

        return ResponseEntity.ok().build();
    }
}
