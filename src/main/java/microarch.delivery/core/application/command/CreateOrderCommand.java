package microarch.delivery.core.application.command;

import libs.errs.Error;
import libs.errs.Guard;
import libs.errs.Result;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import microarch.delivery.core.domain.kernel.Volume;

import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateOrderCommand {

    private UUID orderId;

    private String country;

    private String city;

    private String street;

    private String house;

    private String apartment;

    private Volume volume;

    public static Result<CreateOrderCommand, Error> create(UUID orderId, String country, String city, String street,
            String house, String apartment, Integer volume) {

        var validateOrderId = Guard.againstNullOrEmpty(orderId, "orderId");
        if (validateOrderId != null) {
            return Result.failure(validateOrderId);
        }

        var validateCountry = Guard.againstNullOrEmpty(country, "country");
        if (validateCountry != null) {
            return Result.failure(validateCountry);
        }

        var validateCity = Guard.againstNullOrEmpty(city, "city");
        if (validateCity != null) {
            return Result.failure(validateCity);
        }

        var validateStreet = Guard.againstNullOrEmpty(street, "street");
        if (validateStreet != null) {
            return Result.failure(validateStreet);
        }

        var validateHouse = Guard.againstNullOrEmpty(house, "house");
        if (validateHouse != null) {
            return Result.failure(validateHouse);
        }

        var validateApartment = Guard.againstNullOrEmpty(apartment, "apartment");
        if (validateApartment != null) {
            return Result.failure(validateApartment);
        }

        var volumeResult = Volume.create(volume);
        if (volumeResult.isFailure()) {
            return Result.failure(volumeResult.getError());
        }

        return Result.success(
                new CreateOrderCommand(orderId, country, city, street, house, apartment, volumeResult.getValue()));
    }
}
