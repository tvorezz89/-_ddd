package microarch.delivery.core.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class LocationTest {

    @Test
    void derivedValueObject() {
        assertThat(Location.class.getSuperclass().getSimpleName()).isEqualTo("ValueObject");
    }

    @Test
    void shouldBeCorrectWhenParamsAreCorrectOnCreated() {
        // Arrange

        // Act
        var result = Location.create(5, 5);

        // Assert
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue().getX()).isEqualTo(5);
        assertThat(result.getValue().getY()).isEqualTo(5);
    }

    @Test
    void shouldBeCreatedOnMinBorders() {
        // Act
        var result = Location.create(1, 1);

        // Assert
        assertThat(result.isSuccess()).isTrue();
    }

    @Test
    void shouldBeCreatedOnMaxBorders() {
        // Act
        var result = Location.create(10, 10);

        // Assert
        assertThat(result.isSuccess()).isTrue();
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, -1, 11, 100 })
    void shouldReturnErrorWhenXIsNotCorrectOnCreated(int x) {
        // Act
        var result = Location.create(x, 5);

        // Assert
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getError()).isNotNull();
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, -1, 11, 100 })
    void shouldReturnErrorWhenYIsNotCorrectOnCreated(int y) {
        // Act
        var result = Location.create(5, y);

        // Assert
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getError()).isNotNull();
    }

    @Test
    void shouldBeEqualWhenAllPropertiesIsEqual() {
        // Arrange
        var first = Location.mustCreate(5, 5);
        var second = Location.mustCreate(5, 5);

        // Act
        var result = first.equals(second);

        // Assert
        assertThat(result).isTrue();
        assertThat(first.hashCode()).isEqualTo(second.hashCode());
    }

    @Test
    void shouldBeNotEqualWhenXIsNotEqual() {
        // Arrange
        var first = Location.mustCreate(5, 5);
        var second = Location.mustCreate(6, 5);

        // Act
        var result = first.equals(second);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void shouldBeNotEqualWhenYIsNotEqual() {
        // Arrange
        var first = Location.mustCreate(5, 5);
        var second = Location.mustCreate(5, 6);

        // Act
        var result = first.equals(second);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void shouldBeEqualToItself() {
        // Arrange
        var location = Location.mustCreate(3, 7);

        // Act
        var result = location.equals(location);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void shouldNotBeEqualToNull() {
        // Arrange
        var location = Location.mustCreate(3, 7);

        // Act
        var result = location.equals(null);

        // Assert
        assertThat(result).isFalse();
    }

    @ParameterizedTest
    @CsvSource({ "1, 1, 10, 10, 18", "5, 5, 5, 5, 0", "1, 1, 4, 4, 6", "2, 3, 4, 6, 5" })
    void shouldCalculateDistanceCorrectly(int x1, int y1, int x2, int y2, int expected) {
        // Arrange
        var first = Location.mustCreate(x1, y1);
        var second = Location.mustCreate(x2, y2);

        // Act
        var distance = first.distanceTo(second);

        // Assert
        assertThat(distance).isEqualTo(expected);
    }

    @Test
    void distanceShouldBeSymmetric() {
        // Arrange
        var first = Location.mustCreate(2, 3);
        var second = Location.mustCreate(4, 6);

        // Act
        var d1 = first.distanceTo(second);
        var d2 = second.distanceTo(first);

        // Assert
        assertThat(d1).isEqualTo(d2).isEqualTo(5);
    }
}
