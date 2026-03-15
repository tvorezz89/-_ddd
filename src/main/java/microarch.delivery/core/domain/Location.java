package microarch.delivery.core.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import libs.ddd.ValueObject;
import libs.errs.Error;
import libs.errs.GeneralErrors;
import libs.errs.Result;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

@Getter
@Embeddable
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
public class Location extends ValueObject<Location> {

    public static final int MIN_COORDINATE = 1;
    public static final int MAX_COORDINATE = 10;

    @Column(name = "location_x")
    private final int x;
    @Column(name = "location_y")
    private final int y;

    public static Result<Location, Error> create(int x, int y) {
        if (x < MIN_COORDINATE || x > MAX_COORDINATE) {
            return Result.failure(GeneralErrors.valueIsOutOfRange("x", x, MIN_COORDINATE, MAX_COORDINATE));
        }
        if (y < MIN_COORDINATE || y > MAX_COORDINATE) {
            return Result.failure(GeneralErrors.valueIsOutOfRange("y", y, MIN_COORDINATE, MAX_COORDINATE));
        }
        return Result.success(new Location(x, y));
    }

    public static Location mustCreate(int x, int y) {
        return create(x, y).getValueOrThrow();
    }

    private Location(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int distanceTo(Location location) {
        Objects.requireNonNull(location, "location");
        return Math.abs(this.x - location.x) + Math.abs(this.y - location.y);
    }


    @Override
    protected Iterable<Object> equalityComponents() {
        return List.of(x, y);
    }
}
