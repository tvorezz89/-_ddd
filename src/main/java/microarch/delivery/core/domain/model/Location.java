package microarch.delivery.core.domain.model;

import java.util.List;

import libs.ddd.ValueObject;
import libs.errs.Guard;
import libs.errs.Result;
import lombok.Getter;

@Getter
public final class Location extends ValueObject<Location> {
    private static final int minimumCoordinate = 1;
    private static final int maximumCoordinate = 10;

    private final int x;
    private final int y;

    private Location(int x1, int y1) {
        x = x1;
        y = y1;
    }

    public static Result<Location, libs.errs.Error> create(int x1, int y1) {
        var error = Guard.combine(
                Guard.againstOutOfRange(x1, minimumCoordinate, maximumCoordinate, "x"),
                Guard.againstOutOfRange(y1, minimumCoordinate, maximumCoordinate, "y"));
        if (error != null) {
            return Result.failure(error);
        }
        return Result.success(new Location(x1, y1));
    }

    public int distanceTo(Location location) {
        return Math.abs(x - location.x) + Math.abs(y - location.y);
    }

    @Override
    protected Iterable<Object> equalityComponents() {
        return List.of(x, y);
    }
}
