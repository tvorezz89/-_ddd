package microarch.delivery.core.application.command;

import libs.errs.Error;
import libs.errs.GeneralErrors;
import libs.errs.Guard;
import libs.errs.Result;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import microarch.delivery.core.domain.Address;
import microarch.delivery.core.domain.Volume;

import java.util.UUID;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class CreateOrderCommand {

    private final UUID orderID;

    private final Address address;

    private final Volume volume;

    public static Result<CreateOrderCommand, Error> create(UUID orderID,
                                                           String country,
                                                           String city,
                                                           String street,
                                                           String house,
                                                           String apartment,
                                                           Integer volume) {


        var errors = Guard.combine(GeneralErrors.valueIsRequired(orderID, "orderId"));
        if (errors != null) Result.failure(errors);

        var addressResult = Address.create(country, city, street, house, apartment);
        if (addressResult.isFailure())
            return Result.failure(addressResult.getError());

        var volumeResult = Volume.create(volume);
        if (volumeResult.isFailure())
            return Result.failure(volumeResult.getError());

        return Result.success(new CreateOrderCommand(orderID, addressResult.getValue(), volumeResult.getValue()));
    }
}
