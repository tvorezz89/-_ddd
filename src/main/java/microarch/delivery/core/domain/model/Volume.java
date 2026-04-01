package microarch.delivery.core.domain.model;

import libs.ddd.ValueObject;
import libs.errs.Error;
import libs.errs.GeneralErrors;
import libs.errs.Result;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Volume extends ValueObject<Volume> {

    private final int volume;

    public static Result<Volume, Error> create(int volume) {
        if (volume < 0 || volume > 10)
            return Result.failure(GeneralErrors.valueIsOutOfRange("volume", volume, 0, 10));

        Volume vol = new Volume(volume);

        return Result.success(vol);
    }

    @Override
    protected Iterable<Object> equalityComponents() {
        return List.of(this.volume);
    }
}
