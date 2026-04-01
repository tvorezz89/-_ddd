package microarch.delivery.core.application.commands;

import libs.errs.Error;
import libs.errs.Guard;
import libs.errs.Result;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import microarch.delivery.core.domain.model.Location;

import java.util.Objects;
import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MoveCourierCommand {
    private final UUID courierId;
    private final Location location;

    public static Result<MoveCourierCommand, Error> create(UUID courierId, Location location) {
        var validation = Guard.againstNullOrEmpty(courierId, "courierId");
        if (Objects.nonNull(validation))
            return Result.failure(validation);

        Objects.requireNonNull(location, "location");

        return Result.success(new MoveCourierCommand(courierId, location));
    }
}
