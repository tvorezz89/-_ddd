package microarch.delivery.core.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import libs.ddd.ValueObject;
import libs.errs.Error;
import libs.errs.Guard;
import libs.errs.Result;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Embeddable
@Getter
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Location extends ValueObject<Location> {
    private static final int MIN_VALUE = 1;
    private static final int MAX_VALUE = 10;

    @Column(name = "latitude")
    private final int latitude;

    @Column(name = "longitude")
    private final int longitude;

    public static Result<Location, Error> create(int latitude, int longitude) {
        var errLat = Guard.againstOutOfRange(latitude, MIN_VALUE, MAX_VALUE, "latitude");
        if (errLat != null) {
            return Result.failure(errLat);
        }
        var errLong = Guard.againstOutOfRange(longitude, MIN_VALUE, MAX_VALUE, "longitude");
        if (errLong != null) {
            return Result.failure(errLong);
        }
        return Result.success(new Location(latitude, longitude));
    }

    public static Location mustCreate(int latitude, int longitude) {
        return create(latitude, longitude).getValueOrThrow();
    }

    @Override
    protected Iterable<Object> equalityComponents() {
        return List.of(this.latitude, this.longitude);
    }

    public boolean sameAsLocation(final Location location) {
        return this.latitude == location.latitude && this.longitude == location.longitude;
    }

    public int calculateDistance(final Location location) {
        return Math.abs(latitude - location.latitude) + Math.abs(longitude - location.longitude);
    }
}
