package microarch.delivery.core.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class LocationTest {

    @Nested
    @DisplayName("create() — success cases")
    class CreateSuccess {

        @Test
        @DisplayName("should create location with minimum coordinates (1, 1)")
        void shouldCreateWithMinCoordinates() {
            var result = Location.create(1, 1);

            assertThat(result.isSuccess()).isTrue();
            assertThat(result.getValue().getLatitude()).isEqualTo(1);
            assertThat(result.getValue().getLongitude()).isEqualTo(1);
        }

        @Test
        @DisplayName("should create location with maximum coordinates (10, 10)")
        void shouldCreateWithMaxCoordinates() {
            var result = Location.create(10, 10);

            assertThat(result.isSuccess()).isTrue();
            assertThat(result.getValue().getLatitude()).isEqualTo(10);
            assertThat(result.getValue().getLongitude()).isEqualTo(10);
        }

        @Test
        @DisplayName("should create location with mid-range coordinates")
        void shouldCreateWithMidRangeCoordinates() {
            var result = Location.create(5, 7);

            assertThat(result.isSuccess()).isTrue();
            assertThat(result.getValue().getLatitude()).isEqualTo(5);
            assertThat(result.getValue().getLongitude()).isEqualTo(7);
        }

        @ParameterizedTest(name = "latitude={0}, longitude={1}")
        @CsvSource({"1,1", "1,10", "10,1", "10,10", "5,5", "3,8"})
        @DisplayName("should create location for all valid boundary combinations")
        void shouldCreateForValidBoundaryCombinations(int latitude, int longitude) {
            var result = Location.create(latitude, longitude);

            assertThat(result.isSuccess()).isTrue();
            assertThat(result.getValue().getLatitude()).isEqualTo(latitude);
            assertThat(result.getValue().getLongitude()).isEqualTo(longitude);
        }
    }

    @Nested
    @DisplayName("create() — failure cases")
    class CreateFailure {

        @Test
        @DisplayName("should fail when latitude is below minimum (0)")
        void shouldFailWhenLatitudeBelowMin() {
            var result = Location.create(0, 5);

            assertThat(result.isSuccess()).isFalse();
        }

        @Test
        @DisplayName("should fail when latitude is above maximum (11)")
        void shouldFailWhenLatitudeAboveMax() {
            var result = Location.create(11, 5);

            assertThat(result.isSuccess()).isFalse();
        }

        @Test
        @DisplayName("should fail when longitude is below minimum (0)")
        void shouldFailWhenLongitudeBelowMin() {
            var result = Location.create(5, 0);

            assertThat(result.isSuccess()).isFalse();
        }

        @Test
        @DisplayName("should fail when longitude is above maximum (11)")
        void shouldFailWhenLongitudeAboveMax() {
            var result = Location.create(5, 11);

            assertThat(result.isSuccess()).isFalse();
        }

        @Test
        @DisplayName("should fail when both coordinates are below minimum")
        void shouldFailWhenBothBelowMin() {
            var result = Location.create(0, 0);

            assertThat(result.isSuccess()).isFalse();
        }

        @Test
        @DisplayName("should fail when both coordinates are above maximum")
        void shouldFailWhenBothAboveMax() {
            var result = Location.create(11, 11);

            assertThat(result.isSuccess()).isFalse();
        }

        @ParameterizedTest(name = "latitude={0}")
        @ValueSource(ints = {-100, -1, 0, 11, 50, Integer.MAX_VALUE, Integer.MIN_VALUE})
        @DisplayName("should fail for various invalid latitude values")
        void shouldFailForInvalidLatitudes(int latitude) {
            var result = Location.create(latitude, 5);

            assertThat(result.isSuccess()).isFalse();
        }

        @ParameterizedTest(name = "longitude={0}")
        @ValueSource(ints = {-100, -1, 0, 11, 50, Integer.MAX_VALUE, Integer.MIN_VALUE})
        @DisplayName("should fail for various invalid longitude values")
        void shouldFailForInvalidLongitudes(int longitude) {
            var result = Location.create(5, longitude);

            assertThat(result.isSuccess()).isFalse();
        }
    }

    @Nested
    @DisplayName("mustCreate()")
    class MustCreate {

        @Test
        @DisplayName("should return Location for valid coordinates")
        void shouldReturnLocationForValidCoordinates() {
            var location = Location.mustCreate(3, 7);

            assertThat(location).isNotNull();
            assertThat(location.getLatitude()).isEqualTo(3);
            assertThat(location.getLongitude()).isEqualTo(7);
        }

        @Test
        @DisplayName("should throw when latitude is out of range")
        void shouldThrowWhenLatitudeInvalid() {
            assertThatThrownBy(() -> Location.mustCreate(0, 5))
                    .isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("should throw when longitude is out of range")
        void shouldThrowWhenLongitudeInvalid() {
            assertThatThrownBy(() -> Location.mustCreate(5, 11))
                    .isInstanceOf(RuntimeException.class);
        }
    }

    @Nested
    @DisplayName("equals() / hashcode()")
    class ValueObjectEquality {

        @Test
        @DisplayName("should return true for locations with identical coordinates")
        void shouldReturnTrueForIdenticalCoordinates() {
            var a = Location.mustCreate(5, 5);
            var b = Location.mustCreate(5, 5);

            assertThat(a).isEqualTo(b);
        }

        @Test
        @DisplayName("should return true when compared to itself")
        void shouldReturnTrueForSameInstance() {
            var location = Location.mustCreate(3, 7);

            assertThat(location).isEqualTo(location);
        }

        @Test
        @DisplayName("should return false when latitude differs")
        void shouldReturnFalseWhenLatitudeDiffers() {
            var a = Location.mustCreate(3, 5);
            var b = Location.mustCreate(4, 5);

            assertThat(a).isNotEqualTo(b);
        }

        @Test
        @DisplayName("should return false when longitude differs")
        void shouldReturnFalseWhenLongitudeDiffers() {
            var a = Location.mustCreate(5, 3);
            var b = Location.mustCreate(5, 4);

            assertThat(a).isNotEqualTo(b);
        }

        @Test
        @DisplayName("should return false when both coordinates differ")
        void shouldReturnFalseWhenBothDiffer() {
            var a = Location.mustCreate(1, 1);
            var b = Location.mustCreate(10, 10);

            assertThat(a).isNotEqualTo(b);
        }

        @Test
        @DisplayName("should be symmetric — a.same(b) == b.same(a)")
        void shouldBeSymmetric() {
            var a = Location.mustCreate(2, 8);
            var b = Location.mustCreate(2, 8);

            assertThat(a.equals(b)).isEqualTo(b.equals(a));
        }

        @Test
        @DisplayName("should be symmetric for different locations too")
        void shouldBeSymmetricForDifferent() {
            var a = Location.mustCreate(1, 3);
            var b = Location.mustCreate(7, 9);

            assertThat(a.equals(b)).isEqualTo(b.equals(a));
        }

        @Test
        @DisplayName("equal locations should have same hashCode")
        void equalLocationsShouldHaveSameHashCode() {
            var a = Location.mustCreate(7, 3);
            var b = Location.mustCreate(7, 3);

            assertThat(a).hasSameHashCodeAs(b);
        }

        @Test
        @DisplayName("location should not be equal to null")
        void shouldNotBeEqualToNull() {
            var location = Location.mustCreate(5, 5);

            assertThat(location).isNotEqualTo(null);
        }

        @Test
        @DisplayName("equalityComponents should contain latitude and longitude")
        void equalityComponentsShouldContainBothCoordinates() {
            var location = Location.mustCreate(3, 9);

            assertThat(location.equalityComponents()).containsExactly(3, 9);
        }
    }

    @Nested
    @DisplayName("calculateDistance()")
    class CalculateDistance {

        @Test
        @DisplayName("distance to itself should be 0")
        void distanceToItselfShouldBeZero() {
            var location = Location.mustCreate(5, 5);

            assertThat(location.calculateDistance(location)).isZero();
        }

        @Test
        @DisplayName("distance between identical locations should be 0")
        void distanceBetweenIdenticalShouldBeZero() {
            var a = Location.mustCreate(3, 7);
            var b = Location.mustCreate(3, 7);

            assertThat(a.calculateDistance(b)).isZero();
        }

        @Test
        @DisplayName("distance should be |lat1-lat2| + |lon1-lon2|")
        void shouldCalculateManhattanDistance() {
            var a = Location.mustCreate(2, 6);
            var b = Location.mustCreate(4, 9);

            assertThat(a.calculateDistance(b)).isEqualTo(5);
        }

        @Test
        @DisplayName("distance only along latitude axis")
        void distanceAlongLatitudeOnly() {
            var a = Location.mustCreate(2, 5);
            var b = Location.mustCreate(8, 5);

            assertThat(a.calculateDistance(b)).isEqualTo(6);
        }

        @Test
        @DisplayName("distance only along longitude axis")
        void distanceAlongLongitudeOnly() {
            var a = Location.mustCreate(5, 1);
            var b = Location.mustCreate(5, 10);

            assertThat(a.calculateDistance(b)).isEqualTo(9);
        }

        @Test
        @DisplayName("maximum possible distance (1,1) -> (10,10) = 18")
        void maximumPossibleDistance() {
            var a = Location.mustCreate(1, 1);
            var b = Location.mustCreate(10, 10);

            assertThat(a.calculateDistance(b)).isEqualTo(18);
        }

        @Test
        @DisplayName("distance should be symmetric — a.dist(b) == b.dist(a)")
        void distanceShouldBeSymmetric() {
            var a = Location.mustCreate(2, 3);
            var b = Location.mustCreate(8, 9);

            assertThat(a.calculateDistance(b)).isEqualTo(b.calculateDistance(a));
        }

        @Test
        @DisplayName("distance should always be non-negative")
        void distanceShouldBeNonNegative() {
            var a = Location.mustCreate(10, 10);
            var b = Location.mustCreate(1, 1);

            assertThat(a.calculateDistance(b)).isGreaterThanOrEqualTo(0);
            assertThat(b.calculateDistance(a)).isGreaterThanOrEqualTo(0);
        }

        @ParameterizedTest(name = "({0},{1}) -> ({2},{3}) = {4}")
        @CsvSource({
                "1,1,  1,1,   0",
                "1,1,  1,2,   1",
                "1,1,  2,1,   1",
                "3,4,  6,8,   7",
                "10,10, 1,1,  18",
                "5,5,  5,5,   0",
                "1,10, 10,1,  18"
        })
        @DisplayName("should calculate correct distance for various coordinate pairs")
        void shouldCalculateCorrectDistance(int lat1, int lon1, int lat2, int lon2, int expected) {
            var a = Location.mustCreate(lat1, lon1);
            var b = Location.mustCreate(lat2, lon2);

            assertThat(a.calculateDistance(b)).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("isInReachingDistance()")
    class IsInReachingDistance {

        @Test
        @DisplayName("should return false when distance exceeds MAX_DISTANCE_FOR_REACHING")
        void shouldReturnFalseWhenTooFar() {
            var a = Location.mustCreate(5, 5);
            var b = Location.mustCreate(5, 7);

            assertThat(a.isInReachingDistance(b)).isFalse();
        }

        @ParameterizedTest(name = "neighbor: latitude={0}, longitude={1}")
        @DisplayName("should return true for diagonal neighbor (distance = 2)")
        @CsvSource({
                "4, 4",
                "6, 4",
                "4, 6",
                "6, 6"
        })
        void shouldReturnTrueForDiagonalNeighbor(int otherX, int otherY) {
            var a = Location.mustCreate(5, 5);
            var b = Location.mustCreate(otherX, otherY);

            assertThat(a.isInReachingDistance(b)).isTrue();
        }

        @ParameterizedTest(name = "neighbor: latitude={0}, longitude={1}")
        @DisplayName("should return true for lateral neighbor (distance = 1)")
        @CsvSource({
                "5, 4",
                "6, 5",
                "4, 5",
                "5, 6"
        })
        void shouldReturnTrueForLateralNeighbor(int otherX, int otherY) {
            var a = Location.mustCreate(5, 5);
            var b = Location.mustCreate(otherX, otherY);

            assertThat(a.isInReachingDistance(b)).isTrue();
        }

        @Test
        @DisplayName("should return false for maximum possible distance")
        void shouldReturnFalseForMaxPossibleDistance() {
            var a = Location.mustCreate(1, 1);
            var b = Location.mustCreate(10, 10);

            assertThat(a.isInReachingDistance(b)).isFalse();
        }

        @Test
        @DisplayName("should be symmetric — a.inReach(b) == b.inReach(a)")
        void shouldBeSymmetric() {
            var a = Location.mustCreate(5, 5);
            var b = Location.mustCreate(5, 6);

            assertThat(a.isInReachingDistance(b)).isEqualTo(b.isInReachingDistance(a));
        }
    }
}