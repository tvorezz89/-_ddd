package microarch.delivery.adapters.in.http.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import microarch.delivery.core.application.query.GetAllUnFinishedOrdersResponseDto;
import microarch.delivery.adapters.in.http.api.model.Location;
import microarch.delivery.adapters.in.http.api.model.Order;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CourierMapper {


    public static Order toHttp(GetAllUnFinishedOrdersResponseDto.OrderResponse orderResponse) {
        return new Order(
                orderResponse.getId(),
                new Location(orderResponse.getLocation().getX(),orderResponse.getLocation().getY())
        );
    }
}
