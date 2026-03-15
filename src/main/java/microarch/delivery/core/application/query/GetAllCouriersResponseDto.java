package microarch.delivery.core.application.query;


import lombok.*;
import microarch.delivery.core.application.query.dto.LocationDto;

import java.util.List;
import java.util.UUID;

public record GetAllCouriersResponseDto(
        List<CourierResponse> couriers,
        Object error
) {

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class CourierResponse {
        private UUID courierId;
        private String name;
        private LocationDto courierLocation;
    }

}
