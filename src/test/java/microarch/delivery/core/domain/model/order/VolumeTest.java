package microarch.delivery.core.domain.model.order;

import libs.errs.DomainInvariantException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VolumeTest {

    @Test
    void ofShouldCreateVolumeWhenValueIsPositive() {
        var volume = Volume.of(3);
        assertEquals(3, volume.getValue());
    }

    @Test
    void ofShouldThrowWhenValueIsZero() {
        assertThrows(DomainInvariantException.class, () -> Volume.of(0));
    }
}
