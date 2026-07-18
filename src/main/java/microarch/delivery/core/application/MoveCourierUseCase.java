package microarch.delivery.core.application;

import java.util.UUID;

import libs.errs.DomainInvariantException;
import libs.errs.GeneralErrors;
import microarch.delivery.core.domain.model.order.Location;
import microarch.delivery.core.ports.CourierRepository;
import org.springframework.stereotype.Service;

@Service
public class MoveCourierUseCase {
    private final CourierRepository couriers;

    public MoveCourierUseCase(CourierRepository couriers1) {
        couriers = couriers1;
    }

    public void execute(UUID courierId, int x, int y) {
        var courier = couriers.getById(courierId)
                .orElseThrow(() -> new DomainInvariantException(GeneralErrors.notFound("courier", courierId)));
        courier.move(Location.of(x, y));
        couriers.update(courier);
    }
}
