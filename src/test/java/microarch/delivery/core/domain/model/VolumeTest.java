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
    void shouldBeImmutable() throws NoSuchFieldException {
        var valueField = Volume.class.getDeclaredField("value");

        assertThat(Modifier.isFinal(valueField.getModifiers())).isTrue();
    }
}
