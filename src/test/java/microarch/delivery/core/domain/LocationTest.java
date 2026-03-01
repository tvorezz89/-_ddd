package microarch.delivery.core.domain;

import static org.junit.jupiter.api.Assertions.*;

import libs.errs.Error;
import libs.errs.Result;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class LocationTest {

    @Nested
    @DisplayName("Создание Location")
    class Creation {

        @Test
        @DisplayName("Успешное создание с минимальными координатами 1,1")
        void shouldCreateLocationWithMinCoordinates() {
            Result<Location, Error> result = Location.create(Location.MIN_COORDINATE, Location.MIN_COORDINATE);

            assertTrue(result.isSuccess());
            Location location = result.getValue();
            assertEquals(Location.MIN_COORDINATE, location.getX());
            assertEquals(Location.MIN_COORDINATE, location.getY());
        }

        @Test
        @DisplayName("Успешное создание с максимальными координатами 10,10")
        void shouldCreateLocationWithMaxCoordinates() {
            Result<Location, Error> result = Location.create(Location.MAX_COORDINATE, Location.MAX_COORDINATE);

            assertTrue(result.isSuccess());
            Location location = result.getValue();
            assertEquals(Location.MAX_COORDINATE, location.getX());
            assertEquals(Location.MAX_COORDINATE, location.getY());
        }

        @Test
        @DisplayName("Успешное создание с координатами в допустимом диапазоне")
        void shouldCreateLocationWithValidCoordinates() {
            Result<Location, Error> result = Location.create(5, 7);

            assertTrue(result.isSuccess());
            Location location = result.getValue();
            assertEquals(5, location.getX());
            assertEquals(7, location.getY());
        }

        @Test
        @DisplayName("Ошибка при создании с X меньше минимального")
        void shouldFailWhenXIsLessThanMin() {
            Result<Location, Error> result = Location.create(0, 5);

            assertTrue(result.isFailure());
            assertNotNull(result.getError());
        }

        @Test
        @DisplayName("Ошибка при создании с X больше максимального")
        void shouldFailWhenXIsGreaterThanMax() {
            Result<Location, Error> result = Location.create(11, 5);

            assertTrue(result.isFailure());
            assertNotNull(result.getError());
        }

        @Test
        @DisplayName("Ошибка при создании с Y меньше минимального")
        void shouldFailWhenYIsLessThanMin() {
            Result<Location, Error> result = Location.create(5, 0);

            assertTrue(result.isFailure());
            assertNotNull(result.getError());
        }

        @Test
        @DisplayName("Ошибка при создании с Y больше максимального")
        void shouldFailWhenYIsGreaterThanMax() {
            Result<Location, Error> result = Location.create(5, 11);

            assertTrue(result.isFailure());
            assertNotNull(result.getError());
        }
    }

    @Nested
    @DisplayName("Проверка эквивалентности Location")
    class Equality {

        @Test
        @DisplayName("Две координаты равны, если их X и Y равны")
        void shouldEqualWhenXAndYAreEqual() {
            Location location1 = Location.create(5, 7).getValue();
            Location location2 = Location.create(5, 7).getValue();

            assertEquals(location1, location2);
            assertEquals(location1.hashCode(), location2.hashCode());
        }

        @Test
        @DisplayName("Две координаты не равны, если X различается")
        void shouldNotEqualWhenXDiffers() {
            Location location1 = Location.create(5, 7).getValue();
            Location location2 = Location.create(6, 7).getValue();

            assertNotEquals(location1, location2);
        }

        @Test
        @DisplayName("Две координаты не равны, если Y различается")
        void shouldNotEqualWhenYDiffers() {
            Location location1 = Location.create(5, 7).getValue();
            Location location2 = Location.create(5, 8).getValue();

            assertNotEquals(location1, location2);
        }

        @Test
        @DisplayName("Две координаты не равны, если X и Y различаются")
        void shouldNotEqualWhenBothDiffer() {
            Location location1 = Location.create(5, 7).getValue();
            Location location2 = Location.create(6, 8).getValue();

            assertNotEquals(location1, location2);
        }

        @Test
        @DisplayName("Координата равна самой себе")
        void shouldEqualItself() {
            Location location = Location.create(5, 7).getValue();

            assertEquals(location, location);
        }

        @Test
        @DisplayName("Координата не равна null")
        void shouldNotEqualNull() {
            Location location = Location.create(5, 7).getValue();

            assertNotEquals(null, location);
        }
    }

    @Nested
    @DisplayName("Расчет расстояния между Location")
    class Distance {

        @Test
        @DisplayName("Расстояние между одинаковыми координатами равно 0")
        void shouldReturnZeroDistanceForSameLocation() {
            Location courier = Location.create(5, 7).getValue();
            Location order = Location.create(5, 7).getValue();

            int distance = courier.distanceTo(order).getValue();

            assertEquals(0, distance);
        }

        @Test
        @DisplayName("Расстояние между (1;1) и (3;4)")
        void shouldCalculateManhattanDistance() {
            Location courier = Location.create(1, 1).getValue();
            Location order = Location.create(3, 4).getValue();

            int distance = courier.distanceTo(order).getValue();

            assertEquals(5, distance);
        }

        @Test
        @DisplayName("Расстояние между (1;5) и (8;5)")
        void shouldCalculateHorizontalDistance() {
            Location courier = Location.create(1, 5).getValue();
            Location order = Location.create(8, 5).getValue();

            int distance = courier.distanceTo(order).getValue();

            assertEquals(7, distance);
        }

        @Test
        @DisplayName("Расстояние между (5;1) и (5;9)")
        void shouldCalculateVerticalDistance() {
            Location courier = Location.create(5, 1).getValue();
            Location order = Location.create(5, 9).getValue();

            int distance = courier.distanceTo(order).getValue();

            assertEquals(8, distance);
        }

        @Test
        @DisplayName("Расстояние между минимальной и максимальной координатами")
        void shouldCalculateMaxDistance() {
            Location min = Location.create(Location.MIN_COORDINATE, Location.MIN_COORDINATE).getValue();
            Location max = Location.create(Location.MAX_COORDINATE, Location.MAX_COORDINATE).getValue();

            int distance = min.distanceTo(max).getValue();

            assertEquals(18, distance);
        }

        @Test
        @DisplayName("Расстояние (2;3) и (7;9)")
        void shouldCalculateSymmetricDistance() {
            Location courier = Location.create(2, 3).getValue();
            Location order = Location.create(7, 9).getValue();

            int distance1to2 = courier.distanceTo(order).getValue();
            int distance2to1 = order.distanceTo(courier).getValue();

            assertEquals(distance1to2, distance2to1);
        }

        @Test
        @DisplayName("Бросает исключение при расчете расстояния до null")
        void shouldThrowExceptionWhenOtherIsNull() {
            Location location = Location.create(5, 7).getValue();

            Result<Integer, Error> distance = location.distanceTo(null);

            assertTrue(distance.isFailure());
        }
    }
}