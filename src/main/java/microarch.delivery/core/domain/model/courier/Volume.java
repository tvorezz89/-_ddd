package microarch.delivery.core.domain.model.courier;

import libs.ddd.ValueObject;
import libs.errs.Error;
import libs.errs.GeneralErrors;
import libs.errs.Result;

import java.util.List;

public class Volume extends ValueObject<Volume> {

    private Integer volume;

    private Volume() {
    };

    private Volume(Integer volume) {
        this.volume = volume;
    }

    public static Result<Volume, Error> create(Integer volume) {
        if (volume <= 0)
            return Result.failure(GeneralErrors.valueMustBeGreaterThan("totalVolume", volume, 0));
        return Result.success(new Volume(volume));
    }

    @Override
    protected Iterable<Object> equalityComponents() {
        return List.of(volume);
    }

    public Integer getVolume() {
        return this.volume;
    }
}
