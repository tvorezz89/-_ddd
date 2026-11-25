package microarch.delivery.core.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class VolumeTest {
    @Test
    void volumeMustBePositive() {
        assertEquals(20.0, Volume.create(20).getAmount());
        assertThrows(IllegalArgumentException.class, () -> Volume.create(0));
        assertThrows(IllegalArgumentException.class, () -> Volume.create(-1));
    }
}
