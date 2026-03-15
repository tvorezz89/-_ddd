package microarch.delivery.core.application.command;

import libs.errs.Error;
import libs.errs.UnitResult;
import lombok.RequiredArgsConstructor;
import microarch.delivery.core.domain.kernel.Location;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.ports.CourierRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateCourierCommandHandlerImpl implements CreateCourierCommandHandler {

    private final CourierRepository courierRepository;

    @Override
    public UnitResult<Error> handle(CreateCourierCommand command) {
        var location = Location.create(1, 1).getValue();
        var courier = Courier.create(command.getName(), command.getSpeed(), location);
        if (courier.isFailure()) {
            return UnitResult.failure(courier.getError());
        }

        courierRepository.add(courier.getValue());

        return UnitResult.success();
    }
}
