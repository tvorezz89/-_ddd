package microarch.delivery.core.domain.model;

import libs.ddd.ValueObject;
import libs.errs.Error;
import libs.errs.Guard;
import libs.errs.Result;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class Location extends ValueObject<Location> {
    private static final int MIN_VALUE = 1;
    private static final int MAX_VALUE = 10;

    private final int x;
    private final int y;

    public static Result<Location, Error> create(int x, int y) {
        var err = Guard.combine(Guard.againstOutOfRange(x, MIN_VALUE, MAX_VALUE, "x"),
                Guard.againstOutOfRange(y, MIN_VALUE, MAX_VALUE, "y"));
        if (err != null)
            return Result.failure(err);

        return Result.success(new Location(x, y));
    }

    public static Location mustCreate(int x, int y) {
        return create(x, y).getValueOrThrow();
    }

    @Override
    protected Iterable<Object> equalityComponents() {
        return List.of(this.x, this.y);
    }

    public int distanceTo(Location other) {
        return Math.abs(this.x - other.x) + Math.abs(this.y - other.y);
    }
}
