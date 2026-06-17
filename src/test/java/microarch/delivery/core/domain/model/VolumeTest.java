package microarch.delivery.core.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Modifier;

import static org.assertj.core.api.Assertions.assertThat;

class VolumeTest {

    @Test
    void shouldCreateVolumeWhenValueIsPositive() {
        var result = Volume.create(7);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue().getValue()).isEqualTo(7);
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, -1 })
    void shouldReturnErrorWhenValueIsNotPositive(int value) {
        var result = Volume.create(value);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError()).isNotNull();
    }

    @Test
    void shouldBeEqualWhenValueIsEqual() {
        assertThat(Volume.mustCreate(3)).isEqualTo(Volume.mustCreate(3));
    }

    @Test
    void shouldAddVolumes() {
        var result = Volume.mustCreate(7).add(Volume.mustCreate(5));

        assertThat(result).isEqualTo(Volume.mustCreate(12));
    }

    @Test
    void shouldCompareVolumes() {
        assertThat(Volume.mustCreate(7).isLessThanOrEqualTo(Volume.mustCreate(7))).isTrue();
        assertThat(Volume.mustCreate(7).isLessThanOrEqualTo(Volume.mustCreate(8))).isTrue();
        assertThat(Volume.mustCreate(8).isLessThanOrEqualTo(Volume.mustCreate(7))).isFalse();
    }

    @Test
    void shouldCreateZeroVolumeForCalculations() {
        assertThat(Volume.zero().add(Volume.mustCreate(5))).isEqualTo(Volume.mustCreate(5));
    }

    @Test
    void shouldBeImmutable() throws NoSuchFieldException {
        var valueField = Volume.class.getDeclaredField("value");

        assertThat(Modifier.isFinal(valueField.getModifiers())).isTrue();
    }
}
