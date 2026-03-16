package microarch.delivery.adapters.in.http.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import microarch.delivery.adapters.in.http.api.model.CreateCourierResponse;
import microarch.delivery.adapters.in.http.api.model.Error;
import microarch.delivery.adapters.in.http.api.model.NewCourier;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public interface CreateCourierApi {

    String PATH_CREATE_COURIER = "/api/v1/couriers";

    /**
     * POST /api/v1/couriers : Добавить курьера
     * Позволяет добавить курьера
     *
     * @param newCourier Курьер (optional)
     * @return Курьер успешно создан (status code 201)
     * or Некорректные параметры запроса (status code 400)
     * or Конфликт при создании курьера (status code 409)
     * or Внутренняя ошибка сервиса (status code 500)
     */
    @Operation(
            operationId = "createCourier",
            summary = "Добавить курьера",
            description = "Позволяет добавить курьера",
            tags = {"CreateCourier"},
            responses = {
                    @ApiResponse(responseCode = "201", description = "Курьер успешно создан", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = CreateCourierResponse.class))
                    }),
                    @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))
                    }),
                    @ApiResponse(responseCode = "409", description = "Конфликт при создании курьера", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))
                    }),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервиса", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))
                    })
            }
    )
    @RequestMapping(
            method = RequestMethod.POST,
            value = PATH_CREATE_COURIER,
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    ResponseEntity<CreateCourierResponse> createCourier(
            @Parameter(name = "NewCourier", description = "Курьер") @Valid @RequestBody(required = false) @Nullable NewCourier newCourier
    );
}
