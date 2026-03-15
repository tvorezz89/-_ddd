package microarch.delivery.core.application.query;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import microarch.delivery.core.application.query.dto.LocationDto;

import java.util.List;
import java.util.UUID;

public record GetAllUnFinishedOrdersResponseDto(

        List<OrderResponse> orders,
        Object error

) {

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class OrderResponse {
        private UUID id;
        private LocationDto location;
    }
}
