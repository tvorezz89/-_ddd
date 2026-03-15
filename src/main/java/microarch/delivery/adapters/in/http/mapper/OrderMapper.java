package microarch.delivery.adapters.in.http.mapper;

import microarch.delivery.core.application.query.GetAllCouriersResponseDto;
import microarch.delivery.adapters.in.http.api.model.Courier;
import microarch.delivery.adapters.in.http.api.model.Location;

public final class OrderMapper {

    public static Courier toHttp(GetAllCouriersResponseDto.CourierResponse courierResponse) {
        return new Courier(
                courierResponse.getCourierId(),
                courierResponse.getName(),
                new Location(
                        courierResponse.getCourierLocation().getX(),
                        courierResponse.getCourierLocation().getY()
                )
        );
    }
}
