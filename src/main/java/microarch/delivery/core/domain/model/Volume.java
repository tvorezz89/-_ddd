package microarch.delivery.core.domain.model;

import jakarta.persistence.Embeddable;
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
@Embeddable
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Volume extends ValueObject<Volume> {

    private final int volume;

    public static Result<Volume, Error> create(int volume) {
        if (volume < 0)
            return Result.failure(GeneralErrors.valueMustBeGreaterThan("volume", volume, 0));

        Volume vol = new Volume(volume);

        return Result.success(vol);
    }

    @Override
    protected Iterable<Object> equalityComponents() {
        return List.of(this.volume);
    }
}
