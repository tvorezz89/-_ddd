package microarch.delivery.core.application;

import java.util.UUID;

import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Location;
import microarch.delivery.core.ports.CourierRepository;
import org.springframework.stereotype.Service;

@Service
public class CreateCourierUseCase {
    private final CourierRepository couriers;

    public CreateCourierUseCase(CourierRepository couriers1) {
        couriers = couriers1;
    }

    public UUID execute(String name) {
        var courier = Courier.create(name, Location.of(1, 1));
        couriers.add(courier);
        return courier.getId();
    }
}
