package microarch.delivery.core.domain;

import libs.errs.Result;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import libs.errs.Error;


import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Volume Value Object")
class VolumeTest {

    @Nested
    @DisplayName("Создание через фабрику create()")
    class CreateTests {

        @Test
        @DisplayName("должен успешно создать Volume с положительным значением")
        void shouldCreateVolumeWithPositiveValue() {
            Integer value = 10;

            Result<Volume, Error> result = Volume.create(value);

            assertThat(result.isSuccess()).isTrue();
            assertThat(result.getValueOrThrow().getValue()).isEqualTo(value);
        }

        @Test
        @DisplayName("должен успешно создать Volume с минимальным значением 1")
        void shouldCreateVolumeWithMinValue() {
            Integer value = 1;

            Result<Volume, Error> result = Volume.create(value);

            assertThat(result.isSuccess()).isTrue();
            assertThat(result.getValueOrThrow().getValue()).isEqualTo(value);
        }

        @Test
        @DisplayName("должен вернуть ошибку при null значении")
        void shouldFailWithNullValue() {
            Integer value = null;

            assertThrows(
                    NullPointerException.class,
                    () -> Volume.create(value)
            );
        }

        @Test
        @DisplayName("должен вернуть ошибку при нулевом значении")
        void shouldFailWithZeroValue() {
            Integer value = 0;

            Result<Volume, Error> result = Volume.create(value);

            assertThat(result.isFailure()).isTrue();
        }

        @Test
        @DisplayName("должен вернуть ошибку при отрицательном значении")
        void shouldFailWithNegativeValue() {
            Integer value = -5;

            Result<Volume, Error> result = Volume.create(value);

            assertThat(result.isFailure()).isTrue();
        }
    }

    @Nested
    @DisplayName("Равенство и hashCode")
    class EqualityTests {

        @Test
        @DisplayName("должны быть равны Volume с одинаковыми значениями")
        void shouldEqualVolumesWithSameValue() {
            Volume volume1 = Volume.mustCreate(50);
            Volume volume2 = Volume.mustCreate(50);

            assertThat(volume1).isEqualTo(volume2);
            assertThat(volume1.hashCode()).isEqualTo(volume2.hashCode());
        }

        @Test
        @DisplayName("не должны быть равны Volume с разными значениями")
        void shouldNotEqualVolumesWithDifferentValues() {
            Volume volume1 = Volume.mustCreate(50);
            Volume volume2 = Volume.mustCreate(100);

            assertThat(volume1).isNotEqualTo(volume2);
        }

        @Test
        @DisplayName("должен быть равен самому себе")
        void shouldEqualItself() {
            Volume volume = Volume.mustCreate(75);

            assertThat(volume).isEqualTo(volume);
        }

        @Test
        @DisplayName("не должен быть равен null")
        void shouldNotEqualNull() {
            Volume volume = Volume.mustCreate(75);

            assertThat(volume).isNotEqualTo(null);
        }
    }
}