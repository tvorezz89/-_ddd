package microarch.delivery.core.domain.model.courier;

import libs.errs.Error;
import libs.errs.Result;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VolumeTest {

    @Test
    void create_success() {
        Result<Volume, Error> volume = Volume.create(10);
        assertTrue(volume.isSuccess());
        assertEquals(10, volume.getValue().getVolume());
    }

    @Test
    void create_failure() {
        Result<Volume, Error> volume = Volume.create(0);
        assertTrue(volume.isFailure());
        assertEquals("value.must.be.greater.than", volume.getError().getCode());
    }
}