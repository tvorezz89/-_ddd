package microarch.delivery.core.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class LocationTest {
    @Test
    void shouldCreateLocationWhenCoordinatesInRange() {
        var result = Location.create(1, 10);

        assertTrue(result.isSuccess());
        var location = result.getValue();
        assertEquals(1, location.getX());
        assertEquals(10, location.getY());
    }

    @Test
    void shouldReturnFailureWhenCoordinateLessThanMinimum() {
        var result = Location.create(0, 1);

        assertTrue(result.isFailure());
    }

    @Test
    void shouldReturnFailureWhenCoordinateGreaterThanMaximum() {
        var result = Location.create(1, 11);

        assertTrue(result.isFailure());
    }

    @Test
    void shouldBeEqualWhenCoordinatesMatch() {
        var firstLocation = Location.create(3, 7).getValue();
        var secondLocation = Location.create(3, 7).getValue();

        assertEquals(firstLocation, secondLocation);
        assertEquals(firstLocation.hashCode(), secondLocation.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenCoordinatesDifferent() {
        var firstLocation = Location.create(3, 7).getValue();
        var secondLocation = Location.create(4, 7).getValue();

        assertNotEquals(firstLocation, secondLocation);
    }

    @Test
    void shouldCalculateDistanceAsSumByAxes() {
        var courierLocation = Location.create(2, 2).getValue();
        var orderLocation = Location.create(4, 5).getValue();

        assertEquals(5, courierLocation.distanceTo(orderLocation));
    }

    @Test
    void shouldReturnFailureWhenBothCoordinatesOutOfRange() {
        var result = Location.create(0, 11);

        assertTrue(result.isFailure());
    }
}
