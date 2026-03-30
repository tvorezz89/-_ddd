package microarch.delivery.core.domain.model;

import libs.ddd.ValueObject;
import libs.errs.Error;
import libs.errs.GeneralErrors;
import libs.errs.Result;

import java.util.List;

public class Location extends ValueObject<Location> {
    private int x;
    private int y;

    private static final int MIN = 1;
    private static final int MAX = 10;

    private Location() {
    }

    private Location(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static Result<Location, Error> create(int x, int y) {
        if (x < MIN)
            return Result.failure(GeneralErrors.valueMustBeGreaterThan("x", x, MIN));
        if (y < MIN)
            return Result.failure(GeneralErrors.valueMustBeGreaterThan("y", y, MIN));
        if (x > MAX)
            return Result.failure(GeneralErrors.valueMustBeLessThan("x", x, MAX));
        if (y > MAX)
            return Result.failure(GeneralErrors.valueMustBeLessThan("y", y, MAX));

        Location location = new Location(x, y);

        return Result.success(location);
    }

    @Override
    protected Iterable<Object> equalityComponents() {
        return List.of(x, y);
    }

    public Boolean equals(Location l) {
        return equalityComponents().equals(l.equalityComponents());
    }

    public Result<Integer, Error> distanceTo(Location target) {
        return Result.success(Math.abs(this.x - target.x) + Math.abs(this.y - target.y));
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
