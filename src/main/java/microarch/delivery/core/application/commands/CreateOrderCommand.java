package microarch.delivery.core.application.commands;

import libs.errs.*;
import libs.errs.Error;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateOrderCommand {
    private final UUID orderId;
    private final String country;
    private final String string;
    private final String city;
    private final String house;
    private final String apartment;
    private final int volume;

    public static Result<CreateOrderCommand, Error> create(UUID orderId, String country, String string, String city,
            String house, String apartment, int volume) {
        var validation = Guard.combine(Guard.againstNullOrEmpty(orderId, "orderId"),
                Guard.againstNullOrEmpty(country, "country"), Guard.againstNullOrEmpty(string, "string"),
                Guard.againstNullOrEmpty(city, "city"), Guard.againstNullOrEmpty(house, "house"),
                Guard.againstNullOrEmpty(apartment, "apartment"), Guard.againstLessOrEqual(volume, 0, "volume"));
        if (Objects.nonNull(validation))
            return Result.failure(validation);

        return Result.success(new CreateOrderCommand(orderId, country, string, city, house, apartment, volume));
    }
}
