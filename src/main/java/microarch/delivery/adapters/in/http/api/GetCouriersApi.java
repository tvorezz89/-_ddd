package microarch.delivery.adapters.in.http.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import microarch.delivery.adapters.in.http.api.model.Courier;
import microarch.delivery.adapters.in.http.api.model.Error;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

public interface GetCouriersApi {

    String PATH_GET_COURIERS = "/api/v1/couriers";

    /**
     * GET /api/v1/couriers : Получить всех курьеров
     * Позволяет получить всех курьеров
     *
     * @return Успешный ответ (status code 200)
     * or Ошибка (status code 200)
     */
    @Operation(
            operationId = "getCouriers",
            summary = "Получить всех курьеров",
            description = "Позволяет получить всех курьеров",
            tags = {"GetCouriers"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешный ответ", content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Courier.class)))
                    }),
                    @ApiResponse(responseCode = "default", description = "Ошибка", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))
                    })
            }
    )
    @RequestMapping(
            method = RequestMethod.GET,
            value = PATH_GET_COURIERS,
            produces = {"application/json"}
    )
    ResponseEntity<List<Courier>> getCouriers();
}
