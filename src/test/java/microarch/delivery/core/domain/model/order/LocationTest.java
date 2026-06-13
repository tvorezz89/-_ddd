package microarch.delivery.core.domain.model.order;

import libs.errs.DomainInvariantException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LocationTest {

    @Test
    void isOneCellOrCloserToShouldReturnTrueForNeighborCell() {
        var location = Location.of(10, 10);
        assertTrue(location.isOneCellOrCloserTo(Location.of(11, 9)));
    }

    @Test
    void isOneCellOrCloserToShouldReturnFalseForFarCell() {
        var location = Location.of(10, 10);
        assertFalse(location.isOneCellOrCloserTo(Location.of(12, 10)));
    }

    @Test
    void isOneCellOrCloserToShouldThrowForNullLocation() {
        var location = Location.of(10, 10);
        assertThrows(DomainInvariantException.class, () -> location.isOneCellOrCloserTo(null));
    }
}
