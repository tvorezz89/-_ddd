package microarch.delivery.core.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class LocationTest {

    @ParameterizedTest
    @CsvSource({"5, 5, true",
            "3, 4, true",
            "1, 10, true",
            "10, 1, true",
            "0, 0, false",
            "-10, 15, false",
            "100, -100, false",
            "-50, -50, false"})
    void create(int x, int y, boolean success) {
        if (success) {
            Assertions.assertDoesNotThrow(() -> Location.create(x, y));
        } else {
            Assertions.assertThrows(IllegalArgumentException.class, () -> Location.create(x, y));
        }
    }

    @Test
    void distance() {
        var location = Location.create(1, 3);
        var destination = Location.create(5, 6);
        var distanceTo = location.calculateDistanceTo(destination);
        Assertions.assertEquals(7, distanceTo);

        var distanceFrom = destination.calculateDistanceTo(location);
        Assertions.assertEquals(7, distanceFrom);
    }
}