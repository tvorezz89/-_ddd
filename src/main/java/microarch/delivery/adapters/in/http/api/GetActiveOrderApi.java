package microarch.delivery.adapters.in.http.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import microarch.delivery.adapters.in.http.api.model.Error;
import microarch.delivery.adapters.in.http.api.model.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

public interface GetActiveOrderApi {

    String PATH_GET_ORDERS = "/api/v1/orders/active";

    /**
     * GET /api/v1/orders/active : Получить все незавершенные заказы
     * Позволяет получить все незавершенные заказы
     *
     * @return Успешный ответ (status code 200)
     * or Ошибка (status code 200)
     */
    @Operation(
            operationId = "getOrders",
            summary = "Получить все незавершенные заказы",
            description = "Позволяет получить все незавершенные заказы",
            tags = {"GetOrders"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешный ответ", content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Order.class)))
                    }),
                    @ApiResponse(responseCode = "default", description = "Ошибка", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))
                    })
            }
    )
    @RequestMapping(
            method = RequestMethod.GET,
            value = PATH_GET_ORDERS,
            produces = {"application/json"}
    )
    ResponseEntity<List<Order>> getOrders();
}
