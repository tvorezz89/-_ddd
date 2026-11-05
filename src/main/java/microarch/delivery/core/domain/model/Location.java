package microarch.delivery.core.domain.model;

import libs.ddd.ValueObject;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Location extends ValueObject<Location> {
    private static final int MIN_VALUE = 1;
    private static final int MAX_VALUE = 10;
    private final int x;
    private final int y;

    public static Location create(int x, int y) {
        if (x < MIN_VALUE || x > MAX_VALUE) {
            throw new IllegalArgumentException("Wrong coordinate x of location");
        }
        if (y < MIN_VALUE || y > MAX_VALUE) {
            throw new IllegalArgumentException("Wrong coordinate y of location");
        }
        return new Location(x, y);
    }

    @Override
    protected Iterable<Object> equalityComponents() {
        return List.of(this.x, this.y);
    }

    public int calculateDistanceTo(Location destination) {
        if (destination == null) {
            throw new IllegalArgumentException("No location of destination");
        }
        return Math.abs(this.getX() - destination.getX()) + Math.abs(this.getY() - destination.getY());
    }
}
