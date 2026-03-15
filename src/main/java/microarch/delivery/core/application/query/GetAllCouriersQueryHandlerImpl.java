package microarch.delivery.core.application.query;

import libs.errs.Error;
import libs.errs.Result;
import lombok.RequiredArgsConstructor;
import microarch.delivery.core.application.query.dto.LocationDto;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.ports.CourierRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAllCouriersQueryHandlerImpl implements GetAllCouriersQueryHandler {

    private final CourierRepository courierRepository;

    @Override
    public Result<GetAllCouriersResponseDto, Error> handle() {
        var allCouriers = courierRepository.findAll();
        return Result.success(mapToResponse(allCouriers));
    }

    private GetAllCouriersResponseDto mapToResponse(List<Courier> couriers) {
        return new GetAllCouriersResponseDto(couriers.stream().map(this::toDto).toList(), null);
    }

    private GetAllCouriersResponseDto.CourierResponse toDto(Courier courier) {
        return new GetAllCouriersResponseDto.CourierResponse(
                courier.getId(),
                courier.getName(),
                new LocationDto(courier.getLocation().getX(), courier.getLocation().getY())
        );
    }


}
