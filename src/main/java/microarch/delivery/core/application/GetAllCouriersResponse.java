package microarch.delivery.core.application;

import microarch.delivery.core.domain.model.Location;

import java.util.List;
import java.util.UUID;

public record GetAllCouriersResponse(List<CourierDto> couriers) {
    public record CourierDto(UUID id, String name, Location location) {
    }
}
