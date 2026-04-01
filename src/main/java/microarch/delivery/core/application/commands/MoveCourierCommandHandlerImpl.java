package microarch.delivery.core.application.commands;

import libs.errs.Error;
import libs.errs.GeneralErrors;
import libs.errs.UnitResult;
import microarch.delivery.core.ports.CourierRepository;
import org.springframework.stereotype.Service;

@Service
public class MoveCourierCommandHandlerImpl implements MoveCourierCommandHandler {

    private final CourierRepository courierRepository;

    public MoveCourierCommandHandlerImpl(CourierRepository courierRepository) {
        this.courierRepository = courierRepository;
    }

    @Override
    public UnitResult<Error> handle(MoveCourierCommand command) {
        var courier = courierRepository.findById(command.getCourierId()).orElse(null);
        if (courier == null)
            return UnitResult.failure(GeneralErrors.notFound("Courier", command.getCourierId()));

        courier.changeLocation(command.getLocation());
        courierRepository.save(courier);

        return UnitResult.success();
    }
}
