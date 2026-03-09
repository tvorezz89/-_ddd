package microarch.delivery.core.domain.kernel;

import libs.ddd.ValueObject;
import libs.errs.Error;
import libs.errs.Guard;
import libs.errs.Result;
import lombok.Getter;

import java.util.List;

@Getter
public class Speed extends ValueObject<Speed> {

    private Integer speed;

    private Speed() {
    }

    private Speed(Integer speed) {
        this.speed = speed;
    }

    public static Result<Speed, Error> create(Integer speed) {
        var validateSpeed = Guard.againstLessOrEqual(speed, 0, "speed");
        if (validateSpeed != null) {
            return Result.failure(validateSpeed);
        }

        return Result.success(new Speed(speed));
    }

    @Override
    protected Iterable<Object> equalityComponents() {
        return List.of(speed);
    }
}
