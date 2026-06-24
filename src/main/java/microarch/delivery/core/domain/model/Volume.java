package microarch.delivery.core.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import libs.ddd.ValueObject;
import libs.errs.Error;
import libs.errs.Guard;
import libs.errs.Result;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

@Embeddable
@Getter
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class Volume extends ValueObject<Volume> {
    private static final int MIN_VALUE = 1;

    @Column(name = "volume")
    private final int value;

    public static Result<Volume, Error> create(int value) {
        var err = Guard.againstLessThan(value, MIN_VALUE, "value");
        if (err != null) {
            return Result.failure(err);
        }

        return Result.success(new Volume(value));
    }

    public static Volume mustCreate(int value) {
        return create(value).getValueOrThrow();
    }

    public static Volume zero() {
        return new Volume(0);
    }

    public Volume add(Volume other) {
        Objects.requireNonNull(other, "other");

        return new Volume(this.value + other.value);
    }

    public boolean isLessThanOrEqualTo(Volume other) {
        Objects.requireNonNull(other, "other");

        return this.value <= other.value;
    }

    @Override
    protected Iterable<Object> equalityComponents() {
        return List.of(this.value);
    }
}
