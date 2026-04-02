package microarch.delivery.core.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VolumeTest {

    @Nested
    @DisplayName("create() — success cases")
    class CreateSuccess {

        @ParameterizedTest(name = "size={0}")
        @ValueSource(ints = {1, 2, 25, 50, 75, 99, 100})
        @DisplayName("should create volume for various valid sizes")
        void shouldCreateForValidSizes(int size) {
            var result = Volume.create(size);

            assertThat(result.isSuccess()).isTrue();
            assertThat(result.getValue().getSize()).isEqualTo(size);
        }
    }

    @Nested
    @DisplayName("create() — failure cases")
    class CreateFailure {

        @Test
        @DisplayName("should fail when size is below minimum (0)")
        void shouldFailWhenSizeBelowMin() {
            var result = Volume.create(0);

            assertThat(result.isSuccess()).isFalse();
        }

        @Test
        @DisplayName("should fail when size is above maximum (101)")
        void shouldFailWhenSizeAboveMax() {
            var result = Volume.create(101);

            assertThat(result.isSuccess()).isFalse();
        }

        @Test
        @DisplayName("should fail when size is negative")
        void shouldFailWhenSizeNegative() {
            var result = Volume.create(-1);

            assertThat(result.isSuccess()).isFalse();
        }

        @ParameterizedTest(name = "size={0}")
        @ValueSource(ints = {-1000, -1, 0, 101, 500, Integer.MAX_VALUE, Integer.MIN_VALUE})
        @DisplayName("should fail for various invalid sizes")
        void shouldFailForInvalidSizes(int size) {
            var result = Volume.create(size);

            assertThat(result.isSuccess()).isFalse();
        }
    }

    @Nested
    @DisplayName("mustCreate()")
    class MustCreate {

        @Test
        @DisplayName("should return Volume for valid size")
        void shouldReturnVolumeForValidSize() {
            var volume = Volume.mustCreate(42);

            assertThat(volume).isNotNull();
            assertThat(volume.getSize()).isEqualTo(42);
        }

        @Test
        @DisplayName("should throw when size is below minimum")
        void shouldThrowWhenSizeBelowMin() {
            assertThatThrownBy(() -> Volume.mustCreate(0))
                    .isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("should throw when size is above maximum")
        void shouldThrowWhenSizeAboveMax() {
            assertThatThrownBy(() -> Volume.mustCreate(101))
                    .isInstanceOf(RuntimeException.class);
        }
    }

    @Nested
    @DisplayName("Value Object equality (equals / hashCode)")
    class ValueObjectEquality {

        @Test
        @DisplayName("equal volumes should be equal via equals()")
        void equalVolumesShouldBeEqual() {
            var a = Volume.mustCreate(30);
            var b = Volume.mustCreate(30);

            assertThat(a).isEqualTo(b);
        }

        @Test
        @DisplayName("different volumes should not be equal")
        void differentVolumesShouldNotBeEqual() {
            var a = Volume.mustCreate(30);
            var b = Volume.mustCreate(70);

            assertThat(a).isNotEqualTo(b);
        }

        @Test
        @DisplayName("equal volumes should have same hashCode")
        void equalVolumesShouldHaveSameHashCode() {
            var a = Volume.mustCreate(55);
            var b = Volume.mustCreate(55);

            assertThat(a).hasSameHashCodeAs(b);
        }

        @Test
        @DisplayName("volume should not be equal to null")
        void shouldNotBeEqualToNull() {
            var volume = Volume.mustCreate(10);

            assertThat(volume).isNotEqualTo(null);
        }

        @Test
        @DisplayName("equalityComponents should contain size")
        void equalityComponentsShouldContainSize() {
            var volume = Volume.mustCreate(77);

            assertThat(volume.equalityComponents()).containsExactly(77);
        }
    }
}