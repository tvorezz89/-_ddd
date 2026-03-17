package microarch.delivery.core.application.query.dto;

import microarch.delivery.core.domain.kernel.Location;

import java.util.UUID;

public record GetAllNotCompletedOrdersDto(UUID id, Location location) {
}
