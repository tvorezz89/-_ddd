package microarch.delivery.core.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.lang.reflect.Modifier;

import static org.assertj.core.api.Assertions.assertThat;

class LocationTest {

    @Test
    void derivedAggregate() {
        assertThat(Location.class.getSuperclass().getSimpleName()).isEqualTo("ValueObject");
    }

    @Test
    void shouldBeCorrectWhenParamsAreCorrectOnCreated() {
        var result = Location.create(5, 7);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue().getX()).isEqualTo(5);
        assertThat(result.getValue().getY()).isEqualTo(7);
    }

    @ParameterizedTest
    @CsvSource({ "1, 1", "10, 10", "1, 10", "10, 1" })
    void shouldBeCorrectWhenParamsAreOnBoundaries(int x, int y) {
        var result = Location.create(x, y);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue().getX()).isEqualTo(x);
        assertThat(result.getValue().getY()).isEqualTo(y);
    }

    @ParameterizedTest
    @CsvSource({ "0, 1", "11, 1", "1, 0", "1, 11", "0, 0", "11, 11" })
    void shouldReturnErrorWhenParamsAreOutOfBoard(int x, int y) {
        var result = Location.create(x, y);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getError()).isNotNull();
    }

    @Test
    void shouldBeEqualWhenAllPropertiesIsEqual() {
        var first = Location.mustCreate(3, 4);
        var second = Location.mustCreate(3, 4);

        assertThat(first).isEqualTo(second);
        assertThat(first.hashCode()).isEqualTo(second.hashCode());
    }

    @ParameterizedTest
    @CsvSource({ "3, 5", "4, 4" })
    void shouldBeNotEqualWhenOneOfPropertiesIsNotEqual(int x, int y) {
        var first = Location.mustCreate(3, 4);
        var second = Location.mustCreate(x, y);

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void shouldBeImmutable() throws NoSuchFieldException {
        var xField = Location.class.getDeclaredField("x");
        var yField = Location.class.getDeclaredField("y");

        assertThat(Modifier.isFinal(xField.getModifiers())).isTrue();
        assertThat(Modifier.isFinal(yField.getModifiers())).isTrue();
    }

    @ParameterizedTest
    @CsvSource({ "1, 1, 3, 4, 5", "3, 4, 1, 1, 5", "5, 5, 5, 5, 0", "10, 1, 1, 10, 18" })
    void shouldCalculateDistanceToOtherLocation(int firstX, int firstY, int secondX, int secondY,
            int expectedDistance) {
        var first = Location.mustCreate(firstX, firstY);
        var second = Location.mustCreate(secondX, secondY);

        var distance = first.distanceTo(second);

        assertThat(distance).isEqualTo(expectedDistance);
    }

    @ParameterizedTest
    @CsvSource({ "5, 5, true", "5, 6, true", "6, 5, true", "5, 7, false", "7, 5, false", "6, 6, false" })
    void shouldKnowWhenOtherLocationIsWithinOneCell(int otherX, int otherY, boolean expectedResult) {
        var location = Location.mustCreate(5, 5);
        var other = Location.mustCreate(otherX, otherY);

        assertThat(location.isWithinOneCellFrom(other)).isEqualTo(expectedResult);
    }
}
