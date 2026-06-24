package microarch.delivery.core.application;

import microarch.delivery.core.domain.model.Location;

import java.util.List;
import java.util.UUID;

public record GetNotCompletedOrdersResponse(List<OrderDto> orders) {
    public record OrderDto(UUID id, Location location) {
    }
}
