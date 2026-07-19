package microarch.delivery.core.domain.model.order;

import libs.ddd.ValueObject;
import libs.errs.DomainInvariantException;
import libs.errs.GeneralErrors;
import lombok.Getter;

import java.util.List;

@Getter
public class Location extends ValueObject<Location> {
    private final int x;
    private final int y;

    private Location(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static Location of(int x, int y) {
        return new Location(x, y);
    }

    public boolean isOneCellOrCloserTo(Location location) {
        if (location == null) {
            throw new DomainInvariantException(GeneralErrors.valueIsRequired("location"));
        }

        var xDistance = Math.abs(x - location.x);
        var yDistance = Math.abs(y - location.y);
        return xDistance <= 1 && yDistance <= 1;
    }

    @Override
    protected Iterable<Object> equalityComponents() {
        return List.of(x, y);
    }
}
