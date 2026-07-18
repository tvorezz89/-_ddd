package microarch.delivery.core.application;

import java.util.List;

import microarch.delivery.core.ports.CourierRepository;
import org.springframework.stereotype.Service;

@Service
public class GetCouriersUseCase {
    private final CourierRepository couriers;

    public GetCouriersUseCase(CourierRepository couriers1) {
        couriers = couriers1;
    }

    public List<CourierReadModel> execute() {
        return couriers.getAll().stream().map(courier -> new CourierReadModel(
                courier.getId(),
                courier.getName(),
                courier.getLocation().getX(),
                courier.getLocation().getY())).toList();
    }
}
