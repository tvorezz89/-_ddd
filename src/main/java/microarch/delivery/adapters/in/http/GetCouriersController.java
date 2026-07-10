package microarch.delivery.adapters.in.http;

import lombok.RequiredArgsConstructor;
import microarch.delivery.adapters.in.http.api.GetCouriersApi;
import microarch.delivery.adapters.in.http.model.Courier;
import microarch.delivery.core.application.GetAllCouriersQuery;
import microarch.delivery.core.application.GetAllCouriersQueryHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GetCouriersController implements GetCouriersApi {
    private final GetAllCouriersQueryHandler getAllCouriersQueryHandler;

    @Override
    public ResponseEntity<List<Courier>> getCouriers() {
        var response = getAllCouriersQueryHandler.handle(GetAllCouriersQuery.create());
        var couriers = response.couriers().stream()
                .map(courier -> new Courier(courier.id(), courier.name(), HttpMapper.toHttp(courier.location())))
                .toList();

        return ResponseEntity.ok(couriers);
    }
}
