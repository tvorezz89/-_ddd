package microarch.delivery.core.application;

import microarch.delivery.core.ports.CourierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetAllCouriersQueryHandlerImpl implements GetAllCouriersQueryHandler {
    private final CourierRepository courierRepository;

    public GetAllCouriersQueryHandlerImpl(CourierRepository courierRepository) {
        this.courierRepository = courierRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public GetAllCouriersResponse handle(GetAllCouriersQuery query) {
        var couriers = courierRepository.findAll().stream()
                .map(courier -> new GetAllCouriersResponse.CourierDto(courier.getId(), courier.getName(),
                        courier.getLocation()))
                .toList();

        return new GetAllCouriersResponse(couriers);
    }
}
