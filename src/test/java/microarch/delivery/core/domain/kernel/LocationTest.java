package microarch.delivery.core.domain.kernel;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class LocationTest {

    @Test
    void create_shouldWorkCorrectly() {
        var x = 1;
        var y = 2;
        var result = Location.create(x, y);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue().getX()).isEqualTo(x);
        assertThat(result.getValue().getY()).isEqualTo(y);
    }

    @ParameterizedTest
    @CsvSource({ "0, 5", "5, 0", "11, 5", "5, 11" })
    void create_whenCoordinatesIsInvalid_shouldReturnFailure(int x, int y) {
        var result = Location.create(x, y);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError()).isNotNull();
    }

    @Test
    void equal_shouldWorkCorrectly() {
        var first = Location.create(1, 1);
        var second = Location.create(1, 1);

        assertThat(first.getValue().equals(second.getValue())).isTrue();
        assertThat(first.getValue().getX()).isEqualTo(second.getValue().getX());
        assertThat(first.getValue().getY()).isEqualTo(second.getValue().getY());
    }

    @ParameterizedTest
    @CsvSource({ "1, 1, 2, 1", "1, 1, 1, 2", "1, 1, 2, 2", })
    void equal_whenXNotEqual_shouldReturnFalse(int firstX, int firstY, int secondX, int secondY) {
        var first = Location.create(firstX, firstY);
        var second = Location.create(secondX, secondY);

        assertThat(first.getValue().equals(second.getValue())).isFalse();
    }

    @ParameterizedTest
    @CsvSource({ "2, 3, 6, 8, 9", "1, 1, 10, 10, 18", "7, 8, 3, 1, 11", "7, 7, 7, 7, 0" })
    void distanceTo_shouldWorkCorrectly(int firstX, int firstY, int secondX, int secondY, int expectedDistance) {
        var first = Location.create(firstX, firstY);
        var second = Location.create(secondX, secondY);

        var result = first.getValue().distanceTo(second.getValue());
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue()).isEqualTo(expectedDistance);
    }
}
