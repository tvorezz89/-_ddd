package microarch.delivery.core.application.query;

import microarch.delivery.core.application.query.dto.GetAllNotCompletedOrdersDto;

import java.util.List;

public record GetAllNotCompletedOrdersQueryResponse(List<GetAllNotCompletedOrdersDto> orders) {
}
