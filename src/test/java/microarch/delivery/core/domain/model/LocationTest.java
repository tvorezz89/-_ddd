package microarch.delivery.core.domain.model;

import libs.errs.Error;
import libs.errs.Result;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LocationTest {

    // --- create: valid ---

    @Test
    void create_shouldSucceed_whenCoordinatesAreMinBoundary() {
        Result<Location, Error> result = Location.create(1, 1);
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue().getX()).isEqualTo(1);
        assertThat(result.getValue().getY()).isEqualTo(1);
    }

    @Test
    void create_shouldSucceed_whenCoordinatesAreMaxBoundary() {
        Result<Location, Error> result = Location.create(10, 10);
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue().getX()).isEqualTo(10);
        assertThat(result.getValue().getY()).isEqualTo(10);
    }

    @Test
    void create_shouldSucceed_whenCoordinatesAreInRange() {
        Result<Location, Error> result = Location.create(5, 7);
        assertThat(result.isSuccess()).isTrue();
    }

    // --- create: invalid x ---

    @Test
    void create_shouldFail_whenXIsLessThanMin() {
        Result<Location, Error> result = Location.create(0, 5);
        assertThat(result.isFailure()).isTrue();
    }

    @Test
    void create_shouldFail_whenXIsGreaterThanMax() {
        Result<Location, Error> result = Location.create(11, 5);
        assertThat(result.isFailure()).isTrue();
    }

    // --- create: invalid y ---

    @Test
    void create_shouldFail_whenYIsLessThanMin() {
        Result<Location, Error> result = Location.create(5, 0);
        assertThat(result.isFailure()).isTrue();
    }

    @Test
    void create_shouldFail_whenYIsGreaterThanMax() {
        Result<Location, Error> result = Location.create(5, 11);
        assertThat(result.isFailure()).isTrue();
    }

    // --- equality ---

    @Test
    void equals_shouldReturnTrue_whenXAndYAreEqual() {
        Location a = Location.create(3, 4).getValue();
        Location b = Location.create(3, 4).getValue();
        assertThat(a).isEqualTo(b);
    }

    @Test
    void equals_shouldReturnFalse_whenXDiffers() {
        Location a = Location.create(3, 4).getValue();
        Location b = Location.create(5, 4).getValue();
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void equals_shouldReturnFalse_whenYDiffers() {
        Location a = Location.create(3, 4).getValue();
        Location b = Location.create(3, 7).getValue();
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void hashCode_shouldBeEqual_whenLocationsAreEqual() {
        Location a = Location.create(3, 4).getValue();
        Location b = Location.create(3, 4).getValue();
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    // --- calculateDistance ---

    @Test
    void calculateDistance_shouldReturnZero_whenLocationsAreEqual() {
        Location a = Location.create(5, 5).getValue();
        Location b = Location.create(5, 5).getValue();
        assertThat(a.calculateDistance(b)).isEqualTo(0);
    }

    @Test
    void calculateDistance_shouldReturnCorrectValue() {
        // 2 шага по X + 3 шага по Y = 5
        Location a = Location.create(1, 1).getValue();
        Location b = Location.create(3, 4).getValue();
        assertThat(a.calculateDistance(b)).isEqualTo(5);
    }

    @Test
    void calculateDistance_shouldBeSymmetric() {
        Location a = Location.create(2, 3).getValue();
        Location b = Location.create(7, 6).getValue();
        assertThat(a.calculateDistance(b)).isEqualTo(b.calculateDistance(a));
    }
}
