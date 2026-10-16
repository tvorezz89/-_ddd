package microarch.delivery.core.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VolumeTest {

    @Test
    void shouldBeCreatedWhenValueIsPositive() {
        // Act
        var result = Volume.create(10);

        // Assert
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue().getValue()).isEqualTo(10);
    }

    @Test
    void shouldReturnErrorWhenValueIsNegative() {
        // Act
        var result = Volume.create(-1);

        // Assert
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError()).isNotNull();
    }

    @Test
    void shouldBeEqualWhenValuesAreEqual() {
        // Arrange
        var first = Volume.mustCreate(7);
        var second = Volume.mustCreate(7);

        // Act
        var result = first.equals(second);

        // Assert
        assertThat(result).isTrue();
        assertThat(first.hashCode()).isEqualTo(second.hashCode());
    }

    @Test
    void shouldBeNotEqualWhenValuesAreDifferent() {
        // Arrange
        var first = Volume.mustCreate(7);
        var second = Volume.mustCreate(8);

        // Act
        var result = first.equals(second);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void shouldAddVolumes() {
        // Arrange
        var first = Volume.mustCreate(3);
        var second = Volume.mustCreate(4);

        // Act
        var result = first.add(second);

        // Assert
        assertThat(result.getValue()).isEqualTo(7);
    }

    @Test
    void zeroShouldHaveZeroValue() {
        assertThat(Volume.ZERO.getValue()).isEqualTo(0);
    }
}
