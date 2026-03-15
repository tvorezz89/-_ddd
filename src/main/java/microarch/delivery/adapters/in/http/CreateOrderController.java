package microarch.delivery.adapters.in.http;

import lombok.RequiredArgsConstructor;
import microarch.delivery.adapters.in.http.api.CreateOrderApi;
import microarch.delivery.core.application.command.CreateOrderCommand;
import microarch.delivery.core.application.command.CreateOrderCommandHandler;
import microarch.delivery.adapters.in.http.api.model.CreateOrderResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CreateOrderController implements CreateOrderApi {

    private final CreateOrderCommandHandler createOrderCommandHandler;

    @Override
    public ResponseEntity<CreateOrderResponse> createOrder() {
        var createCommandResult = CreateOrderCommand.create(
                UUID.randomUUID(),
                "Россия", "Москва", "Айтишная", "1", "2", 5
        );

        if (createCommandResult.isFailure()) {
            return ResponseEntity.badRequest().build();
        }
        var command = createCommandResult.getValue();

        var handleResult = createOrderCommandHandler.handle(command);
        if (handleResult.isFailure()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        return ResponseEntity.ok(new CreateOrderResponse().orderId(handleResult.getValue()));
    }
}
