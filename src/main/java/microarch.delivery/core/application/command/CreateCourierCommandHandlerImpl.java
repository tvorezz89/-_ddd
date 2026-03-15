package microarch.delivery.core.application.command;

import libs.errs.Error;
import libs.errs.UnitResult;
import lombok.RequiredArgsConstructor;
import microarch.delivery.core.domain.Location;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.ports.CourierRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateCourierCommandHandlerImpl implements CreateCourierCommandHandler {

    private final CourierRepository courierRepository;

    @Override
    public UnitResult<Error> handle(CreateCourierCommand command) {

        var locationResult = Location.create(4, 4); // todo implement integration with Geo
        if (locationResult.isFailure()) {
            return UnitResult.failure(locationResult.getError());
        }

        var courierResult = Courier.create(command.getName(), command.getSpeed(), locationResult.getValue());

        if (courierResult.isFailure()) {
            return UnitResult.failure(courierResult.getError());
        }

        courierRepository.save(courierResult.getValue());

        return UnitResult.success();
    }
}
