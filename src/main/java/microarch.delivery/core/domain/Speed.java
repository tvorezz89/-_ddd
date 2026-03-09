package microarch.delivery.core.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import libs.ddd.ValueObject;
import libs.errs.Error;
import libs.errs.Result;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

@Getter
@Embeddable
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
public class Speed extends ValueObject<Speed> {

    public static final int ZERO_SPEED  = 0;
    public static final int ZERO_DISTANCE = 0;

    @Column(name = "speed")
    private final Integer speed;

    public static Result<Speed, libs.errs.Error> create(Integer speed) {
        Objects.requireNonNull(speed, "speed");
        if (speed <= ZERO_SPEED) {
            return Result.failure(Speed.Errors.speedShouldBePositive());
        }
        return Result.success(new Speed(speed));
    }

    public static Speed mustCreate(Integer value) {
        return create(value).getValueOrThrow();
    }

    public int calculateStepsForDistance(Integer distance) {
        Objects.requireNonNull(distance, "distance");
        if (distance < ZERO_DISTANCE) {
            throw new IllegalArgumentException(Errors.distanceShouldBePositive().getCode());
        }
        return (int) Math.ceil((double) distance / this.speed);
    }

    private Speed(Integer speed) {
        this.speed = speed;
    }

    @Override
    protected Iterable<Object> equalityComponents() {
        return List.of(speed);
    }

    public static final class Errors {
        public static libs.errs.Error speedShouldBePositive() {
            return Error.of(
                    "speed.should.be.positve",
                    "Скорость должна быть положительной"
            );
        }

        public static libs.errs.Error distanceShouldBePositive() {
            return Error.of(
                    "distance.should.be.positve",
                    "Дистанция должна быть положительной"
            );
        }
    }
}
