package microarch.delivery.core.application.query;

import microarch.delivery.core.application.query.dto.GetAllCouriersDto;

import java.util.List;

public record GetAllCouriersQueryResponse(List<GetAllCouriersDto> couriers) {
}
