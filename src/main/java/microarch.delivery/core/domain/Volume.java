package microarch.delivery.core.domain;

import libs.ddd.ValueObject;
import libs.errs.Result;
import libs.errs.Error;
import lombok.Getter;
import java.util.List;
import java.util.Objects;

@Getter
public class Volume extends ValueObject<Volume> {

    public static final int EMPTY_VOLUME = 0;

    private final Integer value;

    public static Result<Volume, Error> create(Integer value) {
        Objects.requireNonNull(value, "value");
        if (value <= EMPTY_VOLUME) {
            return Result.failure(Errors.totalVolumeShouldBePositive());
        }
        return Result.success(new Volume(value));
    }

    public static Volume mustCreate(Integer value) {
        return create(value).getValueOrThrow();
    }

    public boolean accommodates(Volume container) {
        Objects.requireNonNull(container, "container volume");
        return this.value >= container.value;
    }

    private Volume(Integer value) {
        this.value = value;
    }

    @Override
    protected Iterable<Object> equalityComponents() {
        return List.of(value);
    }

    public static final class Errors {
        public static Error totalVolumeShouldBePositive() {
            return Error.of(
                    "totalVolume.should.be.positve",
                    "Вес груза должен быть положительным"
            );
        }
    }
}