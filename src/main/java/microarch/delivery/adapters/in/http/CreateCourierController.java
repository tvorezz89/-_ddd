package microarch.delivery.adapters.in.http;

import lombok.RequiredArgsConstructor;
import microarch.delivery.adapters.in.http.api.CreateCourierApi;
import microarch.delivery.core.application.command.CreateCourierCommand;
import microarch.delivery.core.application.command.CreateCourierCommandHandler;
import microarch.delivery.adapters.in.http.api.model.CreateCourierResponse;
import microarch.delivery.adapters.in.http.api.model.NewCourier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CreateCourierController implements CreateCourierApi {

    private final CreateCourierCommandHandler createCourierCommandHandler;

    @Override
    public ResponseEntity<CreateCourierResponse> createCourier(@Nullable NewCourier newCourier) {

        var createCommandResult = CreateCourierCommand.create(newCourier.getName(), 1);
        if (createCommandResult.isFailure()) return ResponseEntity.badRequest().build();

        var handlerResult = createCourierCommandHandler.handle(createCommandResult.getValue());
        if (handlerResult.isFailure()) return ResponseEntity.status(HttpStatus.CONFLICT).build();

        return ResponseEntity.ok(new CreateCourierResponse().courierId(handlerResult.getValue()));
    }
}
