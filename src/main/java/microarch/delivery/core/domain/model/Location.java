package microarch.delivery.core.domain.model;

import libs.errs.Error;
import libs.errs.GeneralErrors;
import libs.errs.Result;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import libs.ddd.ValueObject;

import java.util.List;

@Getter
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Location extends ValueObject<Location> {

    private final int x;
    private final int y;

    public static Result<Location, Error> create(int x, int y) {
        if (x < 1 || x > 10)
            return Result.failure(GeneralErrors.valueIsOutOfRange("x", x, 1, 10));
        if (y < 1 || y > 10)
            return Result.failure(GeneralErrors.valueIsOutOfRange("y", y, 1, 10));

        Location location = new Location(x, y);

        return Result.success(location);
    }

    public int calculateDistance(Location location2) {
        return Math.abs(this.x - location2.x) + Math.abs(this.y - location2.y);
    }

    @Override
    protected Iterable<Object> equalityComponents() {
        return List.of(this.x, this.y);
    }
}
