package microarch.delivery.core.domain.model.order;

import libs.ddd.ValueObject;
import libs.errs.Error;
import libs.errs.Guard;
import lombok.Getter;

import java.util.List;

@Getter
public class Volume extends ValueObject<Volume> {
    private final int value;

    private Volume(int value) {
        this.value = value;
    }

    public static Volume of(int value) {
        Error.throwIf(Guard.againstLessOrEqual(value, 0, "value"));
        return new Volume(value);
    }

    @Override
    protected Iterable<Object> equalityComponents() {
        return List.of(value);
    }
}
