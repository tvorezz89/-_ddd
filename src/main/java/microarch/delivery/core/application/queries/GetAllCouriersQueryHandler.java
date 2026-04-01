package microarch.delivery.core.application.queries;

import java.util.List;

public interface GetAllCouriersQueryHandler {
    List<CourierDto> handle(GetAllCouriersQuery query);
}
