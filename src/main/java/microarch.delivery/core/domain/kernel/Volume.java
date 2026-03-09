package microarch.delivery.core.domain.kernel;

import libs.ddd.ValueObject;
import libs.errs.Error;
import libs.errs.Guard;
import libs.errs.Result;
import lombok.Getter;

import java.util.List;

@Getter
public class Volume extends ValueObject<Location> {

    private Integer volume;

    private Volume() {
    }

    private Volume(Integer volume) {
        this.volume = volume;
    }

    public static Result<Volume, Error> create(Integer volume) {
        var validateVolume = Guard.againstLessOrEqual(volume, 0, "volume");
        if (validateVolume != null) {
            return Result.failure(validateVolume);
        }
        return Result.success(new Volume(volume));
    }

    public static Volume getCreated(Integer volume) {
        return create(volume).getValue();
    }

    @Override
    protected Iterable<Object> equalityComponents() {
        return List.of(volume);
    }
}
