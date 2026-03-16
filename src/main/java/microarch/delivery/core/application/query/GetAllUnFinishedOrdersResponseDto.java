package microarch.delivery.core.application.query;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import microarch.delivery.core.application.query.dto.LocationDto;

import java.util.List;

public record GetAllUnFinishedOrdersResponseDto(

        List<OrderResponse> orders,
        Object error

) {

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class OrderResponse {
        private String id;
        private LocationDto location;
    }
}
