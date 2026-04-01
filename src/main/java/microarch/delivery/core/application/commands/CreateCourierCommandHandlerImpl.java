package microarch.delivery.core.application.commands;

import libs.errs.Error;
import libs.errs.Result;
import libs.errs.UnitResult;
import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.ports.CourierRepository;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
public class CreateCourierCommandHandlerImpl implements CreateCourierCommandHandler {

    private final CourierRepository courierRepository;

    public CreateCourierCommandHandlerImpl(CourierRepository courierRepository) {
        this.courierRepository = courierRepository;
    }

    @Override
    public UnitResult<Error> handle(CreateCourierCommand command) {
        var random = ThreadLocalRandom.current();
        var location = Location.create(random.nextInt(1, 11), random.nextInt(1, 11)).getValue();

        Result<Courier, Error> courier = Courier.create(command.getName(), location);
        if (courier.isFailure())
            return UnitResult.failure(courier.getError());

        courierRepository.save(courier.getValue());

        return UnitResult.success();
    }
}
