package microarch.delivery.adapters.in.http;

import lombok.RequiredArgsConstructor;
import microarch.delivery.adapters.in.http.api.GetCouriersApi;
import microarch.delivery.adapters.in.http.mapper.OrderMapper;
import microarch.delivery.core.application.query.GetAllCouriersQueryHandler;
import microarch.delivery.adapters.in.http.api.model.Courier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GetCouriersController implements GetCouriersApi {

    private final GetAllCouriersQueryHandler getAllCouriersQueryHandler;

    @Override
    public ResponseEntity<List<Courier>> getCouriers() {

        var getResult = getAllCouriersQueryHandler.handle();

        if (getResult.isFailure()) return ResponseEntity.status(HttpStatus.CONFLICT).build();

        var orders = getResult.getValue().couriers().stream().map(OrderMapper::toHttp).toList();

        return ResponseEntity.ok(orders);
    }
}
