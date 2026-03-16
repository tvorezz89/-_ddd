package microarch.delivery.adapters.in.http.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import microarch.delivery.adapters.in.http.api.model.CreateOrderResponse;
import microarch.delivery.adapters.in.http.api.model.Error;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public interface CreateOrderApi {

    String PATH_CREATE_ORDER = "/api/v1/orders";

    /**
     * POST /api/v1/orders : Создать заказ
     * Позволяет создать заказ с целью тестирования
     *
     * @return Заказ успешно создан (status code 201)
     * or Некорректные параметры запроса (status code 400)
     * or Конфликт при создании заказа (status code 409)
     * or Внутренняя ошибка сервиса (status code 500)
     */
    @Operation(
            operationId = "createOrder",
            summary = "Создать заказ",
            description = "Позволяет создать заказ с целью тестирования",
            tags = {"CreateOrder"},
            responses = {
                    @ApiResponse(responseCode = "201", description = "Заказ успешно создан", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = CreateOrderResponse.class))
                    }),
                    @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))
                    }),
                    @ApiResponse(responseCode = "409", description = "Конфликт при создании заказа", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))
                    }),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервиса", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))
                    })
            }
    )
    @RequestMapping(
            method = RequestMethod.POST,
            value = PATH_CREATE_ORDER,
            produces = {"application/json"}
    )
    ResponseEntity<CreateOrderResponse> createOrder();
}
