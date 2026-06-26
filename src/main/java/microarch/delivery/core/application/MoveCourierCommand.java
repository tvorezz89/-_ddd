package microarch.delivery.core.application;

import libs.errs.Error;
import libs.errs.Guard;
import libs.errs.Result;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import microarch.delivery.core.domain.model.Location;

import java.util.Objects;
import java.util.UUID;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MoveCourierCommand {
    private final UUID courierId;
    private final Location location;

    public static Result<MoveCourierCommand, Error> create(UUID courierId, Location location) {
        Objects.requireNonNull(location, "location");

        var err = Guard.againstNullOrEmpty(courierId, "courierId");
        if (err != null) {
            return Result.failure(err);
        }

        return Result.success(new MoveCourierCommand(courierId, location));
    }
}
