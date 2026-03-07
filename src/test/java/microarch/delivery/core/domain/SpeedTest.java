package microarch.delivery.core.domain;

import libs.errs.DomainInvariantException;
import libs.errs.Result;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class SpeedTest {

    @DisplayName("Создание скорости успешно, если значение положительное")
    @Test
    void create_shouldSuccess_whenSpeedIsPositive() {
        Result<Speed, libs.errs.Error> result = Speed.create(10);

        assertTrue(result.isSuccess());
        assertNotNull(result.getValue());
        assertEquals(10, result.getValue().getSpeed());
    }

    @DisplayName("Создание скорости неудачно, если значение null")
    @Test
    void create_shouldFail_whenSpeedIsNull() {
        assertThrows(NullPointerException.class, () -> Speed.create(null));
    }

    @DisplayName("Создание скорости неудачно, если значение меньше или равно нулю")
    @ParameterizedTest
    @ValueSource(ints = {0, -1, -100})
    void create_shouldFail_whenSpeedIsZeroOrNegative(int invalidSpeed) {
        Result<Speed, libs.errs.Error> result = Speed.create(invalidSpeed);

        assertTrue(result.isFailure());
        assertEquals(Speed.Errors.speedShouldBePositive(), result.getError());
    }

    @DisplayName("mustCreate возвращает объект при валидном значении")
    @Test
    void mustCreate_shouldReturnObject_whenSpeedIsValid() {
        Speed speed = Speed.mustCreate(5);

        assertNotNull(speed);
        assertEquals(5, speed.getSpeed());
    }

    @DisplayName("mustCreate выбрасывает исключение при невалидном значении")
    @ParameterizedTest
    @ValueSource(ints = {0, -5})
    void mustCreate_shouldThrow_whenSpeedIsInvalid(int invalidSpeed) {
        assertThrows(DomainInvariantException.class, () -> Speed.mustCreate(invalidSpeed));
    }

    @DisplayName("Расчет шагов: корректные значения расстояния и скорости")
    @ParameterizedTest
    @CsvSource({
            "1, 1, 1",    // 1 / 1 = 1
            "2, 5, 3",    // 5 / 2 = 2.5 -> ceil -> 3
            "3, 10, 4",   // 10 / 3 = 3.33 -> ceil -> 4
            "4, 8, 2",    // 8 / 4 = 2
            "1, 10, 10"    // 10 / 1 = 10
    })
    void calculateStepsForDistance_shouldReturnCorrectSteps(int speedValue, int distance, int expectedSteps) {
        Speed speed = Speed.mustCreate(speedValue);

        int steps = speed.calculateStepsForDistance(distance);

        assertEquals(expectedSteps, steps);
    }

    @DisplayName("Расчет шагов: выбрасывает исключение, если расстояние null")
    @Test
    void calculateStepsForDistance_shouldThrow_whenDistanceIsNull() {
        Speed speed = Speed.mustCreate(5);

        assertThrows(NullPointerException.class, () -> speed.calculateStepsForDistance(null));
    }

    @DisplayName("Расчет шагов: выбрасывает исключение, если расстояние отрицательное")
    @Test
    void calculateStepsForDistance_shouldThrow_whenDistanceIsNegative() {
        Speed speed = Speed.mustCreate(5);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> speed.calculateStepsForDistance(-10));

        assertEquals(Speed.Errors.distanceShouldBePositive().getCode(), exception.getMessage());
    }

    @DisplayName("Расчет шагов: расстояние 0 должно возвращать 0 шагов")
    @Test
    void calculateStepsForDistance_shouldReturnZero_whenDistanceIsZero() {
        Speed speed = Speed.mustCreate(5);

        int steps = speed.calculateStepsForDistance(0);

        assertEquals(0, steps);
    }

    @DisplayName("Проверка равенства объектов Speed (ValueObject)")
    @Test
    void equality_shouldWorkBasedOnSpeedValue() {
        Speed speed1 = Speed.mustCreate(10);
        Speed speed2 = Speed.mustCreate(10);
        Speed speed3 = Speed.mustCreate(20);

        assertEquals(speed1, speed2);
        assertEquals(speed1.hashCode(), speed2.hashCode());
        assertNotEquals(speed1, speed3);
    }
}