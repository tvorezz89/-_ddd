package microarch.delivery.core.domain.kernel;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import libs.ddd.ValueObject;
import libs.errs.Error;
import libs.errs.Guard;
import libs.errs.Result;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Volume extends ValueObject<Location> {

    @Column(name = "volume")
    private Integer volume;

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
