package microarch.delivery.core.domain.kernel;

import java.util.List;
import libs.ddd.ValueObject;
import libs.errs.Error;
import libs.errs.Guard;
import libs.errs.Result;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Location extends ValueObject<Location> {

    private int x;
    private int y;

    public static Result<Location, Error> create(int x, int y) {
        var validatedX = Guard.againstOutOfRange(x, 1, 10, "x");
        var validatedY = Guard.againstOutOfRange(y, 1, 10, "y");

        if (validatedX != null) {
            return Result.failure(validatedX);
        }
        if (validatedY != null) {
            return Result.failure(validatedY);
        }

        return Result.success(new Location(x, y));
    }

    public Result<Integer, Error> distanceTo(Location target) {
        if (target.equals(this)) {
            return Result.success(0);
        }

        var targetX = target.getX();
        var targetY = target.getY();

        var resultX = Math.abs(x - targetX);
        var resultY = Math.abs(y - targetY);

        return Result.success(resultX + resultY);
    }

    @Override
    protected Iterable<Object> equalityComponents() {
        return List.of(x, y);
    }

}
