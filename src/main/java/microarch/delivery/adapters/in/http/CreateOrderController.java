package microarch.delivery.adapters.in.http;

import lombok.RequiredArgsConstructor;
import microarch.delivery.adapters.in.http.api.CreateOrderApi;
import microarch.delivery.adapters.in.http.model.CreateOrderResponse;
import microarch.delivery.adapters.in.http.model.NewOrder;
import microarch.delivery.core.application.CreateOrderCommand;
import microarch.delivery.core.application.CreateOrderCommandHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CreateOrderController implements CreateOrderApi {
    private static final int DEFAULT_VOLUME = 1;

    private final CreateOrderCommandHandler createOrderCommandHandler;

    @Override
    public ResponseEntity<CreateOrderResponse> createOrder(NewOrder newOrder) {
        var address = newOrder.getAddress();
        var volume = newOrder.getVolume() == null ? DEFAULT_VOLUME : newOrder.getVolume();
        var commandResult = CreateOrderCommand.create(newOrder.getId(), address.getCountry(), address.getCity(),
                address.getStreet(), address.getHouse(), address.getApartment(), volume);
        if (commandResult.isFailure()) {
            return ResponseEntity.badRequest().build();
        }

        var handleResult = createOrderCommandHandler.handle(commandResult.getValue());
        if (handleResult.isFailure()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateOrderResponse(newOrder.getId()));
    }
}
