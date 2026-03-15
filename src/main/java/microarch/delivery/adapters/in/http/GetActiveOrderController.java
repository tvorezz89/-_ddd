package microarch.delivery.adapters.in.http;

import lombok.RequiredArgsConstructor;
import microarch.delivery.adapters.in.http.api.GetActiveOrderApi;
import microarch.delivery.adapters.in.http.mapper.CourierMapper;
import microarch.delivery.core.application.query.GetAllUnFinishedOrdersHandler;
import microarch.delivery.adapters.in.http.api.model.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequiredArgsConstructor
public class GetActiveOrderController implements GetActiveOrderApi {

    private final GetAllUnFinishedOrdersHandler getAllUnFinishedOrdersHandler;
    @Override
    public ResponseEntity<List<Order>> getOrders() {

        var getResult = getAllUnFinishedOrdersHandler.handle();

        if (getResult.isFailure()) return ResponseEntity.status(HttpStatus.CONFLICT).build();

        var couriers = getResult.getValue().orders().stream().map(CourierMapper::toHttp).toList();

        return ResponseEntity.ok(couriers);
    }
}
